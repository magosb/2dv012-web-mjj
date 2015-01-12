package se.lnu.xmascal.services;

import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;
import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.Calendar;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Map;

// TODO: Should this be faces.bean.ViewScope?

/**
 * Created by doode on 2015-01-08.
 */
@Named
@ApplicationScoped
public class CalendarImageService implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private CalendarManager calendarManager;

    public CalendarImageService() {
    }

    /**
     * Reads request param "cal"
     * @return
     */
    public synchronized StreamedContent getBackground() {
        FacesContext context = FacesContext.getCurrentInstance();

        // View is being rendered. Return a stub StreamedContent so that it will generate right URL.
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new ByteArrayContent();
        }

        // Image is being requested. Return a real StreamedContent with the image bytes.
        else {
            String calName = context.getExternalContext().getRequestParameterMap().get("cal");
            System.out.println("SERVICE, name = " + calName);
            Calendar calendar = calendarManager.getCalendar(calName);
            return new ByteArrayContent(calendar.getBackground());
        }
    }

    // TODO: For future: cal ( thumbn | bg | (wcontent day) )
    // ^ request parameters set can be calendar name, followed by the type of file/binary, followed by either type bg/thumbnail or type window content followed by the day
    // Define public constants in this class to prevent typos?
    /**
     * Reads request param "cal"
     * @return
     */
    public synchronized StreamedContent getThumbnail() {
        FacesContext context = FacesContext.getCurrentInstance();

        // View is being rendered. Return a stub StreamedContent so that it will generate right URL.
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new ByteArrayContent();
        }

        // Image is being requested. Return a real StreamedContent with the image bytes.
        else {
            String calName = context.getExternalContext().getRequestParameterMap().get("cal");
            Calendar calendar = calendarManager.getCalendar(calName);
            return new ByteArrayContent(calendar.getThumbnail());
        }
    }
}
