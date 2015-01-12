package se.lnu.xmascal;

import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.Calendar;
import se.lnu.xmascal.model.Window;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Johan Widén
 * @author Jerry Strand
 */
@Named
@ViewScoped
public class ViewWindowBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private Calendar calendar;
    @EJB
    private CalendarManager calendarManager;

    public ViewWindowBean() {
    }

    public void preRenderListen(ComponentSystemEvent event) throws AbortProcessingException {
        String calName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("cal");
        calendar = calendarManager.getCalendar(calName);

        //TODO remove when we can create a complete calendar.
        // USED for testing:
        List<Window> windows = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            windows.add(new Window(calName, i, "ada".getBytes(), Window.ContentType.PICTURE));
        }
        for (int i = 5; i < 10; i++) {
            windows.add(new Window(calName, i, "ada".getBytes(), Window.ContentType.TEXT));
        }
        for (int i = 10; i < 15; i++) {
            windows.add(new Window(calName, i, "ada".getBytes(), Window.ContentType.VIDEO));
        }
        for (int i = 15; i < 25; i++) {
            windows.add(new Window(calName, i, "ada".getBytes(), Window.ContentType.URL));
        }
        calendar.setWindows(windows);
    }

    public String getName() {
        return calendar.getName();
    }

    public void setName(String name) {
        calendar.setName(name);
    }

    public List<Window> getWindows() {
        return calendar.getWindows();
    }

    public void open() {
        String calName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("cal");
        String wDay = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("wDay");
        String type = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("type");
        System.out.println("open calender: " + calName +" window: " + wDay + " type: " + type);

    }
}
