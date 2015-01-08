package se.lnu.xmascal;

import se.lnu.xmascal.model.Calendar;
import se.lnu.xmascal.model.Window;

import javax.annotation.PostConstruct;
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

    @PostConstruct
    public void init() {
        calendar = new Calendar("Johans christmas calendar", "Ada".getBytes(), "Beta".getBytes(), "pass");
        windows = new ArrayList<Window>();
        for (int i = 1; i < 10; i++) {
            windows.add(new Window("Johans christmas calendar", i, "ada".getBytes(), "imagewindow"));
        }
        for (int i = 10; i < 15; i++) {
            windows.add(new Window("Johans christmas calendar", i, "ada".getBytes(), "videowindow"));
        }
        for (int i = 15; i < 25; i++) {
            windows.add(new Window("Johans christmas calendar", i, "ada".getBytes(), "webwindow"));
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


}
