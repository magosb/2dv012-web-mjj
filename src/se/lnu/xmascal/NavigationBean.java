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
        return "/view-calendar/index.xhtml";
    }

    public String adminStart() {
        return "/admin/index.xhtml";
    }

    public String addCalendar() {
        return "/add-calendar/index.xhtml";
    }

    public String editCalendar() {
        return "/edit-calendar/index.xhtml";
    }

    public String uploadFile() {
        return "uploadFile";
    }

    public String home() {
        return "home";
    }
}
