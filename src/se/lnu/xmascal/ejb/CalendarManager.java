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
import java.util.NoSuchElementException;

/**
 * A stateless Enterprise JavaBean used for database transactions involving the <code>Admin</code> entity class.
 *
 * @author Jerry Strand
 * @author Johan Widen
 */
@Stateless
public class CalendarManager implements Serializable {
    private static final long serialVersionUID = 1L;

    @PersistenceContext(name = "xmascal-pu")
    private EntityManager em;

    public CalendarManager() {
    }

    /**
     * @param window the <code>Window</code> to add to the database
     */
    public void addWindow(Window window) {
        em.persist(window);
    }

    /**
     * Updates the window.
     * @author Johan, the rest is Jerry! ;)
     * @param window the <code>Window</code> to update in the database
     */
    public void updateWindow(Window window) {
        em.merge(window);
    }

    /**
     * @return a <code>List</code> containing all <code>Calendars</code>s in the database
     */
    public List<Calendar> getAllCalendars() {
        TypedQuery<Calendar> theQuery = em.createQuery("SELECT c FROM Calendar c", Calendar.class);
        return theQuery.getResultList();
    }

    /**
     * @param name the name of the <code>Calendar</code> to retrieve from the database
     * @return the <code>Calendar</code>with the given name, or <code>null</code> if no such <code>Calendar</code> exists
     */
    public Calendar getCalendar(String name) {
        return em.find(Calendar.class, name);
    }

    /**
     * @param numericId the numeric ID of the <code>Calendar</code> to retrieve from the database
     * @return the <code>Calendar</code>with the given numeric ID, or <code>null</code> if no such <code>Calendar</code>
     * exists
     */
    public Calendar getCalendar(long numericId) {
        TypedQuery<Calendar> q = em.createQuery("SELECT c FROM Calendar c WHERE c.numericId = :cname", Calendar.class);
        q.setParameter("cname", numericId).setMaxResults(1);
        return q.getSingleResult();
    }

    /**
     * @param numericId the numeric ID of the <code>Calendar</code> whose name shall be retrieved
     * @return the name of the <code>Calendar</code> with the given numeric ID
     */
    public String getName(long numericId) {
        TypedQuery<String> q = em.createQuery("SELECT c.name FROM Calendar c WHERE c.numericId = :id", String.class);
        q.setParameter("id", numericId).setMaxResults(1);
        return q.getSingleResult();
    }

    /**
     * @param numericId the numeric ID of the <code>Calendar</code> to rename
     * @param newName the new name that the <code>Calendar</code> shall receive
     * @throws UnsupportedOperationException if a <code>Calendar</code> with the given ID does not exist, or if the
     * <code>Calendar</code> with the given numeric ID cannot be renamed.
     */
    public void renameCalendar(long numericId, String newName) throws UnsupportedOperationException {
        if (exists(newName)) {
            throw new UnsupportedOperationException("A calendar with the name '" + newName + "' already exists");
        }
        try {
            em.createQuery("UPDATE Calendar c SET c.name = :newName WHERE c.numericId = :id")
                    .setParameter("newName", newName)
                    .setParameter("id", numericId)
                    .executeUpdate();
        } catch (Exception e) {
            throw new UnsupportedOperationException(
                    "Unable to rename calendar with numeric id " + numericId + " to '" + newName + "'", e);
        }
    }

    /**
     * @param cal the <code>Calendar</code> to add to the database
     */
    public void add(Calendar cal) {
        em.persist(cal);
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
        return em.merge(cal);
    }

    /**
     * @param cal the <code>Calendar</code> to be removed. This entity needs to be attached
     */
    public void remove(Calendar cal) {
        /*
         * Checks if the entity is managed by EntityManager#contains() and if not, then make it managed by
         * EntityManager#merge() //Johan
         */
        em.remove(em.contains(cal) ? cal : em.merge(cal));
    }

    /**
     * @param name the name of the <code>Calendar</code> to determine the existence of
     * @return <code>true</code> if a <code>Calendar</code> with the given name exists
     */
    public boolean exists(String name) {
        Query query = em.createQuery("SELECT c.name FROM Calendar c WHERE c.name = :cname");
        query.setParameter("cname", name).setMaxResults(1);
        return query.getResultList().size() != 0;
    }

    /**
     * @return a <code>List</code> of the names of all <code>Calendars</code> in the database
     */
    public List<String> getAllCalendarNames() {
        TypedQuery<String> theQuery = em.createQuery("SELECT c.name FROM Calendar c", String.class);
        return theQuery.getResultList();
    }

    /**
     * @return a <code>List</code> of the numeric IDs of all <code>Calendars</code> in the database
     */
    public List<Long> getAllCalendarIds() {
        TypedQuery<Long> theQuery = em.createQuery("SELECT c.numericId FROM Calendar c", Long.class);
        return theQuery.getResultList();
    }
}
