package com.yandex.yandexmarket.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@javax.persistence.Entity
@Table(name = "Entities")
public class Entity {
    @Id
    @Column(name = "id", nullable = false)
    @Type(type="uuid-char")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private EntityType type;

    @Nullable
    @Getter(AccessLevel.NONE)
    @Column(name = "price", nullable = false)
    private int price;

    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Entity parent;

    @Type(type="uuid-char")
    @Column(name = "parent_name")
    private UUID parentId;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @Column(name = "date", nullable = false)
    private Date date;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(AccessLevel.NONE)
    @ToString.Exclude
    private List<Entity> children;

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Column(name = "child_count", nullable = false)
    private int childCount = 0;


    public List<Entity> getChildren() {
        if (children.size() == 0) return null;

        return children;
    }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Entity entity = (Entity) o;
        return id != null && Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
