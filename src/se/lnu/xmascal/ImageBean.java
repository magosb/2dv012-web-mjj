package se.lnu.xmascal;

import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;
import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.Calendar;

import javax.ejb.EJB;
// TODO: Should this be faces.bean.ViewScope?
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

/**
 * Created by doode on 2015-01-08.
 */
@Named
@ViewScoped
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

}
