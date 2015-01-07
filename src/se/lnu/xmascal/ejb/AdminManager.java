package se.lnu.xmascal.ejb;

import se.lnu.xmascal.model.Admin;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

/**
 * A stateless Enterprise JavaBean used for database transactions involving the <code>Admin</code> entity class.
 *
 * @author Jerry Strand
 */

@Stateless
public class AdminManager implements Serializable {
    private static final long serialVersionUID = 1L;

    @PersistenceContext(name = "xmascal-pu")
    private EntityManager em;

    public AdminManager() {
    }

    /**
     * @return a <code>List</code> containing all <code>Admin</code>s in the database
     */
    public List<Admin> getAllAdmins() {
        TypedQuery<Admin> theQuery = em.createQuery("SELECT c FROM Admin c", Admin.class);
        return theQuery.getResultList();
    }

    /**
     * @param username the username of the <code>Admin</code> to retrieve
     * @return the <code>Admin</code> with the given username, or <code>null</code> if no such <code>Admin</code> exists
     */
    public Admin getAdmin(String username) {
        return em.find(Admin.class, username);
    }

}
