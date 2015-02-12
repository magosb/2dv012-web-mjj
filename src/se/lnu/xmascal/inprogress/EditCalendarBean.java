package se.lnu.xmascal.inprogress;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.*;
import se.lnu.xmascal.model.Calendar;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ValueChangeEvent;
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

    private String text;
    private int day;

    public void preRenderListen(ComponentSystemEvent event) throws AbortProcessingException {
        if(calendar == null) {
            Long cal = Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("cal"));
            calendar = calendarManager.getCalendar(cal);
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

    public void handleContentUpload(FileUploadEvent event) {
        System.out.println("UploadedFile.getContentType(): " + event.getFile().getContentType());
        try {
            windowContent = handleFileUpload(event.getFile().getInputstream());
        } catch (IOException e) {
            e.printStackTrace(); // TODO: Send error message to client
        }
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void saveWindowContent() {
        System.out.println(calendar.getName() + "  " + day + "  " + windowContent + "   " + contentType + "  " + text);
        Window window;
        if(contentType.equals(Window.ContentType.PICTURE) || contentType.equals(Window.ContentType.VIDEO) || contentType.equals(Window.ContentType.AUDIO)) {
            window = new Window(calendar.getName(), day, windowContent, contentType);
        } else{
            window = new Window(calendar.getName(), day, text.getBytes(), contentType);
        }
        calendarManager.updateWindow(window);
        //calendar = calendarManager.update(calendar); // TODO: Does it matter if calendar is assigned the returned one? Returned calendar is detached?
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

    @EJB
    private CalendarManager calendarManager;
    private String name;
    private byte[] background;
    private byte[] thumbnail;
    private String passPhrase;
    private Calendar calendar;
    private boolean isPublic = true;

    // TODO: Save Calendar in DB when next button is clicked (needed to get ID used for redirection to edit page).
    // Set saved to true when save button is clicked. Add @PreDestroy method to this bean to remove calendar from DB if saved is false
    private boolean saved = false;

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
     *
     * @param is
     * @return
     */
    private byte[] handleFileUpload(InputStream is) {
        byte[] data = null;
        try {
            data = new byte[is.available()]; // TODO: THIS MIGHT NOT READ ALL BYTES!!
            is.read(data);
            is.close();
            // TODO Send fileName and data to the database or somewhere...
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

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
    public void update() { // TODO: calendar.set... may not work, since the Calendar may be detached. Need to use em.merge
        if (hasNullErrors()) {
            return;
        }
        if (calendar == null) {
            calendar = calendarManager.getCalendar(name);
        }
        calendar.setName(name);
        calendar.setBackground(background);
        calendar.setThumbnail(thumbnail);
        calendar.setPassPhrase(passPhrase);
        sendInfoMsg("Calendar has been updated.");
    }

    /**
     * Adds the managed calendar. Error messages are sent to the current facelet if any attributes are null and if a
     * calendar with the set name exists. An info message is sent if the calendar was added successfully.
     */
    public String save() {
        if (hasNullErrors()) {
            return null;
        }
        if (calendarManager.exists(name)) {
            sendErrorMsg("A calendar named '" + name + "' already exists!");
        } else {
            calendar = new se.lnu.xmascal.model.Calendar(name, background, thumbnail, passPhrase);
            calendarManager.add(calendar);
            sendInfoMsg("Calendar has been added.");
        }
        System.out.println("Returning edit outcome");
        return "edit"; // TODO: Update this to /admin/edit-calendar/ -- also need to pass parameter detailing which calendar
    }

    /**
     *
     */
    public void remove() {
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
            sendErrorMsg("Invalid thumbnail.");
            hasErrors = true;
        }
        if (passPhrase == null || passPhrase.isEmpty()) {// TODO: Need better validation: empty String? Too short? Etc
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
        FacesContext.getCurrentInstance().addMessage("", facesMessage); // TODO: Do we need to specify anything for ""?
    }

    /**
     * Sends the provided msg as an info message using the 'messages' tag on the facelet of the current page.
     *
     * @param msg the info message to be set
     */
    private void sendInfoMsg(String msg) {
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null);
        FacesContext.getCurrentInstance().addMessage("", facesMessage); // TODO: Do we need to specify anything for ""?
    }

}
