package com.yandex.yandexmarket.dao;

import com.yandex.yandexmarket.entity.Entity;
import com.yandex.yandexmarket.entity.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface EntityRepository extends JpaRepository<Entity, UUID> {
    List<Entity> getAllByType(EntityType type);
    Entity getById(UUID id);
    List<Entity> getEntityByDateBetweenAndType(Date start, Date end, EntityType type);
}
