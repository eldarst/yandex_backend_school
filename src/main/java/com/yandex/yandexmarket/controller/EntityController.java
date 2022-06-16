package com.yandex.yandexmarket.controller;

import com.yandex.yandexmarket.entity.Entity;
import com.yandex.yandexmarket.entity.RequestObject;
import com.yandex.yandexmarket.service.EntityService;
import com.yandex.yandexmarket.service.RequestObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.UUID;


@RestController
@RequestMapping("/components/schemas")
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

    @PostMapping(value = "/ShopUnitImportRequest")
    public ResponseEntity<?> create(@Valid @RequestBody RequestObject requestObject) {
        try {
            requestObjectService.create(requestObject);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/nodes/{id}")
    public ResponseEntity<Entity> read(@PathVariable(name = "id") String id) {
        Entity entity = entityService.read(UUID.fromString(id));

        return entity != null
                ? new ResponseEntity<>(entity, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") UUID id) {
        final boolean deleted = entityService.delete(id);

        return deleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }
}
