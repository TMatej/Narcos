package cz.fi.muni.pv217.narcos.medicine.resource;

import cz.fi.muni.pv217.narcos.medicine.DTO.MedicineDTO;
import cz.fi.muni.pv217.narcos.medicine.entity.Medicine;
import cz.fi.muni.pv217.narcos.medicine.repository.MedicineRepository;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/medicine")
public class MedicineResource {

    private static final Logger LOG = Logger.getLogger(MedicineResource.class);

    @Inject
    MedicineRepository medicineRepository;

    @Inject
    JsonWebToken jwt;

    /**
     * Endpoint for requesting specific medicine by its id.
     *
     * @param id path parameter representing the id of specific medicine
     * @return requested medicine by id.
     */
    @GET
    @Path("{id}")
    @RolesAllowed({"Admin", "User"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedicineById(@PathParam("id") Long id) {
        LOG.info(String.format("Starting procedure get medicine with id %d.", id));
        Medicine medicine = medicineRepository.findById(id);

        if (medicine == null) {
            LOG.error(String.format("Medicine with id %d doesn't exists.", id));
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok(medicineToMedicineDTO(medicine)).build();
    }

    /**
     * Endpoint for requesting specific medicine by its name.
     *
     * @param name path parameter representing the name of specific medicine
     * @return requested medicine by name.
     */
    @GET
    @Path("name/{name}")
    @RolesAllowed({"Admin", "User"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedicineByName(@PathParam("name") String name) {
        LOG.info(String.format("Starting procedure get medicine with name '%s'.", name));
        Medicine medicine = medicineRepository.findByName(name);

        if (medicine == null) {
            LOG.error(String.format("Medicine with name '%s' doesn't exists.", name));
            throw new WebApplicationException(403);
        }

        return Response.ok(medicineToMedicineDTO(medicine)).build();
    }

    @POST
    @Path("{id}")
    @RolesAllowed({"Admin"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createMedicine(MedicineDTO medicineUpdateDTO) {
        LOG.info("Starting procedure create medicine");
        Medicine medicine = medicineRepository.findByName(medicineUpdateDTO.name);

        if (medicine != null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        medicine = new Medicine();
        medicine.name = medicineUpdateDTO.name;
        medicine.manufacturer = medicineUpdateDTO.manufacturer;
        medicine.form = medicineUpdateDTO.form;
        medicine.expirationDate = medicineUpdateDTO.expirationDate;
        medicine.quantity = medicineUpdateDTO.quantity;

        medicineRepository.persist(medicine);

        if (medicineRepository.isPersistent(medicine)) {
            LOG.info(String.format("Medicine '%s' created!", medicine.name));
            return Response.created(URI.create("/medicine/" + medicine.id)).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({"Admin"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateMedicineById(@PathParam("id") Long id, MedicineDTO medicineUpdateDTO) {
        LOG.info(String.format("Starting procedure update medicine with id %d.", id));
        Medicine medicine = medicineRepository.findById(id);

        if (medicine == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        medicine.name = medicineUpdateDTO.name;
        medicine.manufacturer = medicineUpdateDTO.manufacturer;
        medicine.form = medicineUpdateDTO.form;
        medicine.expirationDate = medicineUpdateDTO.expirationDate;
        medicine.quantity = medicineUpdateDTO.quantity;

        LOG.info(String.format("Medicine with id %d updated!", medicine.id));

        return Response.ok(String.format("Medicine with id %d updated!", medicine.id)).build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"Admin"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response deleteMedicineById(@PathParam("id") Long id) {
        LOG.info(String.format("Starting procedure delete medicine with id %d.", id));

        Medicine medicine = medicineRepository.findById(id);

        if (medicine == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        medicineRepository.delete(medicine);

        return Response.ok(String.format("Medicine with id %d deleted!", id)).build();
    }

    private MedicineDTO medicineToMedicineDTO(Medicine medicine) {
        MedicineDTO medicineDTO = new MedicineDTO();
        medicineDTO.id = medicine.id;
        medicineDTO.name = medicine.name;
        medicineDTO.manufacturer = medicine.manufacturer;
        medicineDTO.form = medicine.form;
        medicineDTO.expirationDate = medicine.expirationDate;
        medicineDTO.quantity = medicine.quantity;
        return medicineDTO;
    }
}