package cz.fi.muni.pv217.narcos.user.resource;

import cz.fi.muni.pv217.narcos.user.DTOs.LoginInformationDTO;
import cz.fi.muni.pv217.narcos.user.DTOs.RegisterInformationDTO;
import cz.fi.muni.pv217.narcos.user.entity.Person;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cz.fi.muni.pv217.narcos.user.repository.PersonRepository;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.HashSet;

/**
 * Auth endpoint.
 *
 * @author Matej Turek
 */
@Path("/auth")
public class AuthResource {
    @Inject
    PersonRepository personRepository;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    private static final Logger LOG = Logger.getLogger(AuthResource.class);

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(LoginInformationDTO loginInformationDTO) {
        LOG.info(String.format("Starting authentication of a user %s.", loginInformationDTO.email));
        Person person = personRepository.findByEmail(loginInformationDTO.email);

        if (person == null) {
            LOG.error(String.format("User %s not found!", loginInformationDTO.email));
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!loginInformationDTO.password.equals(person.password)) {
            LOG.error("Passwords doesn't match!");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        LOG.info("User authenticated. Generating new JWT token.");

        HashSet<String> roles = new HashSet<>(Collections.singletonList(person.role.toString()));
        String token = Jwt
                .issuer(issuer)
                .upn(loginInformationDTO.email)
                .groups(roles)
                .subject(person.id.toString())
                .sign();
        LOG.info("JWT token created!");

        return Response.ok(token).build();
    }

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public Response register(RegisterInformationDTO registerInformationDTO) {
        LOG.info("Starting registration of a new user.");
        Person person = personRepository.findByEmail(registerInformationDTO.email);

        if (person != null) {
            LOG.error(String.format("User with email %s already exists!", registerInformationDTO.email));
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        person = new Person();
        person.email = registerInformationDTO.email;
        person.password = registerInformationDTO.password;
        person.firstName = registerInformationDTO.firstName;
        person.lastName = registerInformationDTO.lastName;
        person.role = registerInformationDTO.role;

        try {
            personRepository.persist(person);
        } catch (ConstraintViolationException ex) {
            LOG.error(String.format("Failed to persist user %s", registerInformationDTO.email), ex);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        LOG.info(String.format("New user %s registered!", registerInformationDTO.email));

        return Response.ok(String.format("User with username '%s' created!", person.email)).build();
    }
}
