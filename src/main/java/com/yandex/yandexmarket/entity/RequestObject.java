package com.yandex.yandexmarket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class RequestObject implements Serializable {
    @Nullable
    private List<Entity> items;
    @NonNull
    private String updateDate;

}
