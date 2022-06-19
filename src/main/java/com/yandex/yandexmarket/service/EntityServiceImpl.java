package com.yandex.yandexmarket.service;

import com.yandex.yandexmarket.dao.EntityRepository;
import com.yandex.yandexmarket.entity.Entity;
import com.yandex.yandexmarket.entity.EntityType;
import com.yandex.yandexmarket.exception.InvalidEnterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntityServiceImpl implements EntityService {
    private static final Map<UUID, Entity> ENTITY_REPOSITORY_MAP = new HashMap<>();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    EntityRepository entityRepository;

    @Autowired
    public EntityServiceImpl(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    @Override
    public void create(@Valid Entity entity) {
        if (!entity.valid()) throw new InvalidEnterException("");
        entityRepository.save(entity);
        ENTITY_REPOSITORY_MAP.put(entity.getId(), entity);

        if (entity.getParent() != null) {
            addChild(entity);
        }
    }

    @Override
    public Entity read(UUID uuid) {
        Optional<Entity> entity = entityRepository.findById(uuid);
        return entity.orElse(null);

    }

    @Override
    public boolean update(Entity entity, UUID uuid) {
        entityRepository.save(entity);
        if (ENTITY_REPOSITORY_MAP.containsKey(uuid)) {
            Entity old = ENTITY_REPOSITORY_MAP.get(uuid);
            entity.setId(uuid);
            if (entity.getType() == EntityType.CATEGORY) {
                entity.setChildren(old.getChildren());
            }
            ENTITY_REPOSITORY_MAP.put(uuid, entity);
            if (old.getParent() != null) {
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

        entityRepository.delete(entity);
        if (entity.getChildren() != null) {
            List<UUID> children = entity.getChildren().stream()
                    .map(Entity::getId)
                    .collect(Collectors.toList());

            for (UUID childID : children) {
                delete(childID);
            }
        }

        if (entity.getParent() != null) {
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
        Entity parent = entity.getParent();
        parent.addChild(entity);
        updateParent(parent, entity.getDate());
        ENTITY_REPOSITORY_MAP.put(parent.getId(), parent);
    }

    private void deleteChildFromParent(Entity entity, Date date) {
        Entity parent = entity.getParent();
        parent.deleteChild(entity);
        updateParent(parent, date);
    }

    private void updateParent(Entity entity, Date date) {
        if (date != null) {
            if (date.after(entity.getDate()))
                entity.setDate(date);
        }

        ENTITY_REPOSITORY_MAP.put(entity.getId(), entity);
        if (entity.getParent() != null) {
            Entity parent = entity.getParent();
            updateParent(parent, date);
        }
    }

}
