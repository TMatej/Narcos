package cz.fi.muni.pv217.narcos.user.resource;

import cz.fi.muni.pv217.narcos.user.DTOs.LoginInformationDTO;
import cz.fi.muni.pv217.narcos.user.DTOs.RegisterInformationDTO;
import cz.fi.muni.pv217.narcos.user.entity.Person;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import cz.fi.muni.pv217.narcos.user.repository.PersonRepository;
import io.smallrye.jwt.build.Jwt;
import org.jboss.logging.Logger;

/**
 * Auth endpoint.
 *
 * @author Matej Turek
 */
@Path("/auth")
public class AuthResource {
    @Inject
    PersonRepository personRepository;

    private static final Logger LOG = Logger.getLogger(AuthResource.class);

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

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public void register(RegisterInformationDTO registerInformationDTO) {
        Person person = personRepository.findByEmail(registerInformationDTO.email);

        if (person != null) {
            throw new WebApplicationException(403);
        }

        person = new Person();
        person.email = registerInformationDTO.email;
        person.password = registerInformationDTO.password;
        person.firstName = registerInformationDTO.firstName;
        person.lastName = registerInformationDTO.lastName;

        try {
            personRepository.persist(person);
        } catch (ConstraintViolationException ex) {
            LOG.error("Failed to persist person", ex);
            throw new WebApplicationException(400);
        }
    }
}
