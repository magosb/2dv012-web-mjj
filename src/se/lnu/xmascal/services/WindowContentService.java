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

// TODO: Should this be faces.bean.ViewScope?

/**
 * Created by doode on 2015-01-08.
 */
@Named("wcs")
@ApplicationScoped
public class WindowContentService implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private CalendarManager calendarManager;

    public WindowContentService() {
    }

//    public StreamedContent getImage() {
//        Calendar cal = calendarManager.getCalendar("johans");
//        image = new ByteArrayContent(cal.getBackground());
//        return this.image;
//    }
//

    public StreamedContent getBackground(Calendar calendar) {
        FacesContext context = FacesContext.getCurrentInstance();

        // View is being rendered. Return a stub StreamedContent so that it will generate right URL.
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new ByteArrayContent();
        }

        // Image is being requested. Return a real StreamedContent with the image bytes.
        else {
            return new ByteArrayContent(calendar.getBackground());
        }
    }

    public StreamedContent getContent() {
        FacesContext context = FacesContext.getCurrentInstance();

        // View is being rendered. Return a stub StreamedContent so that it will generate right URL.
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new ByteArrayContent();
        }

        // Image is being requested. Return a real StreamedContent with the image bytes.
        else {
            String wDay = context.getExternalContext().getRequestParameterMap().get("wDay");
            String cal = context.getExternalContext().getRequestParameterMap().get("cal");
            System.out.println("Day is...: " + wDay + " cal: " + cal);
            return new ByteArrayContent(calendarManager.getCalendar(cal).getWindows().get(Integer.parseInt(wDay)).getContent());
        }
    }
}
