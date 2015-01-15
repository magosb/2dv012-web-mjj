package se.lnu.xmascal.inprogress;

import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * Created by doode on 2015-01-13.
 */
public class CookieManager implements Serializable {
    private static final long serialVersionUID = 1L;

    public CookieManager() {
    }

    // TODO: Prepend "cal" to calendarName. Or, simpler: add ID column to calendar table and use that

    /**
     * Adds a request for the client to set a cookie with the given parameters.
     *
     * @param name the name of the cookie
     * @param value the value of the cookie
     * @param maxAge the maximum age, specified as number of seconds into the future, starting from when the browser received the cookie
     */
    private void setCookie(String name, String value, int maxAge) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        Cookie cookie = null;

        // Cookies sent in this request by the client
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(name)) {
                    cookie = c;
                    break;
                }
            }
        }

        if (cookie != null) {   // Cookie was set previously. Update its value
            cookie.setValue(value);
        } else {                // There's no pre-existing cookie. Create a new one
            cookie = new Cookie(name, value);
            cookie.setPath(request.getContextPath());
        }
        cookie.setMaxAge(maxAge);

        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        response.addCookie(cookie);
    }

    /**
     * @param calendarCookie the <code>CalendarCookie</code> to request that the client browser sets
     */
    public synchronized void setCalendarCookie(CalendarCookie calendarCookie) {
        Cookie cookie = calendarCookie.toCookie();
        setCookie(cookie.getName(), cookie.getValue(), Integer.MAX_VALUE);
    }

    /**
     * Retrieves a <code>Cookie</code> from the current HTTP request.
     *
     * @param name the name of the <code>Cookie</code> to retrieve
     * @return the <code>Cookie</code> with the given name, or <code>null</code> if no such <code>Cookie</code> exists
     */
    private Cookie getCookie(String name) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        Cookie cookie = null;

        // Cookies sent in this request by the client
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(name)) {
                    cookie = c;
                    return cookie;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param calendarId
     * @return or <code>null</code> if no cookie with the given calendarId exists
     * @throws UnsupportedOperationException
     */
    public synchronized CalendarCookie getCalendarCookie(int calendarId) throws UnsupportedOperationException {
        Cookie cookie = getCookie(String.valueOf(calendarId));
        if (cookie == null) {
            return null;
        } else {
            try {
                CalendarCookie calCookie = new CalendarCookie(cookie); // TODO: Perhaps have a makeCalendarCookie in this class instead?
                return calCookie;
            } catch (IllegalArgumentException e) {
                throw new UnsupportedOperationException(
                        "A CalendarCookie cannot be constructed with value from the cookie with the given calendarId");
            }
        }
    }

    /* TODO: Implement password restriction based on this:
     * The value of a cookie may consist of any printable ASCII character (! through ~, unicode \u0021 through \u007E)
     * excluding , and ; and excluding whitespace. The name of the cookie also excludes = as that is the delimiter
     * between the name and value. The cookie standard RFC2965 is more limiting but not implemented by browsers.
     *
     * passphrase|0|1|0|0|1|0|0|0|0
     */

}
