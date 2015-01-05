package se.lnu.xmascal.model;

import javax.persistence.*;

/**
 * This class represents an administrator of the Christmas Calendar web application.
 *
 * @author Jerry Strand
 */
@Entity
@Table(name = "admin")
@NamedQuery(name = "Admin.findAll", query = "SELECT a FROM Admin a")
public class Admin {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    public Admin() {
    }

    /**
     * @param username The username of the admin
     * @param password The password of the admin
     */
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * @return the username of this <code>Admin</code>
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * @param username the username to set for this <code>Admin</code>
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password of this <code>Admin</code>
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @param password the password to set for this <code>Admin</code>
     */
    public void setPassword(String password) {
        this.password = password;
    }

}
