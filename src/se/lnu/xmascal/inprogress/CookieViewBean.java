package se.lnu.xmascal.inprogress;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import java.io.Serializable;

/**
 * Created by doode on 2015-01-14.
 */
@Named
@ViewScoped
public class CookieViewBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String passphrase = null;
    private boolean remember = false;
    private boolean authorized = false;

    @Inject
    CookieManager cookieManager;

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public boolean getRemember() {
        return remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void checkAuthCookie(ComponentSystemEvent event) throws AbortProcessingException {
        // TODO: Instead of static name, get calendar ID from request params
        Cookie cookie = cookieManager.getCookie("cal");
        if (cookie != null && cookie.getValue().equals("pass")) {
            authorized = true;
        }
    }

    public void authCheck() {
        authorized = passphrase.equals("pass"); // TODO: Check if passphrase matches that of the calendar

        if (remember) {
            cookieManager.setCookie("cal", "pass", Integer.MAX_VALUE); // TODO: Set proper calendar cookie
        }
    }

}
