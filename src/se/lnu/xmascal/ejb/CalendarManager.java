package se.lnu.xmascal.ejb;

import se.lnu.xmascal.model.Calendar;
import se.lnu.xmascal.model.Window;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

/**
 * A stateless Enterprise JavaBean used for database transactions involving the <code>Admin</code> entity class.
 *
 * @author Jerry Strand
 */
@Stateless
public class CalendarManager implements Serializable {
    private static final long serialVersionUID = 1L;

    @PersistenceContext(name = "xmascal-pu")
    private EntityManager em;

    public CalendarManager() {
    }

    /**
     * @return a <code>List</code> containing all <code>Calendars</code>s in the database
     */
    public List<Calendar> getAllCalendars() {
        TypedQuery<Calendar> theQuery = em.createQuery("SELECT c FROM Calendar c", Calendar.class);
        return theQuery.getResultList();
    }

    /**
     * @param name the <code>Calendar</code> to retrieve from the database
     * @return the <code>Calendar</code>with the given name, or <code>null</code> if no such <code>Calendar</code> exists
     */
    public Calendar getCalendar(String name) {
        return em.find(Calendar.class, name);
    }

    /**
     * @param cal the <code>Calendar</code> to add to the database
     */
    public void add(Calendar cal) {
        em.persist(cal);
    }

    /**
     * @param window the <code>Window</code> to add to the database
     * @return
     */
    public void addWindow(Window window) {
        em.persist(window);
    }

    /**
     * Updates the given <code>Calendar</code> and all its <code>Window</code>s.<br />
     * <b>Note that if any of the <code>Window</code>s of this <code>Calendar</code> has not previously been added to
     * the database with addWindow(Window), then this method will throw an exception.</b>
     *
     * @param cal the <code>Calendar</code> to update
     * @return the updated <code>Calendar</code>
     */
    public Calendar update(Calendar cal) {
        return em.merge(cal); // TODO: How efficient is this? If some byte[] has not been changed, will it still be processed?
    }

    /**
     * @param cal the <code>Calendar</code> to be removed. This entity needs to be attached
     */
    public void remove(Calendar cal) {
        em.remove(cal);
    }

    /**
     * @param name the name of the <code>Calendar</code> to check for
     * @return <code>true</code> if a <code>Calendar</code> with the given name exists
     */
    public boolean exists(String name) {
        Query query = em.createQuery("SELECT c.name FROM Calendar c WHERE c.name = :cname");
        query.setParameter("cname", name).setMaxResults(1);
        return query.getResultList().size() != 0;
    }

    /**
     * @return
     */
    public List<String> getAllCalendarNames() {
        TypedQuery<String> theQuery = em.createQuery("SELECT c.name FROM Calendar c", String.class);
        return theQuery.getResultList();
    }
}
