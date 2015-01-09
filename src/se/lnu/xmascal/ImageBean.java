package se.lnu.xmascal;

import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;
import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.Calendar;

import javax.ejb.EJB;
// TODO: Should this be faces.bean.ViewScope?
import javax.enterprise.context.ApplicationScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by doode on 2015-01-08.
 */
@Named
@ApplicationScoped
public class ImageBean {
    private StreamedContent image;
    @EJB
    private CalendarManager calendarManager;

    public ImageBean() {
    }

    public StreamedContent getImage() {
        Calendar cal = calendarManager.getCalendar("johans");
        image = new ByteArrayContent(cal.getBackground());
        return this.image;
    }

    public List<StreamedContent> getAllThumbnails() {
        List<Calendar> cals = calendarManager.getAllCalendars();
        List<StreamedContent> contents = new ArrayList<>();
        for (Calendar cal : cals) {
            if (cal.getThumbnail() == null) {
                System.out.println("null");
            }
            contents.add(new ByteArrayContent(cal.getThumbnail()));
        }
        System.out.println(contents.size());
        return contents;
    }

}
