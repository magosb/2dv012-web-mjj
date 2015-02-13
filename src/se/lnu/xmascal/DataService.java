package se.lnu.xmascal;

import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;
import se.lnu.xmascal.model.Calendar;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.Serializable;

/**
 * This class is responsible for making binary database data available in a number of ways.
 *
 * @author Jerry Strand
 */
@Named
@ApplicationScoped
public class DataService implements Serializable {
    private static final long serialVersionUID = 1L;

    @PersistenceContext(name = "xmascal-pu")
    private EntityManager em;

    public DataService() {
    }

    /**
     * When this method is called from a view that is being rendered, it returns stub data with the sole responsibility
     * of generating a valid URL.
     * When the client browser uses the generated URL to request the image, this method will check for a request
     * parameter "cal" and return the actual thumbnail data of the <code>Calendar</code> with that name.
     */
    public synchronized StreamedContent getCalendarThumbnail() {
        FacesContext context = FacesContext.getCurrentInstance();

        // View is being rendered. Return a stub StreamedContent so that it will generate right URL.
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new ByteArrayContent();
        }

        // Image is being requested. Return a real StreamedContent with the image bytes.
        else {
            try {
                long calId = Long.parseLong(context.getExternalContext().getRequestParameterMap().get("cal"));
                return new ByteArrayContent(getCalendarThumbnail(calId));
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    /**
     * @param calendarId the numeric id of the <code>Calendar</code> whose thumbnail shall be retrieved
     * @return the binary data that represents the thumbnail
     */
    private byte[] getCalendarThumbnail(long calendarId) {
        Query query = em.createQuery("SELECT c.thumbnail FROM Calendar c WHERE c.numericId = :id");
        query.setParameter("id", calendarId);
        return (byte[]) query.getSingleResult();
    }

    /**
     * When this method is called from a view that is being rendered, it returns stub data with the sole responsibility
     * of generating a valid URL.
     * When the client browser uses the generated URL to request the background picture, this method will check for a
     * request parameter "cal" and return the actual background data of the <code>Calendar</code> with that numeric ID.
     */
    public synchronized StreamedContent getCalendarBackground() {
        FacesContext context = FacesContext.getCurrentInstance();

        // View is being rendered. Return a stub StreamedContent so that it will generate right URL.
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new ByteArrayContent();
        }

        // Image is being requested. Return a real StreamedContent with the image bytes.
        else {
            try {
                long calId = Long.parseLong(context.getExternalContext().getRequestParameterMap().get("cal"));
                return new ByteArrayContent(getCalendarBackground(calId));
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    /**
     * @param calendarId the numeric id of the <code>Calendar</code> whose background picture shall be retrieved
     * @return the binary data representing the background
     */
    private byte[] getCalendarBackground(long calendarId) {
        Query query = em.createQuery("SELECT c.background FROM Calendar c WHERE c.numericId = :id");
        query.setParameter("id", calendarId);
        return (byte[]) query.getSingleResult();
    }

    /**
     * This method is used to retrieve <b>non text</b> content of a <code>Calendar Window</code>.
     * When this method is called from a view that is being rendered, it returns stub data with the sole responsibility
     * of generating a valid URL.
     * When the client browser uses the generated URL to request the content, this method will check for request
     * parameters "cal" and "day" and return the actual content of that <code>Calendar Window</code>.
     */
    public synchronized StreamedContent getStreamedContent() {
        FacesContext context = FacesContext.getCurrentInstance();

        // View is being rendered. Return a stub StreamedContent so that it will generate right URL.
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new ByteArrayContent();
        }
        // Image is being requested. Return a real StreamedContent with the image bytes.
        else {
            try {
                Long calId = Long.parseLong(context.getExternalContext().getRequestParameterMap().get("cal"));
                int winDay = Integer.parseInt(context.getExternalContext().getRequestParameterMap().get("day"));
                return new ByteArrayContent(getWindowContent(calId, winDay));
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    /**
     * This method is used to retrieve text (includes URLs) content of a <code>Calendar Window</code>.
     *
     * @param calId the numeric ID of the <code>Calendar</code> whose <code>Window</code> content to retrieve
     * @param winDay the day of the <code>Window</code> whose content to retrieve
     * @return the text content of the given <code>Window</code>
     */
    public synchronized String getTextContent(long calId, int winDay) {
        return new String(getWindowContent(calId, winDay));
    }

    /**
     * @param calendarId the numeric id of the <code>Calendar</code> whose <code>Window</code> content to retrieve
     * @param day the day of the <code>Window</code>
     * @return the binary data representing the <code>Window</code> content
     */
    private synchronized byte[] getWindowContent(long calendarId, int day) {
        TypedQuery<Calendar> q = em.createQuery("SELECT c FROM Calendar c WHERE c.numericId = :cname", Calendar.class);
        q.setParameter("cname", calendarId).setMaxResults(1);

        return q.getSingleResult()
                .getWindows()
                .get(day-1)
                .getContent();
    }

}
