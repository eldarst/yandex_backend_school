package com.yandex.yandexmarket.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.Id;
import java.util.*;
import java.util.stream.Collectors;


@Data
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class Entity {
    @Id
    @NonNull
    private UUID id;

    @NonNull
    private String name;

    @NonNull
    private EntityType type;

    @Nullable
    @Getter(AccessLevel.NONE)
    private int price;

    @Nullable
    private UUID parentId;

    @NonNull
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date date;

    @Nullable
    private List<Entity> children;

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    private double sum = 0;

    @JsonIgnore
    private int childCount = 0;

    public int getPrice() {
        if (children == null) return price;
        return children.size() > 0 ? (int) (sum / childCount) : (int) sum;
    }

    public double getSum() {
        return sum == 0 ? price : sum;
    }

    public void addChild(Entity entity) {
        if (children == null) children = new ArrayList<>();
        children.add(entity);
    }

    public void deleteChild(Entity entity) {
        if (children == null) return;
        List<Entity> list = children.stream()
                .filter(x -> x.id.equals(entity.id))
                .collect(Collectors.toCollection(ArrayList::new));
        list.forEach(children::remove);
    }

}
