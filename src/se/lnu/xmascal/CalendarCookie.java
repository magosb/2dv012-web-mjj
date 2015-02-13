package se.lnu.xmascal;

import javax.servlet.http.Cookie;
import java.util.Base64;

/**
 * This class represents a Calendar cookie in the Christmas Calendar web application. Its purpose is to provide
 * for easier management of the data in each such cookie, as well as encoding/decoding to/from Base64 Basic
 * as defined by java.util.Base64.
 *
 * @author Jerry Strand
 */
public class CalendarCookie {
    private static final int WINDOWS_PER_CALENDAR = 24;
    private long calendarId;
    private String passphrase;
    private boolean[] windows;

    /**
     * @param calendarId the numeric calendar ID of the calendar that this cookie represents
     * @param passphrase the passphrase of the calendar represented by this cookie, or <code>null</code> if it has no
     *                   passphrase
     * @param windows an array of exactly 24 boolean values that represent whether a certain window has been opened
     *                by the user or not. The windows are numerated in ascending order and <code>true</code> represents
     *                an opened window while <code>false</code> represents a closed window. If this parameter is
     *                <code>null</code>, no windows are considered opened
     * @throws IllegalArgumentException if the number of given windows are not exactly 24
     */
    public CalendarCookie(long calendarId, String passphrase, boolean[] windows) throws IllegalArgumentException {
        if (windows == null) {
            windows = new boolean[WINDOWS_PER_CALENDAR];
        } else if (windows.length != WINDOWS_PER_CALENDAR) {
            throw new IllegalArgumentException("length of argument 'windows' must be 24");
        }
        this.calendarId = calendarId;
        this.passphrase = passphrase;
        this.windows = windows;
    }

    /**
     * Creates a new <code>CalendarCookie</code> with the numeric calendar ID, windows and passphrase found in the given
     * cookie. The format of the given cookie's value and name must match the one explained in the javadoc for
     * {@link #toCookie() toCookie()}
     * @param cookie the <code>Cookie</code> from which the <code>CalendarCookie</code> shall be constructed.
     * @throws IllegalArgumentException if the given cookie has a non integer calendar ID, has a null value, does not
     * have 24 window values and/or if at least one of the window values is neither 0 or 1
     */
    public CalendarCookie(Cookie cookie) throws IllegalArgumentException {
        try {
            calendarId = Integer.valueOf(cookie.getName());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Calendar cookie has a non integer calendar ID", e);
        }

        String value = cookie.getValue();
        if (value == null) {
            throw new IllegalArgumentException("Calendar cookie has a null value");
        }

        String[] tokens = cookie.getValue().split("\\|");
        if (tokens.length == 2) {
            passphrase = new String(Base64.getDecoder().decode(tokens[1]));
        }

        char[] winChArr = tokens[0].toCharArray();
        if (winChArr.length != 24) {
            throw new IllegalArgumentException("Calendar cookie does not have 24 window values");
        }

        windows = new boolean[24];
        for (int i = 0; i < 24; i++) {
            switch (winChArr[i]) {
                case '1':
                    windows[i] = true;
                    break;
                case '0':
                    windows[i] = false;
                    break;
                default:
                    throw new IllegalArgumentException("Calendar cookie has a window value that is neither 1 nor 0");
            }
        }
    }

    /**
     * @return the numeric calendar ID of the calendar that this cookie represents
     */
    public long getCalendarId() {
        return calendarId;
    }

    /**
     * @param calendarId the numeric ID of the calendar that this cookie represents
     */
    public void setCalendarId(long calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * @return the passphrase of the calendar that this cookie represents, or <code>null</code> if it does not have one
     */
    public String getPassphrase() {
        return passphrase;
    }

    /**
     * @param passphrase the passphrase of the calendar that this cookie represents
     */
    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    /**
     * @return an array of 24 <code>boolean</code> values, in which <code>true</code> at index X represents window on
     * day X+1 that as been opened, and <code>false</code> represents a window on day X+1 that has not been opened
     */
    public boolean[] getWindows() {
        return windows;
    }

    /**
     * @param windows the opened/closed status of the windows of the calendar that this cookie represents.
     *                See {@link #getWindows() getWindows()} for specifics
     */
    public void setWindows(boolean[] windows) {
        this.windows = windows;
    }

    /**
     * Formats and returns a <code>Cookie</code> version of this <code>CalendarCookie</code>. The name of the cookie
     * is the numeric ID of the <code>Calendar</code>. The format of the cookie's value is as follows:<br>
     * The first 24 characters are either '0' or '1'. A 0 represents a window that the user has not opened, and a '1'
     * represents a window that the user has opened. The opened/closed status of each window is enumerated in ascending
     * order, starting with the first window and ending with the last window.<br>
     * Optionally, if the calendar that this cookie represents is private, and the user has chosen to store the
     * passphrase in this cookie, the windows' opened/closed status will be followed by a '|' and the Base 64 Basic
     * encoded string version of that passphrase.<br>
     * Example cookie value: "111100000000000000000000|xJkP97"
     *
     * @return a <code>Cookie</code> with the name and value properties set
     */
    public Cookie toCookie() {
        StringBuilder cookieValue = new StringBuilder();
        for (boolean opened : windows) {
            cookieValue.append(opened ? '1' : '0');
        }

        if (passphrase != null) {
            cookieValue.append('|');
            String encoded = Base64.getEncoder().encodeToString(passphrase.getBytes());
            cookieValue.append(encoded);
        }

        return new Cookie(String.valueOf(calendarId), cookieValue.toString());
    }
}