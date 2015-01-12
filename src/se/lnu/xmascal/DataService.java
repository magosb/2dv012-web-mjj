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
 * @author Jerry Strand
 */
@Named
@ApplicationScoped// TODO: Should be application scoped
public class DataService implements Serializable {
    private static final long serialVersionUID = 1L;

    @PersistenceContext(name = "xmascal-pu")
    private EntityManager em;

    public DataService() {
    }






    // TODO: For future: cal ( thumbn | bg | (wcontent day) )
    // ^ request parameters set can be calendar name, followed by the type of file/binary, followed by either type bg/thumbnail or type window content followed by the day
    // Define public constants in this class to prevent typos?
    // TODO: ^ NO! Define these methods:
    // getCalendarBackground: param cal
    // getCalendarThumbnail: param cal
    // getWindowContent param cal, param day

    /**
     * Reads request param "cal"
     *
     * @return
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
            //Calendar calendar = calendarManager.getCalendar(calName);
            return new ByteArrayContent(getCalendarThumbnail(calName));
        }
    }

    private byte[] getCalendarThumbnail(String calendarName) {
        System.out.println("GETTING THUMBNAIL");
        Query query = em.createQuery("SELECT c.thumbnail FROM Calendar c WHERE c.name = :cname");
        query.setParameter("cname", calendarName);
        return (byte[]) query.getSingleResult();
    }

    /**
     * Reads request param "cal"
     *
     * @return
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
            System.out.println("SERVICE, name = " + calName);
            //Calendar calendar = calendarManager.getCalendar(calName);
            return new ByteArrayContent(getCalendarBackground(calName));
        }
    }

    private byte[] getCalendarBackground(String calendarName) {
        System.out.println("GETTING BACKGROUND");
        Query query = em.createQuery("SELECT c.background FROM Calendar c WHERE c.name = :cname");
        query.setParameter("cname", calendarName);
        return (byte[]) query.getSingleResult();
    }

    public synchronized StreamedContent getWindowContent() {
        FacesContext context = FacesContext.getCurrentInstance();

        // View is being rendered. Return a stub StreamedContent so that it will generate right URL.
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new ByteArrayContent();
        }

        // Image is being requested. Return a real StreamedContent with the image bytes.
        else {
            String calName = context.getExternalContext().getRequestParameterMap().get("cal");
            int winDay = Integer.parseInt(context.getExternalContext().getRequestParameterMap().get("day"));
            //Calendar calendar = calendarManager.getCalendar(calName);
            return new ByteArrayContent(getWindowContent(calName, winDay));
        }
    }

    private byte[] getWindowContent(String calendarName, int day) {
        System.out.println("GETTING CONTENT");
        Query query = em.createQuery("SELECT w.content FROM Window w WHERE w.calendarName = :cname AND w.day = :wday");
        query.setParameter("cname", calendarName).setParameter("wday", day);
        return (byte[]) query.getSingleResult();
    }
}
