package se.lnu.xmascal;

import se.lnu.xmascal.ejb.CalendarManager;

import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.inject.Named;

/**
 * Created by doode on 2015-01-10.
 */
@Named("windows")
@ViewScoped
public class AddWindowBean {

    @EJB
    private CalendarManager calendarManager;
    private String calendarName;

    public AddWindowBean() {
    }

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }
}
