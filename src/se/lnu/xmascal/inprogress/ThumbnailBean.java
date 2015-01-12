package se.lnu.xmascal.inprogress;

import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;
import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.Calendar;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@Named("thumbnails")
@ViewScoped
public class ThumbnailBean implements Serializable{
    private static final long serialVersionUID = 1L;

    @EJB
    private CalendarManager calendarManager;

    public synchronized StreamedContent getThumbnail() throws IOException { // TODO: Handle
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

    public synchronized List<String> getCalendarNames() {
        return calendarManager.getAllCalendarNames();
    }

}