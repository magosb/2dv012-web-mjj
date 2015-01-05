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
public class AdminQueryBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @PersistenceContext(name = "xmascal-pu")
    EntityManager em;

    public AdminQueryBean() {
    }

    /**
     * @return a <code>List</code> containing all <code>Admin</code>s in the database
     */
    public List<Admin> getAllAdmins() {
        TypedQuery<Admin> theQuery = em.createQuery("SELECT c FROM Admin c", Admin.class);
        return theQuery.getResultList();
    }


    /**
     * @param username the username of the <code>Admin</code> to retrieve from the database
     * @return the <code>Admin</code> with the specified username
     */
    // TODO: ^ Or null if no such admin exists?
    public Admin getAdmin(String username) {
        return em.find(Admin.class, username);
    }

    /**
     * @param admin the <code>Admin</code> to add to the database
     */
    public void addAdmin(Admin admin) {
        em.persist(admin);
    }

    /**
     * @param admin the <code>Admin</code>to remove from the database
     */
    public void removeAdmin(Admin admin) {
        Admin temp = em.find(Admin.class, admin.getUsername());
        if (temp != null) {
            em.remove(temp);
        }
    }

    /**
     * @param admin the Admin to be updated in the database
     */
    public void updateAdmin(Admin admin) {
        Admin temp = em.find(Admin.class, admin.getUsername());
        if (temp != null) {
            temp.setUsername(admin.getUsername());
            temp.setPassword(admin.getPassword());
        }
    }

}
