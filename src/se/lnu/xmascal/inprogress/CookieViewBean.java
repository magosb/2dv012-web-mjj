package se.lnu.xmascal.inprogress;

import se.lnu.xmascal.CalendarCookie;
import se.lnu.xmascal.CookieManager;
import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.Calendar;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * @author Jerry Strand
 */
@Named
@ViewScoped
public class CookieViewBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String CAL_REQ_PARAM_NAME = "cal";
    private String passphrase = null;
    private boolean remember = false;
    private boolean authorized = false;
    private Calendar calendar = null;
    private CalendarCookie calendarCookie = null;

    @Inject
    CookieManager cookieManager;

    @EJB
    CalendarManager calendarManager;

    /**
     * This method retrieves the requested calendar. If that calendar is private, attempt is made to read the calendar
     * passphrase from client cookie. If such a cookie exists, and if it has a passphrase which matches that of the
     * calendar, the client is considered authorized to view the calendar.
     */
    @PostConstruct
    private void init() {
        calendar = getCalendar();
        if (calendar == null) {
            authorized = false; // TODO: Need better way to handle this. What to do here? Calendar does not exist
        } else if (calendar.getPassPhrase() == null) {
            authorized = true;
        } else {
            calendarCookie = cookieManager.getCalendarCookie(calendar.getNumericId());

            // Cookie must exist and have a passphrase matching that of the calendar for the client to be authorized
            authorized = (calendarCookie != null && calendar.getPassPhrase().equals(calendarCookie.getPassphrase()));
        }
    }

    /**
     * @return <code>Calendar</code> with numeric ID that matches that of request parameter "cal", or <code>null</code>
     * if no such calendar exists
     */
    private Calendar getCalendar() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            long calId = Long.parseLong(context.getExternalContext().getRequestParameterMap().get(CAL_REQ_PARAM_NAME));
            return calendarManager.getCalendar(calId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * @return the passphrase that the client has entered for the calendar
     */
    public String getPassphrase() {
        return passphrase;
    }

    /**
     * @param passphrase the passphrase that the client has entered for the calendar
     */
    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    /**
     * @return <code>true</code> if the client has indicated that the entered password should remembered using a cookie,
     * else false
     */
    public boolean getRemember() {
        return remember;
    }

    /**
     * @param remember whether or not the client wants to have the passphrase of the calendar remembered using a cookie
     */
    public void setRemember(boolean remember) {
        this.remember = remember;
    }

    /**
     * @return <code>true</code> if the client is considered authorized to view the calendar, else <code>false</code>
     */
    public boolean isAuthorized() {
        return authorized;
    }

    /**
     * This method checks whether the submitted passphrase matches that of the calendar that the client requested.
     * If the passphrase is correct the client is considered authorized to view the calendar. If the client wishes for
     * the passphrase to be remembered, a cookie will be set.
     */
    // TODO: IF THE CLIENT DOES NOT SET THE REMEMBER CHECKBOX, OPENED WINDOWS WILL NOT BE REMEMBERED!
    public void validatePassphrase() {
        if (calendar == null) { // Due to calendar with given request param ID not existing
            authorized = false; // TODO: Need better way to handle this. What to do here? Calendar does not exist
        } else if (calendar.getPassPhrase() == null) {
            authorized = true;

            // If the user wishes to be remembered, either set new cookie or update exisiting one
            if (remember) {
                if (calendarCookie == null) {
                    calendarCookie = new CalendarCookie(calendar.getNumericId(), null, null);
                } else {
                    calendarCookie.setPassphrase(null);
                }
                cookieManager.setCalendarCookie(calendarCookie);
            }
        } else if (calendar.getPassPhrase().equals(passphrase)) {
            authorized = true;

            // Passphrase is valid. If the user wishes to be remembered, either set new cookie or update exisiting one
            if (remember) {
                if (calendarCookie == null) {
                    calendarCookie = new CalendarCookie(calendar.getNumericId(), passphrase, null);
                    cookieManager.setCalendarCookie(calendarCookie);

                    // passphrase cannot be null here, since calendar's passphrase is not null, and matches entered pass
                } else if (!passphrase.equals(calendarCookie.getPassphrase())) {
                    calendarCookie.setPassphrase(passphrase);
                    cookieManager.setCalendarCookie(calendarCookie);
                }
            }
        } else {
            authorized = false;
        }
    }

    /**
     * @param windowNr
     * @return
     */
    public boolean getIsOpened(int windowNr) {
        if (calendarCookie != null) {
            System.out.println("getIsOpened: cookie is NOT null");
            return calendarCookie.getWindows()[windowNr - 1]; // Zero indexed array. 0 is day 1, 23 is day 24
        } else {
            System.out.println("getIsOpened: cookie IS null");
            return false;
        }
    }

    /**
     * @param windowNr
     */
    public void setIsOpened(int windowNr) {
        if (calendarCookie == null) {

            // If calendarCookie is null, user has chosen not to be remembered earlier -- do not store passphrase
            calendarCookie = new CalendarCookie(calendar.getNumericId(), null, null);
        }
        calendarCookie.getWindows()[windowNr - 1] = true; // Zero indexed array. 0 is day 1, 23 is day 24
        cookieManager.setCalendarCookie(calendarCookie);
    }

}
