package se.lnu.xmascal.inprogress;

import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.*;
import se.lnu.xmascal.model.Calendar;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

/**
 * @author Johan Widén
 */
@Named("thumbnails")
@ViewScoped
public class ThumbnailBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Boolean> thumbnailPrivate;

    @EJB
    private CalendarManager calendarManager;

    private String passPhrase;


    /**
     * This method get all names of the calendars.
     *
     * @return List with calendar names
     */
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
        System.out.println("Calendar id is: " + cal);
        return calendarManager.getCalendar(cal).isPrivate();
    }

    public void openPrivate() {
        String calName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("cal");
        System.out.println("open calender: " + calName);
    }

    public String getPassPhrase() {
        return passPhrase;
    }

    public void setPassPhrase(String passPhrase) {
        this.passPhrase = passPhrase;
    }

    public String displayPrivate() {
        String calName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("cal");
        Calendar cal = calendarManager.getCalendar(calName);
        if (!(cal.getPassPhrase() == passPhrase)) {
            //sendErrorMsg("Wrong pass phrase!");
            System.out.println("Wrong pass phrase " + cal.getPassPhrase() + " " + passPhrase);
            return "";
        } else {
            System.out.println("Correct pass phrase");
            return "view";
        }
    }

}