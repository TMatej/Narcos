package cz.muni.fi.pv217.narcos.orders;

import cz.muni.fi.pv217.narcos.orders.dtos.CreateOrderItemDto;
import cz.muni.fi.pv217.narcos.orders.dtos.OrderDto;
import cz.muni.fi.pv217.narcos.orders.dtos.OrderItemDto;
import cz.muni.fi.pv217.narcos.orders.dtos.UserDto;
import cz.muni.fi.pv217.narcos.orders.entities.Order;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import cz.muni.fi.pv217.narcos.orders.dtos.CreateOrderDto;
import cz.muni.fi.pv217.narcos.orders.entities.OrderItem;
import cz.muni.fi.pv217.narcos.orders.entities.OrderStatus;
import org.jboss.logging.Logger;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrdersResource {
    private static final Logger LOG = Logger.getLogger(OrdersResource.class);

    @ConfigProperty(name = "pv217.userService")
    String userServiceUrl;

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed({"Admin", "User"})
    public List<OrderDto> getAllOrders(@QueryParam("userId") Long userId) {
        LOG.info("Getting all orders" + (userId != null ? String.format(" from user %d", userId) : ""));
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
    @RolesAllowed({"Admin", "User"})
    @Path("{id}")
    public Response getOrderById(@PathParam("id") Long id) {
        Optional<Order> maybeOrder = Order.findByIdOptional(id);
        if (maybeOrder.isEmpty()) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(orderEntityToDto(maybeOrder.get())).build();
    }

    @POST
    @RolesAllowed({"Admin", "User"})
    @Transactional
    @Path("new")
    @Counted(name = "createdOrders", description = "How many orders were created.")
    @Timed(name = "createdTimer", description = "How long it takes to create orders.", unit = MetricUnits.MILLISECONDS)
    public Response createOrder(CreateOrderDto order) {
        LOG.info(String.format("Creating order from user %d to pharmacy %d", order.getUserId(), order.getPharmacyId()));
        // check whether user of the order exists
        if (!checkUserExists(order.getUserId())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("User does not exist").build();
        }
        // check whether the pharmacy exists
        // check whether all the medications exist
        // check whether there is enough medications in store
        Order orderEntity = createOrderToEntity(order);
        orderEntity.persist();
        if (orderEntity.isPersistent()) {
            return Response.created(URI.create("/orders/" + orderEntity.id)).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @PUT
    @RolesAllowed({"Admin", "User"})
    @Transactional
    @Path("{id}")
    public Response updateStatus(@PathParam("id") Long id, OrderStatus status) {
        Optional<Order> maybeOrder = Order.findByIdOptional(id);
        if (maybeOrder.isEmpty()) return Response.status(Response.Status.NOT_FOUND).build();
        Order order = maybeOrder.get();
        //check whether the change is valid
        order.status = status;
        return Response.ok().build();
    }

    private boolean checkUserExists(Long userId) {
        LOG.info(String.format("Jwt token: %s", jwt.getRawToken()));
        LOG.info(String.format("Adress: %s", userServiceUrl + "/users/" + userId));
        UserDto user = ClientBuilder.newClient()
                .target( userServiceUrl + "/users/" + userId)
                .request()
                .header("Authorization", "Bearer " + jwt.getRawToken())
                .get()
                .readEntity(new GenericType<>(){});
        return user != null;
    }

    private Order createOrderToEntity(CreateOrderDto order) {
        Order orderEntity = new Order();
        orderEntity.userId = order.getUserId();
        orderEntity.pharmacyId = order.getPharmacyId();
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
        dto.setPharmacyId(entity.pharmacyId);
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
