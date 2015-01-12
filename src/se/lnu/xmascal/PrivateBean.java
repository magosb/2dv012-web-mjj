package se.lnu.xmascal;

import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.Calendar;

import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Created by Zixix on 2015-01-12.
 */
@Named("privateB")
@ViewScoped
public class PrivateBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private CalendarManager calendarManager;

    public synchronized boolean isPrivate() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return false;
        }
        // Image is being requested. Return a real StreamedContent with the image bytes.
        else {
            String name = context.getExternalContext().getRequestParameterMap().get("name");
            Calendar calendar = calendarManager.getCalendar(name);
            if (calendar.getPassPhrase() != null) {
                return true;
            } else {
                return false;
            }
        }
    }
}
