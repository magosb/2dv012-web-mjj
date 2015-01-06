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

}
