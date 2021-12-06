package cz.muni.fi.pv217.narcos.user.entity;

/**
 * @author Matej Turek
 */
public enum Role {
    Admin, User;

    public String toString() {
        switch (this) {
            case Admin:
                return "Admin";
            case User:
                return "User";
            default:
                return null;
        }
    }
}
