package cz.fi.muni.pv217.narcos.user.DTOs;

import cz.fi.muni.pv217.narcos.user.entity.Role;

/**
 * DTO representation of Person.
 *
 * @author Matej Turek
 */
public class UserDTO {
    public Long id;
    public String firstname;
    public String lastName;
    public String email;
    public Role role;
}
