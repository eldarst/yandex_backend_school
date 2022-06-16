package com.yandex.yandexmarket.service;

import com.yandex.yandexmarket.entity.Entity;
import com.yandex.yandexmarket.entity.RequestObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class RequestObjectServiceImpl implements RequestObjectService{

    private EntityService entityService;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");



    @Autowired
    RequestObjectServiceImpl(EntityService entityService) {
        this.entityService = entityService;
    }


    @Override
    public void create(RequestObject requestObject) throws ParseException {
        if (requestObject.getItems() == null) return;

        Date date = formatter.parse(requestObject.getUpdateDate());
        if (date == null) return;

        for (Entity entity: requestObject.getItems()) {
            entity.setDate(date);
            if (entityService.read(entity.getId()) != null) {
                entityService.update(entity, entity.getId());
            }
            else {
                entityService.create(entity);
            }
        }
    }
}
