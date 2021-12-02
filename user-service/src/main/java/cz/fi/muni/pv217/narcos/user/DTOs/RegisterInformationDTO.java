package cz.fi.muni.pv217.narcos.user.DTOs;

import cz.fi.muni.pv217.narcos.user.entity.Role;

/**
 * @author Matej Turek
 */
public class RegisterInformationDTO {
    public String password;
    public String firstName;
    public String lastName;
    public String email;
    public Role role;
}
