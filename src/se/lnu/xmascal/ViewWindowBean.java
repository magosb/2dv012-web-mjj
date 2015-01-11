package se.lnu.xmascal;

import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;
import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.Calendar;
import se.lnu.xmascal.model.Window;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Johan Widén
 */
@Named("viewWindowBean")
@ApplicationScoped
public class ViewWindowBean implements Serializable {
    private static final long serialVersionUID = 1L;
    @EJB
    private CalendarManager calendarManager;
    private Calendar calendar = new Calendar();
    private String name;

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance().
                getExternalContext().getRequestParameterMap();
        String cal = params.get("cal");

        System.out.println("Cal is: " + cal);

            name = cal;
            System.out.println("Equal?: " + name + " " + cal);
            calendar = calendarManager.getCalendar(name);

            //TODO remove when we can create a complete calendar.
            // USED for testing:
            List<Window> windows = new ArrayList<>();
            for (int i = 1; i < 10; i++) {
                windows.add(new Window(name, i, "ada".getBytes(), Window.ContentType.PICTURE));
            }
            for (int i = 10; i < 15; i++) {
                windows.add(new Window(name, i, "ada".getBytes(), Window.ContentType.VIDEO));
            }
            for (int i = 15; i < 25; i++) {
                windows.add(new Window(name, i, "ada".getBytes(), Window.ContentType.URL));
            }
            calendar.setWindows(windows);


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        System.out.println("SET NAME " + name);
        calendar = calendarManager.getCalendar(name);
        this.name = name;
    }

    public StreamedContent getBackground() {
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

    public List<Window> getWindows() {
        return calendar.getWindows();
    }

    public StreamedContent getContent() {
        FacesContext context = FacesContext.getCurrentInstance();

        // View is being rendered. Return a stub StreamedContent so that it will generate right URL.
        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new ByteArrayContent();
        }

        // Image is being requested. Return a real StreamedContent with the image bytes.
        else {
            String day = context.getExternalContext().getRequestParameterMap().get("windowDay");
            return new ByteArrayContent(calendar.getWindows().get(Integer.parseInt(day)).getContent());
        }
    }
}
