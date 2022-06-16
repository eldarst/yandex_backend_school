package com.yandex.yandexmarket.service;

import com.yandex.yandexmarket.entity.Entity;
import com.yandex.yandexmarket.entity.EntityType;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntityServiceImpl implements EntityService {
    private static final Map<UUID, Entity> ENTITY_REPOSITORY_MAP = new HashMap<>();

    @Override
    public void create(Entity entity) {
        ENTITY_REPOSITORY_MAP.put(entity.getId(), entity);

        if (entity.getParentId() != null) {
            Entity parent = ENTITY_REPOSITORY_MAP.get(entity.getParentId());
            updateParent(parent, entity.getSum(), entity.getDate());
            parent.addChild(entity);
            ENTITY_REPOSITORY_MAP.put(parent.getId(), parent);
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
            if (old.getType() == EntityType.CATEGORY) {
                entity.setId(uuid);
                entity.setSum(old.getSum());
                entity.setChildCount(old.getChildCount());
                entity.setChildren(old.getChildren());
                ENTITY_REPOSITORY_MAP.put(uuid, entity);
                if (old.getParentId() != null) {
                    old.setSum(0);
                    deleteChildFromParent(old, entity.getDate());

                    Entity parent = ENTITY_REPOSITORY_MAP.get(entity.getParentId());
                    parent.addChild(entity);
                    updateParent(parent, 0, entity.getDate());
                }
            }
            else {

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

    private void deleteChildFromParent(Entity entity, Date date) {
        Entity parent = ENTITY_REPOSITORY_MAP.get(entity.getParentId());
        parent.deleteChild(entity);
        updateParent(parent, -entity.getSum(), date);
    }

    private void updateParent(Entity entity, double sum, Date date) {
        if (date != null) {
            if (date.after(entity.getDate()))
                entity.setDate(date);
        }

        entity.setSum(entity.getSum() + sum);
        entity.setChildCount(entity.getChildCount() + getIncrease(sum));
        ENTITY_REPOSITORY_MAP.put(entity.getId(), entity);
        if (entity.getParentId() != null) {
            Entity parent = ENTITY_REPOSITORY_MAP.get(entity.getParentId());
            updateParent(parent, sum, date);
        }
    }

    private int getIncrease(double sum) {
        if (sum > 0) return 1;
        else if (sum < 0) return -1;
        return 0;
    }
}
