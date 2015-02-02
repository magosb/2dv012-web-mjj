package se.lnu.xmascal.inprogress;

import javax.servlet.http.Cookie;
import java.util.Base64;

/**
 * This class represents a Calendar cookie in the Christmas Calendar web application. Its purpose is to provide
 * for a more easily management of the data in each such cookie, as well as encoding/decoding to/from Base64 Basic
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
     * @param calendarId the calendar ID that this cookie represents
     * @param passphrase the passphrase of the calendar represented by this cookie, or <code>null</code> if it has no passphrase
     * @param windows an array of exactly 24 boolean values that represent whether a certain window has been opened
     *                by the user or not. The windows are numerated in ascending order and <code>true</code> represents
     *                an opened window while <code>false</code> represents a closed window. If this parameter is
     *                <code>null</code>, no windows are considered opened
     */
    public CalendarCookie(long calendarId, String passphrase, boolean[] windows) throws IllegalArgumentException { // TODO: Add exception to JavaDoc
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
     *
     * @param cookie the <code>Cookie</code> from which the <code>CalendarCookie</code> shall be constructed. // TODO: Clarify format it needs to follow
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

        char[] charr = tokens[0].toCharArray();
        if (charr.length != 24) {
            throw new IllegalArgumentException("Calendar cookie does not have 24 window values");
        }

        windows = new boolean[24];
        for (int i = 0; i < 24; i++) {
            switch (charr[i]) {
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

    public long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(long calendarId) {
        this.calendarId = calendarId;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public boolean[] getWindows() {
        return windows;
    }

    public void setWindows(boolean[] windows) {
        this.windows = windows;
    }

    /**
     * The format of the cookie's value is as follows:<br>
     * The first 24 characters are either '0' or '1'. A 0 represents a window that the user has not opened, and a '1'
     * represents a window that the user has opened. The opened/closed status of each window is enumerated in ascending
     * order, starting with the first window and ending with the last window.<br>
     * Optionally, if the calendar that this cookie represents is private, and the user has chosen to store the
     * passphrase in this cookie, the windows' opened/closed status will be followed by a '|' and the Base 64 Basic
     * encoded string version of that passphrase.
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