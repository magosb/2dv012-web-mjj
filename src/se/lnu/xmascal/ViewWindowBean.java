package se.lnu.xmascal;

import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;
import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.Calendar;
import se.lnu.xmascal.model.Window;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
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
    private StreamedContent background;
    @EJB
    private CalendarManager calendarManager;



    @PostConstruct
    public void init() {
        calendar = calendarManager.getCalendar("johans");

        windows = new ArrayList<Window>();
        for (int i = 1; i < 10; i++) {
            windows.add(new Window("Johans christmas calendar", i, "ada".getBytes(), "image"));
        }
        for (int i = 10; i < 15; i++) {
            windows.add(new Window("Johans christmas calendar", i, "ada".getBytes(), "video"));
        }
        for (int i = 15; i < 25; i++) {
            windows.add(new Window("Johans christmas calendar", i, "ada".getBytes(), "web"));
        }
    }

    public String getName() {
        return calendar.getName();
    }

    public StreamedContent getBackground() {
        background = new ByteArrayContent(calendar.getBackground());
        return this.background;
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


}
