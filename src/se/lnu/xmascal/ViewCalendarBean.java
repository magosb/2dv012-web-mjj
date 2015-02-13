package se.lnu.xmascal;

import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.Calendar;
import se.lnu.xmascal.model.Window;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * This
 *
 * @author Johan Widén
 */
@Named
@ViewScoped
public class ViewCalendarBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private Calendar calendar;
    @EJB
    private CalendarManager calendarManager;

    /**
     * Load the calendar before the view is rendered.
     * @throws AbortProcessingException
     */
    public void preRenderListen() throws AbortProcessingException {
        if(calendar == null) {
            Long cal = Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("cal"));
            calendar = calendarManager.getCalendar(cal);
        }
    }

    /**
     * Returns the name of the calendar
     * @return Calendar name
     */
    public String getName() {
        return calendar.getName();
    }

    /**
     * Returns the ID of the calendar.
     * @return Calendar ID
     */
    public Long getId() {
        return calendar.getNumericId();
    }

    /**
     * Returns a list of all the windows for the calendar.
     * @return List of Window
     */
    public List<Window> getWindows() {
        return calendar.getWindows();
    }

}
