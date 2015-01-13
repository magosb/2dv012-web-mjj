package se.lnu.xmascal;

import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;

// TODO: Need to validate all request parameters

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
     * of generating a valid URL.<br>
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
            String calName = context.getExternalContext().getRequestParameterMap().get("cal");
            return new ByteArrayContent(getCalendarThumbnail(calName));
        }
    }

    /**
     * @param calendarName the name of the <code>Calendar</code> whose thumbnail shall be retrieved
     * @return the binary data that represents the thumbnail
     */
    private byte[] getCalendarThumbnail(String calendarName) {
        Query query = em.createQuery("SELECT c.thumbnail FROM Calendar c WHERE c.name = :cname");
        query.setParameter("cname", calendarName);
        return (byte[]) query.getSingleResult();
    }

    /**
     * When this method is called from a view that is being rendered, it returns stub data with the sole responsibility
     * of generating a valid URL.<br>
     * When the client browser uses the generated URL to request the image, this method will check for a request
     * parameter "cal" and return the actual background data of the <code>Calendar</code> with that name.
     */
    public synchronized StreamedContent getCalendarBackground() {
        FacesContext context = FacesContext.getCurrentInstance();

        // View is being rendered. Return a stub StreamedContent so that it will generate right URL.
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new ByteArrayContent();
        }

        // Image is being requested. Return a real StreamedContent with the image bytes.
        else {
            String calName = context.getExternalContext().getRequestParameterMap().get("cal");
            return new ByteArrayContent(getCalendarBackground(calName));
        }
    }

    /**
     * @param calendarName the name of the <code>Calendar</code> whose background shall be retrieved
     * @return the binary data representing the background
     */
    private byte[] getCalendarBackground(String calendarName) {
        System.out.println("GETTING BACKGROUND");
        Query query = em.createQuery("SELECT c.background FROM Calendar c WHERE c.name = :cname");
        query.setParameter("cname", calendarName);
        return (byte[]) query.getSingleResult();
    }

    /**
     * This method is used to retrieve <b>non text</b> content of a <code>Calendar Window</code>.<br>
     * When this method is called from a view that is being rendered, it returns stub data with the sole responsibility
     * of generating a valid URL.<br>
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
            String calName = context.getExternalContext().getRequestParameterMap().get("cal");
            int winDay = Integer.parseInt(context.getExternalContext().getRequestParameterMap().get("day"));
            return new ByteArrayContent(getWindowContent(calName, winDay));
        }
    }

    /**
     * This method is used to retrieve text (includes URL) content of a <code>Calendar Window</code>.<br>
     * When this method is called from a view that is being rendered, it returns stub data with the sole responsibility
     * of generating a valid URL.<br>
     * When the client browser uses the generated URL to request the content, this method will check for request
     * parameters "cal" and "day" and return the actual content of that <code>Calendar Window</code>.
     */
    public synchronized String getTextContent(String calName, int winDay) {
        return new String(getWindowContent(calName, winDay));
    }

    /**
     * @param calendarName the name of the <code>Calendar</code> whose <code>Window</code> content to retrieve
     * @param day the day of the <code>Window</code>
     * @return the binary data representing the <code>Window</code> content
     */
    private byte[] getWindowContent(String calendarName, int day) {
        Query query = em.createQuery("SELECT w.content FROM Window w WHERE w.calendarName = :cname AND w.day = :wday");
        query.setParameter("cname", calendarName).setParameter("wday", day);
        return (byte[]) query.getSingleResult();
    }

}
