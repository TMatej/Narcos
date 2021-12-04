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
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.List;

@Path("/pharmacies")
public class PharmacyResource {

    private static final Logger LOG = Logger.getLogger(PharmacyResource.class);

    @Inject
    PharmacyRepository repository;

    @Inject
    JsonWebToken jwt;

    @GET
    @Path("{id}")
    @RolesAllowed({"Admin", "User"})
    @Produces(MediaType.APPLICATION_JSON)
    public Pharmacy getPharmacyById(@PathParam("id") Long id) {
        LOG.infof("Getting user with id %d", id);

        Pharmacy pharmacy = repository.findById(id);

        if (pharmacy == null) {
            LOG.errorf("Pharmacy with id %d does not exit.", id);
            throw new WebApplicationException(403);
        }

        return pharmacy;
    }

    @GET
    @RolesAllowed({"Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    public List<Pharmacy> getPharmacies(@QueryParam("id") String ids) {
        LOG.infof("Getting multiple users with ids %d.", ids);
        Stream<Pharmacy> stream;
        if (ids != null) {
            try {
                Set<Long> idsSet = Arrays
                        .stream(ids.split(","))
                        .map(Long::decode)
                        .collect(Collectors.toSet());
                stream = repository.findByIds(idsSet);
            } catch (NumberFormatException ex) {
                throw new WebApplicationException(404);
            }
        } else {
            stream = repository.streamAll();
        }

        return stream.collect(Collectors.toList());
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({"Admin"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Pharmacy updatePharmacyById(@PathParam("id") Long id, Pharmacy pharmacy) {
        LOG.infof("Updating pharmacy with id %d", id);

        Pharmacy persistedPharmacy = repository.findById(id);

        if (persistedPharmacy == null) {
            LOG.errorf("Pharmacy with id %d does not exist", id);
            throw new WebApplicationException(403);
        }

        persistedPharmacy.city = pharmacy.city;
        persistedPharmacy.name = pharmacy.name;
        persistedPharmacy.street = pharmacy.street;
        persistedPharmacy.streetNumber = pharmacy.streetNumber;

        LOG.infof("Pharmacy with id %d updated.", id);

        return persistedPharmacy;
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"Admin"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String deletePharmacyById(@PathParam("id") Long id) {
        LOG.infof("Deleting use with id %d.", id);
        Pharmacy pharmacy = repository.findById(id);

        if (pharmacy == null) {
            LOG.errorf("User with id %d does not exist.");
            throw new WebApplicationException(403);
        }

        repository.delete(pharmacy);

        return String.format("User with id %d deleted.", id);
    }
}
