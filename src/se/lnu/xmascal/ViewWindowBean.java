package se.lnu.xmascal;

import org.primefaces.component.media.Media;
import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.Calendar;
import se.lnu.xmascal.model.Window;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Johan Widén
 */
@ManagedBean
@SessionScoped
public class ViewWindowBean {
    private Calendar calendar;
    private List<Window> windows;
    private byte[] background;
    @EJB
    private CalendarManager calendarManager;
    private StreamedContent content;



    @PostConstruct
    public void init() {
        calendar = calendarManager.getCalendar("johans");

        windows = new ArrayList<Window>();
        for (int i = 1; i < 10; i++) {
            windows.add(new Window("johans", i, "ada".getBytes(), "image"));
        }
        for (int i = 10; i < 15; i++) {
            windows.add(new Window("johans", i, "ada".getBytes(), "video"));
        }
        for (int i = 15; i < 25; i++) {
            windows.add(new Window("johans", i, "ada".getBytes(), "web"));
        }
    }

    public String getName() {
        return calendar.getName();
    }

    public byte[] getBackground() {
        return calendar.getBackground();
    }

    public String getPassPhrase() {
        return calendar.getPassPhrase();
    }

    public byte[] getThumbnail() {
        return calendar.getThumbnail();
    }

    public List<Window> getWindows() {
        return windows;
    }

    public StreamedContent getContent() {

        content = new ByteArrayContent(windows.get(1).getContent());
        return this.content;
    }
}
