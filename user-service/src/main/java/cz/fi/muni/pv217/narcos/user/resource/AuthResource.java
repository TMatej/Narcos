package cz.fi.muni.pv217.narcos.user.resource;

import cz.fi.muni.pv217.narcos.user.DTOs.LoginInformationDTO;
import cz.fi.muni.pv217.narcos.user.entity.Person;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import cz.fi.muni.pv217.narcos.user.repository.PersonRepository;
import io.smallrye.jwt.build.Jwt;

/**
 * Auth endpoint.
 *
 * @author Matej Turek
 */
@Path("/auth")
public class AuthResource {
    @Inject
    PersonRepository personRepository;

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String login(LoginInformationDTO loginInformationDTO) {
        Person person = personRepository.findByEmail(loginInformationDTO.email);

        if (person == null) {
            throw new WebApplicationException(404);
        }

        if (!loginInformationDTO.password.equals(person.password)) {
            throw new WebApplicationException(401);
        }

        return Jwt.subject(person.id.toString()).sign();
    }
}
