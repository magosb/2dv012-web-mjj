package se.lnu.xmascal;

import se.lnu.xmascal.ejb.AdminQueryBean;
import se.lnu.xmascal.model.Admin;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;

/**
 * This class is a ViewScoped Managed Bean for the Admin class.
 */
//@DeclareRoles("XmasCalAdmin")
@Named
@ViewScoped
public class AdminManagedBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private AdminQueryBean adminQueryBean;
    private String username;
    private String password;

    public String getUsername() {
        return username;
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

    /**
     * Sets the provided msg as an error message using the 'messages' tag on the facelet of the current page.
     *
     * @param msg the error message to be set
     */
    private void setErrorMsg(String msg) {
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null);
        FacesContext.getCurrentInstance().addMessage("", facesMessage); // TODO: Do we need to specify anything for ""?
    }

    /**
     * Uses the currently set username and password of this <code>AdminManagedBean</code> to validate the admin. If
     * the credentials match those of an admin in the database, the admin is logged in.
     *
     * @return a page redirection <code>String</code>
     */
    public String login() {
        for (Admin admin : adminQueryBean.getAllAdmins()) {
            if (admin.getUsername().equals(username) && admin.getPassword().equals(password)) {
                return "/index.xhtml?faces-redirect=true";
            }
        }

        setErrorMsg("No admin with that username and password exists.");
        return null;
    }

}
