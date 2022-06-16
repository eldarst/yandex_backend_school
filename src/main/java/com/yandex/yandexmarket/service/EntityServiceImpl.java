package com.yandex.yandexmarket.service;

import com.yandex.yandexmarket.entity.Entity;
import com.yandex.yandexmarket.entity.EntityType;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntityServiceImpl implements EntityService {
    private static final Map<UUID, Entity> ENTITY_REPOSITORY_MAP = new HashMap<>();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");


    @Override
    public void create(Entity entity) {
        ENTITY_REPOSITORY_MAP.put(entity.getId(), entity);

        if (entity.getParentId() != null) {
            addChild(entity);
        }
    }

    @Override
    public Entity read(UUID uuid) {
        return ENTITY_REPOSITORY_MAP.get(uuid);
    }

    @Override
    public boolean update(Entity entity, UUID uuid) {
        if (ENTITY_REPOSITORY_MAP.containsKey(uuid)) {
            Entity old = ENTITY_REPOSITORY_MAP.get(uuid);
            entity.setId(uuid);
            if (entity.getType() == EntityType.CATEGORY) {
                entity.setChildren(old.getChildren());
            }
            ENTITY_REPOSITORY_MAP.put(uuid, entity);
            if (old.getParentId() != null) {
                deleteChildFromParent(old, entity.getDate());
                addChild(entity);
            }

            return true;
        }
        return false;
    }


    @Override
    public boolean delete(UUID uuid) {
        Entity entity = ENTITY_REPOSITORY_MAP.get(uuid);

        if (entity.getChildren() != null) {
            List<UUID> children = entity.getChildren().stream()
                    .map(Entity::getId)
                    .collect(Collectors.toList());
            entity.getChildren().clear();

            for (UUID childID : children) {
                delete(childID);
            }
        }

        if (entity.getParentId() != null) {
            deleteChildFromParent(entity, null);
        }

        return ENTITY_REPOSITORY_MAP.remove(uuid) != null;
    }

    @Override
    public List<Entity> sales(String stringDate) throws ParseException {
        Date date = formatter.parse(stringDate);
        if (date == null) return null;

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -1);
        Date dayBefore = c.getTime();
        return ENTITY_REPOSITORY_MAP.values().stream()
                .filter(entity -> entity.getType() == EntityType.OFFER && !(dayBefore.after(entity.getDate()) && !(date.before(entity.getDate()))))
                .collect(Collectors.toUnmodifiableList());
    }


    private void addChild(Entity entity) {
        Entity parent = ENTITY_REPOSITORY_MAP.get(entity.getParentId());
        parent.addChild(entity);
        updateParent(parent, entity.getDate());
        ENTITY_REPOSITORY_MAP.put(parent.getId(), parent);
    }

    private void deleteChildFromParent(Entity entity, Date date) {
        Entity parent = ENTITY_REPOSITORY_MAP.get(entity.getParentId());
        parent.deleteChild(entity);
        updateParent(parent, date);
    }

    private void updateParent(Entity entity, Date date) {
        if (date != null) {
            if (date.after(entity.getDate()))
                entity.setDate(date);
        }

        ENTITY_REPOSITORY_MAP.put(entity.getId(), entity);
        if (entity.getParentId() != null) {
            Entity parent = ENTITY_REPOSITORY_MAP.get(entity.getParentId());
            updateParent(parent, date);
        }
    }

}
