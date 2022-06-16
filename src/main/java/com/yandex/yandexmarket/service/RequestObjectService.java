package com.yandex.yandexmarket.service;

import com.yandex.yandexmarket.entity.Entity;
import com.yandex.yandexmarket.entity.RequestObject;

import java.text.ParseException;

public interface RequestObjectService {
    /**
     * Создает новые категории/товары
     * @param requestObject - объект для создания
     */
    void create(RequestObject requestObject) throws ParseException;
}
