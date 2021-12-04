package org.acme.orders.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class OrderItem extends PanacheEntity {
    public Long medicationId;
    public Integer count;

    @ManyToOne(optional = false)
    public Order order;
}
