package se.lnu.xmascal;

import org.primefaces.event.FileUploadEvent;
import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.Calendar;
import se.lnu.xmascal.model.Window;

import javax.annotation.PreDestroy;
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


// <!-- <h:selectOneRadio id="isPublic" value="#{newCalendar.public}" onchange="submit()" valueChangeListener="#{newCalendar.update}"> TODO: This will submit entire form? Change to separate form? -->
//<!-- <h:inputText id="passPhrase" value="#{newCalendar.passPhrase}" disabled="#{newCalendar.public}"/> -->

/**
 * This class is a ViewScoped Managed Bean for the Calendar class.
 *
 * @author Jerry Strand
 */
//@DeclareRoles("XmasCalAdmin")
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

    // TODO: Save Calendar in DB when next button is clicked (needed to get ID used for redirection to edit page).
    // Set baseConfigured to true when save button is clicked. Add @PreDestroy method to this bean to remove calendar from DB if baseConfigured is false
    private boolean baseConfigured = false;

    @PreDestroy
    private void cleanUp() { // TODO: Actually, this should be done when the save button is clicked?
        if (!baseConfigured) { // Name, background, thumbnail weren't entered
            // TODO: Remove from DB, set background/thumbnail to null
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getPassPhrase() {
        return passPhrase;
    }

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
            // event.getFile().getContents(); // TODO: Not needed?
            thumbnail = getUploadedBytes(event.getFile().getInputstream()); // TODO: Test what happens if Save is clicked while upload is being done
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
        is.read(data);
        is.close();
        return data;
    }

    /**
     * Adds the managed calendar. Error messages are sent to the current facelet if any attributes are null and if a
     * calendar with the set name exists. An info message is sent if the calendar was added successfully.
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

        calendar = new Calendar(name, background, thumbnail, passPhrase);
        List<Window> windows = new ArrayList<>();
        byte[] defaultMessage = "No content has been added to this window.".getBytes();
        for (int i = 1; i <= 24; i++) {
            windows.add(new Window(calendar.getName(), i, defaultMessage, Window.ContentType.TEXT));
        }
        calendar.setWindows(windows);

        calendarManager.add(calendar);
        sendInfoMsg("Calendar has been added.");

        calendar = calendarManager.getCalendar(name); // Needed to get correct numeric ID
        String outcome = "/admin/edit-calendar/index.xhtml?faces-redirect=true&amp;cal=" + calendar.getNumericId(); //
        System.out.println("Returning outcome " + outcome);
        return outcome; // TODO: Update this to /admin/edit-calendar/ -- also need to pass parameter detailing which calendar
    }


    /**
     * Checks for <code>null</code> attributes and if found, sends error message(s) to the current facelet.
     *
     * @return <code>true</code> if any attributes are <code>null</code>
     */
    private boolean hasNullErrors() {
        boolean hasErrors = false;

        if (name == null || name.isEmpty()) { // TODO: Need better validation: empty String? Too short? Etc
            sendErrorMsg("Invalid name.");
            hasErrors = true;
        }
        if (background == null) {// TODO: Need better validation: empty String? Too short? Etc
            sendErrorMsg("Invalid background.");
            hasErrors = true;
        }
        if (thumbnail == null) {// TODO: Need better validation: empty String? Too short? Etc
            FacesContext.getCurrentInstance().validationFailed();
            // Add an appropriate faces message which can be displayed in a message component which autoupdates for instance.
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
/*
    // TODO: Try to make this happen client side instead?
    public String onFlowProcess(FlowEvent event) {
        System.out.println("New step: " + event.getNewStep());
        System.out.println(name + passPhrase);
        return event.getNewStep();
    }

    public void update(ValueChangeEvent event) {
        System.out.print("isPublic was '" + isPublic + "'");
        isPublic = (Boolean) event.getNewValue();
        System.out.println(", now changed to '" + isPublic + "'");
    }

    /**
     * Updates the managed calendar. Error messages are sent to the current facelet if any attributes are null. An info
     * message is sent if the calendar was updated successfully.
     */
// public void update(ValueChangeEvent e) TODO: Might be able to use this instead/in conjunction?
//public void update() { // TODO: calendar.set... may not work, since the Calendar may be detached. Need to use em.merge
//    if (hasNullErrors()) {
//        return;
//    }
//    if (calendar == null) {
//        calendar = calendarManager.getCalendar(name);
//    }
//    calendar.setName(name);
//    calendar.setBackground(background);
//    calendar.setThumbnail(thumbnail);
//    calendar.setPassPhrase(passPhrase);
//    sendInfoMsg("Calendar has been updated.");
//}

    /*
     *
     *
    public void remove() { // TODO: Not needed?
        if (name == null) {
            sendErrorMsg("Name of a calendar is needed to remove it!");
        } else if (!calendarManager.exists(name)) {
            sendErrorMsg("No calendar with the name '" + name + "' exists!");
        } else if (calendar == null) {
            calendar = calendarManager.getCalendar(name);
        }
        calendarManager.remove(calendar);
        name = null;
        background = null;
        thumbnail = null;
        passPhrase = null;
        sendInfoMsg("Calendar has been removed.");
    }
        */