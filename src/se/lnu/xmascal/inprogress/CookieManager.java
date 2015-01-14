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
    private boolean auth = false;

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
    public void setCookie(String name, String value, int maxAge) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        Cookie cookie = null;

        // Cookies sent in this request by the client
        Cookie[] cookies = request.getCookies();

        // First check if a cookie with the given name is already set, and if so, use it
        // if (cookies != null && cookies.length > 0) {
        //     for (int i = 0; i < cookies.length; i++) {
        //         if (cookies[i].getName().equals(name)) {
        //             cookie = cookies[i];
        //             break;
        //         }
        //     }
        // }
        for (Cookie c : cookies) { // TODO: What if cookies is null?
            if (c.getName().equals(name)) {
                cookie = c;
                break;
            }
        }

        if (cookie != null) {
            cookie.setValue(value);
        } else {
            cookie = new Cookie(name, value);
            cookie.setPath(request.getContextPath());
        }
        cookie.setMaxAge(maxAge);

        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        response.addCookie(cookie);
    }

    /**
     * Retrieves a <code>Cookie</code> from the current HTTP request.
     *
     * @param name the name of the <code>Cookie</code> to retrieve
     * @return the <code>Cookie</code> with the given name, or <code>null</code> if no such <code>Cookie</code> exists
     */
    public Cookie getCookie(String name) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        Cookie cookie = null;

        // Cookies sent in this request by the client
        Cookie[] cookies = request.getCookies();
        //if (cookies != null && cookies.length > 0) {
        //    for (int i = 0; i < cookies.length; i++) {
        //        if (cookies[i].getName().equals(name)) {
        //            cookie = cookies[i];
        //            return cookie;
        //        }
        //    }
        //}

        for (Cookie c : cookies) { // TODO: What if cookies is null?
           if (c.getName().equals(name)) {
               cookie = c;
               return cookie;
           }
        }
        return null;
    }

    /* TODO: Implement password restriction based on this:
     * The value of a cookie may consist of any printable ASCII character (! through ~, unicode \u0021 through \u007E)
     * excluding , and ; and excluding whitespace. The name of the cookie also excludes = as that is the delimiter
     * between the name and value. The cookie standard RFC2965 is more limiting but not implemented by browsers.
     */
}
