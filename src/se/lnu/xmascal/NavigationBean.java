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

    public String uploadFile() {
        return "uploadFile";
    }

    public String adminLogin() {
        return "admin";
    }

    public String home() {
        return "home";
    }
}
