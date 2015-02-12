package se.lnu.xmascal.inprogress;

import org.primefaces.event.FileUploadEvent;
import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.*;
import se.lnu.xmascal.model.Calendar;

import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.*;
import java.util.*;

/**
 *
 * THIS CLASS WILL BE CLEARED UP! ;)
 *
 * @author Johan Widén
 */
@Named("editCalendar")
@ViewScoped
public class EditCalendarBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private CalendarManager calendarManager;
    private Calendar calendar;
    private Boolean isPublic;
    private String oldName;
    private String text;
    private int day;

    public void preRenderListen(ComponentSystemEvent event) throws AbortProcessingException {
        if(calendar == null) {
            Long cal = Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("cal"));
            calendar = calendarManager.getCalendar(cal);
            isPublic = !calendar.isPrivate();
            oldName = calendar.getName();
        }
    }

    public Long getId() {
        return calendar.getNumericId();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // --------------------------------------
    // WINDOW CONFIG RELATED --------------->
    private int windowNumber;
    private byte[] windowContent = new byte[1];
    private Window.ContentType contentType = Window.ContentType.PICTURE;
    private static Map<String, Window.ContentType> contentTypeItems;
    static {
        contentTypeItems = new LinkedHashMap<>();
        contentTypeItems.put(Window.ContentType.PICTURE.toString(), Window.ContentType.PICTURE);
        contentTypeItems.put(Window.ContentType.VIDEO.toString(), Window.ContentType.VIDEO);
        contentTypeItems.put(Window.ContentType.AUDIO.toString(), Window.ContentType.AUDIO); // label, value
        contentTypeItems.put(Window.ContentType.URL.toString(), Window.ContentType.URL);
        contentTypeItems.put(Window.ContentType.TEXT.toString(), Window.ContentType.TEXT);
    }

    public void updateDay() {
        day = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("win"));
        System.out.println("UPDATE DAY: " + day);
    }


    /**
     * Saves the window content
     */
    public void saveWindowContent() {
        Window window;
        if(contentType.equals(Window.ContentType.PICTURE) || contentType.equals(Window.ContentType.VIDEO) || contentType.equals(Window.ContentType.AUDIO)) {
            window = new Window(calendar.getName(), day, windowContent, contentType);
        } else if(contentType.equals(Window.ContentType.URL)) {
            String url;
            if(text.startsWith("www")) {
                url = "http://" + text;
            } else if(text.startsWith("http://")) {
                url = text;
            } else {
                url = "http://www." + text;
            }
            window = new Window(calendar.getName(), day, url.getBytes(), contentType);
        } else {
            window = new Window(calendar.getName(), day, text.getBytes(), contentType);
        }
        calendarManager.updateWindow(window);
    }

    public void handleContentUpload(FileUploadEvent event) {
        System.out.println("UploadedFile.getContentType(): " + event.getFile().getContentType());
        try {
            windowContent = getUploadedBytes(event.getFile().getInputstream());
        } catch (IOException e) {
            e.printStackTrace(); // TODO: Send error message to client
        }
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public int getWindowNumber() {
        return windowNumber;
    }

    public void setWindowNumber(int windowNumber) {
        this.windowNumber = windowNumber;
    }


    public Window.ContentType getContentType() {
        return contentType;
    }

    public void setContentType(Window.ContentType contentType) {
        this.contentType = contentType;
    }

    public Map<String, Window.ContentType> getContentTypeItems() {
        return contentTypeItems;
    }

    public List<Window> getWindows() {
        return calendar.getWindows();
    }

    /**
     *
     * COPY FORM ADDCALENDARBEAN
     *
     */



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
        return calendar.getName();
    }

    public void setName(String name) {
        calendar.setName(name);
    }

    public boolean getPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getPassPhrase() {
        return calendar.getPassPhrase();
    }

    public void setPassPhrase(String passPhrase) {
        calendar.setPassPhrase(passPhrase);
    }

    /**
     * This method retrieves the uploaded background picture in binary format.
     *
     * @param event the <code>FileUploadEvent</code> that triggers the handling of background picture upload
     */
    public void handleBackgroundUpload(FileUploadEvent event) {
        try {
            calendar.setBackground(getUploadedBytes(event.getFile().getInputstream()));
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
            calendar.setThumbnail(getUploadedBytes(event.getFile().getInputstream())); // TODO: Test what happens if Save is clicked while upload is being done
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
    public void save() {
        if (getPublic()) {
            calendar.setPassPhrase(null); // To make sure passPhrase is null in database
        }
        if (hasNullErrors()) {
            System.out.println("errors");
        } else if (!calendar.getName().equals(oldName)) {
            if(calendarManager.exists(calendar.getName())) {
                sendErrorMsg("A calendar named '" + calendar.getName() + "' already exists!");
            } else {
                // TODO CHANGE TO RENAME Method.
                //calendarManager.renameCalendar(calendar.getNumericId(), calendar.getName());
                calendarManager.renameCalendar(calendar.getNumericId(), calendar.getName());
            }
        } else {
            calendarManager.update(calendar);
            sendInfoMsg("Calendar has been updated.");
        }
    }


    /**
     * Checks for <code>null</code> attributes and if found, sends error message(s) to the current facelet.
     *
     * @return <code>true</code> if any attributes are <code>null</code>
     */
    private boolean hasNullErrors() {
        boolean hasErrors = false;

        if (calendar.getName() == null || calendar.getName().isEmpty()) { // TODO: Need better validation: empty String? Too short? Etc
            sendErrorMsg("Invalid name.");
            hasErrors = true;
        }
        if (calendar.getBackground() == null) {// TODO: Need better validation: empty String? Too short? Etc
            sendErrorMsg("Invalid background.");
            hasErrors = true;
        }
        if (calendar.getThumbnail() == null) {// TODO: Need better validation: empty String? Too short? Etc
            FacesContext.getCurrentInstance().validationFailed();
            // Add an appropriate faces message which can be displayed in a message component which autoupdates for instance.
            sendErrorMsg("Invalid thumbnail.");
            hasErrors = true;
        }
        if (!getPublic() && (calendar.getPassPhrase() == null || calendar.getPassPhrase().isEmpty())) {
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
