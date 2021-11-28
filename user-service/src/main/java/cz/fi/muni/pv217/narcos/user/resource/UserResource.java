package cz.fi.muni.pv217.narcos.user.resource;

import cz.fi.muni.pv217.narcos.user.DTOs.UserDTO;
import cz.fi.muni.pv217.narcos.user.entity.Person;
import cz.fi.muni.pv217.narcos.user.repository.PersonRepository;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author Matej Turek
 */
@Path("/users")
public class UserResource {
    @Inject
    PersonRepository personRepository;

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
