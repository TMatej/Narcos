package org.acme.orders.dtos;

import java.util.List;

public class CreateOrderDto {
    private Long userId;
    private List<CreateOrderItemDto> items;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<CreateOrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<CreateOrderItemDto> items) {
        this.items = items;
    }
}
