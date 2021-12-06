package cz.muni.fi.pv217.narcos.pharmacy.resource;

import cz.muni.fi.pv217.narcos.pharmacy.entity.Pharmacy;
import cz.muni.fi.pv217.narcos.pharmacy.repository.PharmacyRepository;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/pharmacies")
public class PharmacyResource {

    private static final Logger LOG = Logger.getLogger(PharmacyResource.class);

    @Inject
    PharmacyRepository repository;

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed({"Admin", "User"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPharmacies() {
        LOG.info("Getting all pharmacies.");

        return Response.ok(repository.listAll()).build();
    }

    @POST
    @RolesAllowed({"Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createPharmacy(Pharmacy pharmacy) {
        LOG.info("Creating new pharmacy.");

        repository.persist(pharmacy);

        return Response.ok(pharmacy).build();
    }

    @GET
    @Path("{id}")
    @RolesAllowed({"Admin", "User"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPharmacyById(@PathParam("id") Long id) {
        LOG.infof("Getting pharmacy with id %d", id);

        Pharmacy pharmacy = repository.findById(id);

        if (pharmacy == null) {
            LOG.errorf("Pharmacy with id %d does not exit.", id);
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(pharmacy).build();
    }

    @GET
    @RolesAllowed({"Admin", "User"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPharmacies(@QueryParam("id") String ids) {
        LOG.infof("Getting multiple pharmacies with ids %d.", ids);
        Stream<Pharmacy> stream;
        if (ids != null) {
            try {
                Set<Long> idsSet = Arrays
                        .stream(ids.split(","))
                        .map(Long::decode)
                        .collect(Collectors.toSet());
                stream = repository.findByIds(idsSet);
            } catch (NumberFormatException ex) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } else {
            stream = repository.streamAll();
        }

        return Response.ok(stream.collect(Collectors.toList())).build();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({"Admin"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updatePharmacyById(@PathParam("id") Long id, Pharmacy pharmacy) {
        LOG.infof("Updating pharmacy with id %d", id);

        Pharmacy persistedPharmacy = repository.findById(id);

        if (persistedPharmacy == null) {
            LOG.errorf("Pharmacy with id %d does not exist", id);
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        persistedPharmacy.city = pharmacy.city;
        persistedPharmacy.name = pharmacy.name;
        persistedPharmacy.street = pharmacy.street;
        persistedPharmacy.streetNumber = pharmacy.streetNumber;

        LOG.infof("Pharmacy with id %d updated.", id);
        repository.persist(persistedPharmacy);

        return Response.ok(persistedPharmacy).build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"Admin"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public Response deletePharmacyById(@PathParam("id") Long id) {
        LOG.infof("Deleting pharmacy with id %d.", id);
        Pharmacy pharmacy = repository.findById(id);

        if (pharmacy == null) {
            LOG.errorf("Pharmacy with id %d does not exist.");
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        repository.delete(pharmacy);

        return Response.ok(String.format("Pharmacy with id %d deleted.", id)).build();
    }
}
