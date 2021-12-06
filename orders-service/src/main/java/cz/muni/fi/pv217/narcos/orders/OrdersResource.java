package cz.muni.fi.pv217.narcos.orders;

import cz.muni.fi.pv217.narcos.orders.dtos.CreateOrderDto;
import cz.muni.fi.pv217.narcos.orders.dtos.CreateOrderItemDto;
import cz.muni.fi.pv217.narcos.orders.dtos.OrderDto;
import cz.muni.fi.pv217.narcos.orders.dtos.OrderItemDto;
import cz.muni.fi.pv217.narcos.orders.dtos.RecordDto;
import cz.muni.fi.pv217.narcos.orders.entities.Order;
import cz.muni.fi.pv217.narcos.orders.entities.OrderItem;
import cz.muni.fi.pv217.narcos.orders.entities.OrderStatus;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logging.Logger;

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
import javax.ws.rs.client.Entity;
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
    private static final Logger LOG = Logger.getLogger(OrdersResource.class);

    @ConfigProperty(name = "pv217.userService")
    private String userServiceUrl;
    @ConfigProperty(name = "pv217.pharmacyService")
    private String pharmacyServiceUrl;
    @ConfigProperty(name = "pv217.medicineService")
    private String medicineServiceUrl;
    @ConfigProperty(name = "pv217.recordService")
    private String recordServiceUrl;

    @Inject
    private JsonWebToken jwt;

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
        LOG.info(String.format("Get order  with id %d", id));
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
        if (!checkUserExists(order.getUserId())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (!checkPharmacyExists(order.getPharmacyId())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        for (CreateOrderItemDto item: order.getItems()) {
            if (!checkMedicineExists(item.getMedicationId())) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            if (!checkRecord(order.getPharmacyId(), item.getMedicationId(), item.getCount())) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        Order orderEntity = createOrderToEntity(order);
        orderEntity.persist();
        if (orderEntity.isPersistent()) {
            for (CreateOrderItemDto item: order.getItems()) {
                RecordDto rec = new RecordDto();
                rec.pharmacyId = order.getPharmacyId();
                rec.medicineId = item.getMedicationId();
                rec.amount = item.getCount();
                subtractFromRecord(rec);
            }
            return Response.created(URI.create("/orders/" + orderEntity.id)).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @PUT
    @RolesAllowed({"Admin", "User"})
    @Transactional
    @Path("{id}")
    public Response updateStatus(@PathParam("id") Long id, OrderStatus status) {
        LOG.info(String.format("Updating status of order with id %d to %s", id, status.name()));
        Optional<Order> maybeOrder = Order.findByIdOptional(id);
        if (maybeOrder.isEmpty()) return Response.status(Response.Status.NOT_FOUND).build();
        Order order = maybeOrder.get();
        order.status = status;
        return Response.ok().build();
    }

    private boolean checkUserExists(Long userId) {
        int status = ClientBuilder.newClient()
                .target( userServiceUrl + "/users/" + userId)
                .request()
                .header("Authorization", "Bearer " + jwt.getRawToken())
                .get().getStatus();
        return status == 200;
    }

    private boolean checkPharmacyExists(Long pharmacyId) {
        int status = ClientBuilder.newClient()
                .target( pharmacyServiceUrl + "/pharmacies/" + pharmacyId)
                .request()
                .header("Authorization", "Bearer " + jwt.getRawToken())
                .get().getStatus();
        return status == 200;
    }

    private boolean checkMedicineExists(Long medicineId) {
        int status = ClientBuilder.newClient()
                .target( medicineServiceUrl + "/medicine/" + medicineId)
                .request()
                .header("Authorization", "Bearer " + jwt.getRawToken())
                .get().getStatus();
        return status == 200;
    }

    private boolean checkRecord(Long pharmacyId, Long medicineId, int amount) {
        Response res = ClientBuilder.newClient()
                .target( recordServiceUrl + "/records/pharmacy/" + pharmacyId + "/medicine/" + medicineId )
                .request()
                .header("Authorization", "Bearer " + jwt.getRawToken())
                .get();
        if (res.hasEntity()) {
            RecordDto record = res.readEntity(RecordDto.class);
            return record != null && record.amount >= amount;
        }
        return false;
    }

    private boolean subtractFromRecord(RecordDto record) {
        Response res = ClientBuilder.newClient()
                .target( recordServiceUrl + "/records/order")
                .request()
                .header("Authorization", "Bearer " + jwt.getRawToken())
                .put(Entity.json(record));
        return res.getStatus() == 200;
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
