package se.lnu.xmascal;

import se.lnu.xmascal.ejb.AdminManager;
import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.Admin;
import se.lnu.xmascal.model.Calendar;

import javax.annotation.PostConstruct;
import javax.annotation.security.DeclareRoles;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;

/**
 * This class is a SessionScoped Managed Bean for the Admin class.
 * @author Johan Widén
 */
@DeclareRoles("XmasCalAdmin")
@Named("admin")
@SessionScoped
public class AdminBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private AdminManager adminManager;
    private Admin admin;

    @EJB
    private CalendarManager calendarManager;
    Calendar calendar;

    public String getUsername() {
        return admin.getUsername();
    }

    public void setUsername(String username) {
        admin.setUsername(username);
    }

    public String getPassword() {
        return admin.getPassword();
    }

    public void setPassword(String password) {
        admin.setPassword(password);
    }

    /**
     * Retrieves currently logged in <code>Admin</code> from database.
     */
    @PostConstruct
    public void setup() {
        ExternalContext external = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) external.getRequest();
        String username = request.getRemoteUser();
        admin = adminManager.getAdmin(username);

        // Test whether the current user belongs to a given role:
        // String role = "admin";
        // boolean isAdmin = request.isUserInRole(role);
    }

    /**
     * Sends the provided msg as an error message using the 'messages' tag on the facelet of the current page.
     *
     * @param msg the error message to be set
     */
    private void sendErrorMsg(String msg) {
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null);
        FacesContext.getCurrentInstance().addMessage("", facesMessage); // TODO: Do we need to specify anything for ""?
    }

    /**
     * Logs out the admin.
     */
    public void logout() {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
        try {
            request.logout();
            ctx.redirect("../");
        } catch (ServletException e) {
            sendErrorMsg("Logout failed."); // TODO: Will this way of communicating an error be used?
        } catch (IOException e) {
            sendErrorMsg("Redirect failed.");
        }

    }

    /**
     * Delete the calendar passed by the RequestParameter 'cal'.
     */
    public void removeCalendar() {
        int cal = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("cal"));
        calendar = calendarManager.getCalendar(cal);
        calendarManager.remove(calendar);
    }
}
