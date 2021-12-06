package cz.muni.fi.pv217.narcos.record.resource;

import cz.muni.fi.pv217.narcos.record.entity.Record;
import cz.muni.fi.pv217.narcos.record.repository.RecordRepository;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@Path("/records")
public class RecordResource {

    private static final Logger LOG = Logger.getLogger(RecordResource.class);

    @Inject
    RecordRepository repository;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("pharmacy/{id}")
    @RolesAllowed({"Admin", "User"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMedicineInPharmacy(@PathParam("id") Long id) {
        LOG.infof("Getting all medicine in pharmacy with id \"%d\"", id);

        return Response.ok(repository.findAllMedicineInPharmacy(id).collect(Collectors.toList())).build();
    }

    @GET
    @Path("medicine/{id}")
    @RolesAllowed({"Admin", "User"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPharmaciesWithMedicine(@PathParam("id") Long id) {
        LOG.infof("Getting all pharmacies with medicine with id \"%d\"", id);

        return Response.ok(repository.findAllPharmaciesWithMedicine(id).collect(Collectors.toList())).build();
    }

    @GET
    @Path("pharmacy/{pharmacyId}/medicine/{medicineId}")
    @RolesAllowed({"Admin", "User"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecordOfMedicineFromPharmacy(@PathParam("pharmacyId") Long pharmacyId,
                                                  @PathParam("medicineId") Long medicineId) {
        LOG.infof("Getting medicine with id \"%d\" from pharmacy with id \"%d\"", medicineId, pharmacyId);

        return Response.ok(repository.findRecordOfMedicineInPharmacy(pharmacyId, medicineId)).build();
    }

    @PUT
    @Path("order")
    @RolesAllowed("Admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public Response orderMedicine(Record record) {
        LOG.infof("Getting medicine with id \"%d\" from pharmacy with id \"%d\"",
                record.medicineId, record.pharmacyId);

        Record persistedRecord = repository.findRecordOfMedicineInPharmacy(record.pharmacyId, record.medicineId);
        if (validateIfNull(persistedRecord)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (validateRecordAmountIfNegative(record.amount)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (persistedRecord.amount < record.amount) {
            LOG.error("Requested amount is not available at the moment.");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        persistedRecord.amount -= record.amount;
        repository.persist(persistedRecord);

        return Response.ok("Order was successful!").build();
    }

    @PUT
    @Path("resupply")
    @RolesAllowed("Admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response resupplyMedicine(Record record) {
        LOG.infof("Getting medicine with id \"%d\" from pharmacy with id \"%d\"",
                record.medicineId, record.pharmacyId);

        Record persistedRecord = repository.findRecordOfMedicineInPharmacy(record.pharmacyId, record.medicineId);
        if (validateIfNull(persistedRecord)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (validateRecordAmountIfNegative(record.amount)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        persistedRecord.amount += record.amount;
        repository.persist(persistedRecord);

        return Response.ok(persistedRecord).build();
    }

    @POST
    @RolesAllowed("Admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createRecord(Record record) {
        LOG.info("Creating new record");

        Record persistedRecord = repository.findRecordOfMedicineInPharmacy(record.pharmacyId, record.medicineId);

        if (persistedRecord != null) {
            LOG.error("Record already exist.");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (validateRecordAmountIfNegative(record.amount)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        repository.persist(record);

        return Response.ok(record).build();
    }

    @DELETE
    @Path("pharmacy/{pharmacyId}/medicine/{medicineId}")
    @RolesAllowed("Admin")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public Response deleteOrder(@PathParam("pharmacyId") Long pharmacyId, @PathParam("medicineId") Long medicineId) {
        LOG.infof("Deleting record in pharmacy with id \"%d\" for medicine with id \"%d\"",
                pharmacyId, medicineId);

        Record persistedRecord = repository.findRecordOfMedicineInPharmacy(pharmacyId, medicineId);

        if (validateIfNull(persistedRecord)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        repository.delete(persistedRecord);

        return Response.ok(String.format("Deleted record in pharmacy with id \"%d\" for medicine with id \"%d\"",
                pharmacyId, medicineId)).build();
    }

    private boolean validateIfNull(Record record) {
        if (record == null) {
            LOG.error("Record does not exist");
            return true;
        }
        return false;
    }

    private boolean validateRecordAmountIfNegative(int amount) {
        if (amount < 0) {
            LOG.error("Requested amount cannot be negative number.");
            return true;
        }
        return false;
    }
}