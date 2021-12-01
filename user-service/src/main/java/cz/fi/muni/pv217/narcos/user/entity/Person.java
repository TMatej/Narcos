package cz.fi.muni.pv217.narcos.user.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Set;
import java.util.stream.Stream;


/**
 * Class representing a single record in db table persons.
 * Extending the PanacheEntity allows Quarkus take care about public
 * variables by creating them getters/setters therefore there is no
 * violation of the encapsulation.
 *
 * @author Matej Turek
 */
@Entity
@Table(name = "persons")
public class Person extends PanacheEntity {
    @NotBlank
    public String firstName;
    @NotBlank
    public String lastName;
    @Email
    public String email;
    @NotBlank
    public String password;
    // variable that holds information, whether the object was deleted
    public boolean stored;
}
