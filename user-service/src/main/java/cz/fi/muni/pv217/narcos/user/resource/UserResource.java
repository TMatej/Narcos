package cz.fi.muni.pv217.narcos.user.resource;

import cz.fi.muni.pv217.narcos.user.DTOs.UserDTO;
import cz.fi.muni.pv217.narcos.user.DTOs.UserUpdateDTO;
import cz.fi.muni.pv217.narcos.user.entity.Person;
import cz.fi.muni.pv217.narcos.user.repository.PersonRepository;

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
    @Inject
    PersonRepository personRepository;

    /**
     * Endpoint for requesting specific user by their id.
     *
     * @param id path parameter representing the id of specific user
     * @return requested user by id.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserDTO getUserById(@PathParam("id") Long id) {
        Person person = personRepository.findById(id);

        if (person == null) {
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
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserDTO> getUsersById(@QueryParam("ids") String ids) {
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

    // TODO update, delete endpoints -> create two roles (admin, basic user).

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String updateUserById(@PathParam("id") Long id, UserUpdateDTO userUpdateDTO) {
        Person person = personRepository.findById(id);

        if (person == null) {
            throw new WebApplicationException(403);
        }

        // map all fields from the user parameter to the existing entity
        person.email = userUpdateDTO.email;
        person.password = userUpdateDTO.password;
        person.firstName = userUpdateDTO.firstname;
        person.lastName = userUpdateDTO.lastName;

        return String.format("User with id %d updated!", person.id);
    }

    @DELETE
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public String deleteUserById(@PathParam("id") Long id) {
        Person person = personRepository.findById(id);

        if (person == null) {
            throw new WebApplicationException(403);
        }
        personRepository.delete(person);

        return String.format("User with id %d deleted!", id);
    }

    // TODO Only admin may list/update/delete any user.
    // TODO Basic user may list/update/delete only themself.

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
}
