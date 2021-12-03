package cz.fi.muni.pv217.narcos.user.resource;

import cz.fi.muni.pv217.narcos.user.DTOs.UserDTO;
import cz.fi.muni.pv217.narcos.user.DTOs.UserUpdateDTO;
import cz.fi.muni.pv217.narcos.user.entity.Person;
import cz.fi.muni.pv217.narcos.user.repository.PersonRepository;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Matej Turek
 */
@Path("/users")
public class UserResource {

    private static final Logger LOG = Logger.getLogger(UserResource.class);

    @Inject
    PersonRepository personRepository;
    @Inject
    JsonWebToken jwt;

    /**
     * Endpoint for requesting specific user by their id.
     *
     * @param id path parameter representing the id of specific user
     * @return requested user by id.
     */
    @GET
    @Path("{id}")
    @RolesAllowed({"Admin", "User"})
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getUserById(@PathParam("id") Long id) {
        LOG.info(String.format("Starting procedure get user with id %d.", id));
        check(id);
        Person person = personRepository.findById(id);

        if (person == null) {
            LOG.error(String.format("User with id %d doesn't exists.", id));
            throw new WebApplicationException(403);
        }

        return personToUserDTO(person);
    }

    /**
     * Endpoint for requesting list of specific users by providing their ids.
     *
     * @param ids - query parameter containing users ids divided by comma.
     * @return list of requested users.
     */
    @GET
    @RolesAllowed({"Admin"})
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserDTO> getUsers(@QueryParam("id") String ids) {
        LOG.info(String.format("Starting procedure get multiple users with ids %s.", ids));
        Stream<Person> stream;
        if (ids != null) {
            try {
                Set<Long> idsSet = Arrays
                        .stream(ids.split(","))
                        .map(Long::decode)
                        .collect(Collectors.toSet());
                stream = personRepository.streamByIds(idsSet);
            } catch (NumberFormatException e) {
                throw new WebApplicationException(404);
            }
        } else {
            stream = Person.streamAll();
        }

        return stream
                .map(this::personToUserDTO)
                .collect(Collectors.toList());
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({"Admin", "User"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String updateUserById(@PathParam("id") Long id, UserUpdateDTO userUpdateDTO) {
        LOG.info(String.format("Starting procedure update user with id %d.", id));
        check(id);
        Person person = personRepository.findById(id);

        if (person == null) {
            throw new WebApplicationException(403);
        }

        // map all fields from the user parameter to the existing entity
        person.email = userUpdateDTO.email;
        person.password = userUpdateDTO.password;
        person.firstName = userUpdateDTO.firstname;
        person.lastName = userUpdateDTO.lastName;

        LOG.info(String.format("User with id %d updated!", person.id));

        return String.format("User with id %d updated!", person.id);
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"Admin", "User"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String deleteUserById(@PathParam("id") Long id) {
        LOG.info(String.format("Starting procedure delete user with id %d.", id));
        check(id);
        Person person = personRepository.findById(id);

        if (person == null) {
            throw new WebApplicationException(403);
        }
        personRepository.delete(person);

        return String.format("User with id %d deleted!", id);
    }

    /**
     * Function for mapping Person to UserDTO.
     *
     * @param person - object to be used for mapping
     * @return new user from person
     */
    private UserDTO personToUserDTO(Person person) {
        UserDTO user = new UserDTO();
        user.id = person.id;
        user.firstname = person.firstName;
        user.lastName = person.lastName;
        user.email = person.email;
        return user;
    }

    private void check(Long id) {
        LOG.info("Verifying user access rights.");

        if (!Long.decode(jwt.getSubject()).equals(id) &&
                !jwt.getGroups().contains("Admin")) {
            LOG.error("Verification failed.");
            throw new WebApplicationException(403);
        }
    }
}
