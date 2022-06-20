package com.yandex.yandexmarket.controller;

import com.yandex.yandexmarket.entity.Entity;
import com.yandex.yandexmarket.entity.RequestObject;
import com.yandex.yandexmarket.exception.InvalidEnterException;
import com.yandex.yandexmarket.service.EntityService;
import com.yandex.yandexmarket.service.RequestObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;


@RestController
public class EntityController {

    private final EntityService entityService;
    private final RequestObjectService requestObjectService;

    @Autowired
    public EntityController(EntityService entityService, RequestObjectService requestObjectService) {
        this.entityService = entityService;
        this.requestObjectService = requestObjectService;
    }

    @GetMapping(value = "/helloworld")
    public ResponseEntity<String> helloWorld() {
        return new ResponseEntity<>("Hello, Eldar!", HttpStatus.OK);
    }

    @PostMapping(value = "/imports")
    public ResponseEntity<?> create(@RequestBody RequestObject requestObject) {
        try {
            requestObjectService.create(requestObject);
        } catch (Exception e) {
            throw new InvalidEnterException("");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/nodes/{id}")
    public ResponseEntity<Object> read(@PathVariable(name = "id") String id) {
        try{
            Entity entity = entityService.read(UUID.fromString(id));
            return entity != null
                    ? new ResponseEntity<>(entity, HttpStatus.OK)
                    : new ResponseEntity<>("Item not found", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException exception){
            throw new InvalidEnterException("");
        }



    }


    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            final boolean deleted = entityService.delete(uuid);
            return deleted
                    ? new ResponseEntity<>(HttpStatus.OK)
                    : new ResponseEntity<>("Item not found", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            throw new InvalidEnterException("");
        }
    }

    @GetMapping(value = "/sales")
    public ResponseEntity<Object> sales(@RequestParam String date) {
        try {
            List<Entity> offers = entityService.sales(date);
            return new ResponseEntity<>(offers, HttpStatus.OK);
        } catch (ParseException e) {
            throw new InvalidEnterException("");
        }
    }

}
