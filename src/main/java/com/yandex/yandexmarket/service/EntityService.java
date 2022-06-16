package com.yandex.yandexmarket.service;


import com.yandex.yandexmarket.entity.Entity;

import java.text.ParseException;
import java.util.List;
import java.util.UUID;

public interface EntityService {
    /**
     * Создает новую категорию/товар
     * @param entity - объект для создания
     */
    void create(Entity entity);

    /**
     * Возвращает категорию/товар по его ID
     * @param uuid - uuid объекта
     * @return - объект с заданным ID
     */
    Entity read(UUID uuid);

    /**
     * Обновляет объект с заданным uuid,
     * в соответствии с переданным объектом
     * @param entity - объект в соответсвии с которым нужно обновить данные
     * @param uuid - uuid объекта который нужно обновить
     * @return - true если данные были обновлены, иначе false
     */
    boolean update(Entity entity, UUID uuid);

    /**
     * Удаляет объект с заданным uuid
     * @param uuid - uuid объекта, который нужно удалить
     * @return - true если объект был удален, иначе false
     */
    boolean delete(UUID uuid);

    /**
     * Возвращает товары цена которых была обновлена за последние 24 часа включительно
     * @param data - Дата и время запроса
     * @return - Актуальные товары
     */
    List<Entity> sales(String data) throws ParseException;
}
