package se.lnu.xmascal;

import se.lnu.xmascal.ejb.AdminManager;
import se.lnu.xmascal.model.Admin;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;

/**
 * This class is a ViewScoped Managed Bean for the Admin class.
 */
//@DeclareRoles("XmasCalAdmin")
@Named("admin")
@ViewScoped
public class AdminBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private AdminManager adminManager;
    private String username;
    private String password;
    private Admin admin;

    public String getUsername() {
        //return username;
        return admin.getUsername();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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
     *
     * @author Johan Widén, Jerry Strand
     */
    public void logout() {  // TODO: TEST THIS METHOD
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
        try {
            request.logout();
            ctx.redirect("../");
        } catch (ServletException e) {
            sendErrorMsg("Logout failed.");
        } catch (IOException e) {
            sendErrorMsg("Redirect failed.");
        }

    }

}
