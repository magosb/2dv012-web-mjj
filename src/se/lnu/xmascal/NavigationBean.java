package se.lnu.xmascal;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Navigation bean
 * @author Johan Widén
 *
 */
@ManagedBean
@SessionScoped
public class NavigationBean {

    public String viewCalendar() {
        return "view";
    }

    public String admin() {
        return "admin";
    }

    public String login() {
        return "login";
    }

    public String logout() {
        return "logout";
    }

    public String addCalendar() {
        return "add";
    }

    public String editCalendar() {
        return "edit";
    }

    public String uploadFile() {
        return "uploadFile";
    }

    public String home() {
        return "home";
    }
}
