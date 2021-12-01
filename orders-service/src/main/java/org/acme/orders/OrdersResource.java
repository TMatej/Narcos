package org.acme.orders;

import org.acme.orders.dtos.CreateOrderDto;
import org.acme.orders.dtos.CreateOrderItemDto;
import org.acme.orders.dtos.OrderDto;
import org.acme.orders.dtos.OrderItemDto;
import org.acme.orders.entities.Order;
import org.acme.orders.entities.OrderItem;
import org.acme.orders.entities.OrderStatus;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrdersResource {
    @GET
    public List<OrderDto> getAllOrders(@QueryParam("userId") Long userId) {
        List<Order> orders;
        if (userId != null) {
            orders = Order.list("userId", userId);
        } else {
            orders = Order.listAll();
        }
        return orders.stream().map(c -> orderEntityToDto(c))
                .collect(Collectors.toList());
    }

    @GET
    @Path("{id}")
    public Response getOrderById(@PathParam("id") Long id) {
        Optional<Order> maybeOrder = Order.findByIdOptional(id);
        if (maybeOrder.isEmpty()) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(orderEntityToDto(maybeOrder.get())).build();
    }

    @POST
    @Transactional
    @Path("new")
    @Counted(name = "createdOrders", description = "How many orders were created.")
    @Timed(name = "createdTimer", description = "How long it takes to create orders.", unit = MetricUnits.MILLISECONDS)
    public Response createOrder(CreateOrderDto order) {
        // check whether user of the order exists
        // check whether all the medications exist
        // check whether  there is enough medications in store
        Order orderEntity = createOrderToEntity(order);
        orderEntity.persist();
        if (orderEntity.isPersistent()) {
            // send email about the created order
            return Response.created(URI.create("/orders/" + orderEntity.id)).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @POST
    @Transactional
    @Path("{id}/update")
    public Response updateStatus(@PathParam("id") Long id, OrderStatus status) {
        Optional<Order> maybeOrder = Order.findByIdOptional(id);
        if (maybeOrder.isEmpty()) return Response.status(Response.Status.NOT_FOUND).build();
        Order order = maybeOrder.get();
        //check whether the change is valid
        order.status = status;
        return Response.ok().build();
    }

    private Order createOrderToEntity(CreateOrderDto order) {
        Order orderEntity = new Order();
        orderEntity.userId = order.getUserId();
        orderEntity.status = OrderStatus.CREATED;
        orderEntity.items = new ArrayList<>();
        for (CreateOrderItemDto item: order.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.medicationId = item.getMedicationId();
            orderItem.count = item.getCount();
            orderItem.order = orderEntity;
            orderEntity.items.add(orderItem);
        }
        return orderEntity;
    }

    private OrderDto orderEntityToDto(Order entity) {
        OrderDto dto = new OrderDto();
        dto.setId(entity.id);
        dto.setUserId(entity.userId);
        dto.setCreatedAt(entity.createdAt);
        dto.setStatus(entity.status);
        List<OrderItemDto> orderItems = new ArrayList<>();
        for (OrderItem oi: entity.items) {
            OrderItemDto oiDto = new OrderItemDto();
            oiDto.setId(oi.id);
            oiDto.setMedicationId(oi.medicationId);
            oiDto.setCount(oi.count);
            orderItems.add(oiDto);
        }
        dto.setItems(orderItems);
        return dto;
    }
}
