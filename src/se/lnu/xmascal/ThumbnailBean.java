package se.lnu.xmascal;

import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.Calendar;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Named;
import java.io.IOException;
import java.util.List;

@Named("thumbnails")
@ApplicationScoped
public class ThumbnailBean {

    @EJB
    private CalendarManager calendarManager;

    public StreamedContent getThumbnail() throws IOException { // TODO: Handle
        FacesContext context = FacesContext.getCurrentInstance();

        // View is being rendered. Return a stub StreamedContent so that it will generate right URL.
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new ByteArrayContent();
            //return new DefaultStreamedContent();
        }

        // Image is being requested. Return a real StreamedContent with the image bytes.
        else {
            String name = context.getExternalContext().getRequestParameterMap().get("name");
            Calendar calendar = calendarManager.getCalendar(name);
            //return new DefaultStreamedContent(new ByteArrayInputStream(image.getBytes()));
            return new ByteArrayContent(calendar.getThumbnail());
        }
    }

    public List<String> getCalendarNames() {
        return calendarManager.getAllCalendarNames();
    }

}