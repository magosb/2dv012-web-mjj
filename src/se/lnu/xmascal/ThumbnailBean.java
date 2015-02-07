package se.lnu.xmascal;

import se.lnu.xmascal.ejb.CalendarManager;

import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

/**
 * This class handles all thumbnails of calendars.
 * @author Johan Widén
 */
@Named("thumbnails")
@ViewScoped
public class ThumbnailBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private CalendarManager calendarManager;

    /**
     * This method get all names of the calendars.
     *
     * @return List with calendar names
     */
    @Deprecated
    public synchronized List<String> getCalendarNames() {
        return calendarManager.getAllCalendarNames();
    }

    /**
     * @return a <code>List</code> of the numeric IDs of all <code>Calendar</code>s in the system
     */
    public synchronized List<Long> getCalendarIds() {
        return calendarManager.getAllCalendarIds();
    }

    /**
     * This methods returns true or false if the given calendar is private or not.
     *
     * @param cal The calendar to check if it is private or not.
     * @return Boolean that is either True or False depending of the param calendar
     */
    public synchronized boolean isCalendarPrivate(Long cal) {
        return calendarManager.getCalendar(cal).isPrivate();
    }
}