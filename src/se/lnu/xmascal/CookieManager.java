package se.lnu.xmascal;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * This class is an ApplicationScoped bean used for setting and retrieving client cookies.
 *
 * @author Jerry Strand
 */
@Named
@ApplicationScoped
public class CookieManager implements Serializable {
    private static final long serialVersionUID = 1L;

    public CookieManager() {
    }

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
     * @param calendarId the numeric ID of the <code>Calendar</code> whose <code>CalendarCookie</code> to retrieve
     * @return the <code>CalendarCookie</code> that represents the <code>Calendar</code> with the given numeric ID, or
     * <code>null</code> if no such cookie exists
     * @throws <code>UnsupportedOperationException</code> if the cookie found has invalid data
     */
    public synchronized CalendarCookie getCalendarCookie(long calendarId) throws UnsupportedOperationException {
        Cookie cookie = getCookie(String.valueOf(calendarId));
        if (cookie == null) {
            return null;
        } else {
            try {
                CalendarCookie calCookie = new CalendarCookie(cookie);
                return calCookie;
            } catch (IllegalArgumentException e) {
                throw new UnsupportedOperationException(
                        "A CalendarCookie cannot be constructed with value from the cookie with the given calendarId",e);
            }
        }
    }

}
