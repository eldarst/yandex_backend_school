package com.yandex.yandexmarket.service;

import com.yandex.yandexmarket.dao.EntityRepository;
import com.yandex.yandexmarket.entity.Entity;
import com.yandex.yandexmarket.entity.EntityType;
import com.yandex.yandexmarket.exception.InvalidEnterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class EntityServiceImpl implements EntityService {
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

        if (entity.getParent() != null)
            updateParent(entity.getParent(), entity.getDate());
    }

    @Override
    public Entity read(UUID uuid) {
        Optional<Entity> entity = entityRepository.findById(uuid);
        return entity.orElse(null);
    }

    @Override
    public boolean update(Entity entity, UUID uuid) {
        if (!entity.valid()) throw new InvalidEnterException("");
        try {
            entityRepository.save(entity);
            if (entity.getParent() != null)
                updateParent(entity.getParent(), entity.getDate());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    @Override
    public boolean delete(UUID uuid) {

        Entity entity = this.read(uuid);
        if (entity != null)
        {
            if (entity.getParentId() != null) {
                Entity parent = entity.getParent();
                parent.getChildren().remove(entity);
                entityRepository.save(parent);
                entityRepository.delete(entity);
            }
            else {
                entityRepository.delete(entity);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<Entity> sales(String stringDate) throws ParseException {
        Date date = formatter.parse(stringDate);
        if (date == null) return null;

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -1);
        Date dayBefore = c.getTime();

        return entityRepository.getEntityByDateBetweenAndType(dayBefore, date, EntityType.OFFER);
    }

    private void updateParent(Entity entity, Date date) {
        if (date != null) {
            if (date.after(entity.getDate()))
                entity.setDate(date);
        }

        entityRepository.save(entity);
        if (entity.getParent() != null) {
            Entity parent = entity.getParent();
            updateParent(parent, date);
        }
    }

}
