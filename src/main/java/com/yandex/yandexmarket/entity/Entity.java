package com.yandex.yandexmarket.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import java.util.*;
import java.util.stream.Collectors;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Entity {
    @Id
    @NotEmpty(message = "Not valid")
    private UUID id;

    @NotEmpty(message = "Not valid")
    private String name;

    @NotEmpty(message = "Not valid")
    private EntityType type;

    @Nullable
    @Getter(AccessLevel.NONE)
    private int price;

    @Nullable
    private UUID parentId;

    @NotEmpty(message = "Not valid")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date date;

    @Nullable
    private List<Entity> children;

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    private int childCount = 0;

    public int getPrice() {
        if (type == EntityType.OFFER) return price;

        if (children == null) children = new ArrayList<>();

        return (int) getAverage();
    }

    public int getChildCount() {
        if (children == null) return 0;

        return children.size();
    }

    private double getAverage() {
        double sum = 0, q = 0;
        if (children.size() == 0) return 0;

        for (Entity child: children) {
            if (child.type == EntityType.CATEGORY) {
                sum += child.getAverage() * child.getChildCount();
                q += child.getChildCount();
            } else
            {
                sum += child.price;
                q++;
            }
        }
        if (q == 0) return 0;

        return sum / q;
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

    public boolean valid() {
        if (this.id == null || this.name == null || this.type == null || this.date == null) {
            return false;
        }
        return true;
    }

}
