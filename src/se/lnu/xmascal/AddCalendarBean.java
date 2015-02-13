package se.lnu.xmascal;

import org.primefaces.event.FileUploadEvent;
import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.Calendar;
import se.lnu.xmascal.model.Window;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a ViewScoped Managed Bean used for the addition of calendars to the application.
 *
 * @author Jerry Strand
 */
//@DeclareRoles("XmasCalAdmin") // TODO: Skip this?
@Named("newCalendar")
@ViewScoped
public class AddCalendarBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private CalendarManager calendarManager;
    private String name;
    private byte[] background;
    private byte[] thumbnail;
    private String passPhrase;
    private Calendar calendar = new Calendar();
    private boolean isPublic = true;

    /**
     * @return the name that the new calendar shall receive
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name that the new calendar shall receive
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return <code>true</code> if the calendar shall be public, otherwise <code>false</code>
     */
    public boolean getPublic() {
        return isPublic;
    }

    /**
     * @param isPublic whether the calendar shall be public or not
     */
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    /**
     * @return the passphrase for the new calendar
     */
    public String getPassPhrase() {
        return passPhrase;
    }

    /**
     * @param passPhrase the passphrase for the new calendar
     */
    public void setPassPhrase(String passPhrase) {
        this.passPhrase = passPhrase;
    }

    /**
     * This method retrieves the uploaded background picture in binary format.
     *
     * @param event the <code>FileUploadEvent</code> that triggers the handling of background picture upload
     */
    public void handleBackgroundUpload(FileUploadEvent event) {
        try {
            background = getUploadedBytes(event.getFile().getInputstream());
            sendInfoMsg(event.getFile().getFileName() + " has been uploaded.");
        } catch (IOException e) {
            sendErrorMsg("Unable to retrieve uploaded background picture data");
        }
    }

    /**
     * This method retrieves the uploaded thumbnail in binary format.
     *
     * @param event the <code>FileUploadEvent</code> that triggers the handling of thumbnail upload
     */
    public void handleThumbnailUpload(FileUploadEvent event) {
        try {
            thumbnail = getUploadedBytes(event.getFile().getInputstream());
            sendInfoMsg(event.getFile().getFileName() + " has been uploaded.");
        } catch (IOException e) {
            sendErrorMsg("Unable to retrieve uploaded thumbnail data");
        }
    }

    /**
     * @param is the <code>InputStream</code> from which to read the bytes
     * @return an array of <code>byte</code>s containing the the uploaded data
     */
    private byte[] getUploadedBytes(InputStream is) throws IOException {
        byte[] data = new byte[is.available()]; // TODO: THIS MIGHT NOT READ ALL BYTES!!
        is.read(data); // TODO: THIS MAY BE THE SOURCE OF SOME PROBLEMS!!
        is.close();
        return data;
    }

    /**
     * This method stores the entered data as a calendar in the database. Error messages are sent to the current facelet
     * if any attributes are null and if a calendar with the set name exists. An info message is sent if the calendar
     * was added successfully.
     */
    public String save() {
        if (isPublic) {
            passPhrase = null; // To make sure passPhrase is null in database
        }
        if (hasNullErrors()) {
            return null;
        }
        if (calendarManager.exists(name)) {
            sendErrorMsg("A calendar named '" + name + "' already exists!");
            return null;
        }

        // Set default content in all windows
        List<Window> windows = new ArrayList<>();
        byte[] defaultMessage = "No content has been added to this window.".getBytes();
        for (int i = 1; i <= 24; i++) {
            windows.add(new Window(name, i, defaultMessage, Window.ContentType.TEXT));
        }

        calendar = new Calendar(name, background, thumbnail, passPhrase);
        calendar.setWindows(windows);
        calendarManager.add(calendar);
        sendInfoMsg("Calendar has been added.");

        calendar = calendarManager.getCalendar(name); // Sadly needed to get correct numeric ID
        return "/admin/edit-calendar/index.xhtml?faces-redirect=true&amp;cal=" + calendar.getNumericId();
    }


    /**
     * Checks for <code>null</code> attributes, empty name and (if calendar shall be private) empty passphrase. If any
     * such errors are found,  error message(s) are sent to the current facelet.
     *
     * @return <code>true</code> if any errors are found
     */
    private boolean hasNullErrors() {
        boolean hasErrors = false;

        if (name == null || name.isEmpty()) {
            sendErrorMsg("Invalid name.");
            hasErrors = true;
        }
        if (background == null) {
            sendErrorMsg("Invalid background.");
            hasErrors = true;
        }
        if (thumbnail == null) {
            FacesContext.getCurrentInstance().validationFailed(); // TODO: This needs some testing! Does it work? Can it be made to work? If so, add to all cases?
            sendErrorMsg("Invalid thumbnail.");
            hasErrors = true;
        }
        if (!isPublic && (passPhrase == null || passPhrase.isEmpty())) {
            sendErrorMsg("Invalid pass phrase.");
            hasErrors = true;
        }

        return hasErrors;
    }

    /**
     * Sends the provided msg as an error message using the 'messages' tag on the facelet of the current page.
     *
     * @param msg the error message to be set
     */
    private void sendErrorMsg(String msg) {
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }

    /**
     * Sends the provided msg as an info message using the 'messages' tag on the facelet of the current page.
     *
     * @param msg the info message to be set
     */
    private void sendInfoMsg(String msg) {
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }

}
