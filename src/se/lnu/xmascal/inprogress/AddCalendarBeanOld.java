package se.lnu.xmascal.inprogress;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.Calendar;
import se.lnu.xmascal.model.Window;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


// <!-- <h:selectOneRadio id="isPublic" value="#{newCalendar.public}" onchange="submit()" valueChangeListener="#{newCalendar.update}"> TODO: This will submit entire form? Change to separate form? -->
//<!-- <h:inputText id="passPhrase" value="#{newCalendar.passPhrase}" disabled="#{newCalendar.public}"/> -->
/**
 * This class is a ViewScoped Managed Bean for the Calendar class.
 *
 * @author Jerry Strand
 */
//@DeclareRoles("XmasCalAdmin")
@Named("newCalendarOld")
@ViewScoped
public class AddCalendarBeanOld implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private CalendarManager calendarManager;
    private String name;
    private byte[] background;
    private byte[] thumbnail;
    private String passPhrase;
    private Calendar calendar = new Calendar();
    private boolean isPublic = true;


    // Change the current date here:
    private final int CURRENT_DATE = 4;
    private java.util.Calendar currentDate = new GregorianCalendar();

    // TODO: Will probably need to keep a list of Windows here, that the admin has added on Add Calendar page

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

    public int getWindowNumber() {
        return windowNumber;
    }

    public void setWindowNumber(int windowNumber) {
        this.windowNumber = windowNumber;
    }

    public void handleBackgroundUpload(FileUploadEvent event) {
        try {
            background = handleFile(event.getFile().getInputstream());
        } catch (IOException e) {
            e.printStackTrace(); // TODO: Send error message to client
        }
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void handleThumbnailUpload(FileUploadEvent event) {
        try {
            event.getFile().getContents();
            thumbnail = handleFile(event.getFile().getInputstream());
        } catch (IOException e) {
            e.printStackTrace(); // TODO: Send error message to client
        }
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    // --------------------------------------
    // WINDOW CONFIG RELATED --------------->
    private int windowNumber;
    private byte[] windowContent;
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

    public void handleContentUpload(FileUploadEvent event) {
        System.out.println("UploadedFile.getContentType(): " + event.getFile().getContentType());
        try {
            windowContent = handleFile(event.getFile().getInputstream());
        } catch (IOException e) {
            e.printStackTrace(); // TODO: Send error message to client
        }
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void saveWindowContent() {
        String calName = calendarManager.getName(Long.parseLong(name));
        Window window = new Window(calName, windowNumber, windowContent, contentType);
        System.out.println(contentType);
        // TODO: THIS WILL FAIL IF THE WINDOW HAS ALREADY BEEN ADDED!
        calendarManager.addWindow(window);
        //calendar = calendarManager.update(calendar); // TODO: Does it matter if calendar is assigned the returned one? Returned calendar is detached?
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
    // <----------  END WINDOW CONFIG RELATED
    // --------------------------------------

    /**
     *
     * @param is
     * @return
     */
    private byte[] handleFile(InputStream is) {
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


    public List<Calendar> getAllCalendars() {
        return calendarManager.getAllCalendars();
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
            calendar = new Calendar(name, background, thumbnail, passPhrase);
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
     * This method returns the current date. Notice this methods is also used to manuplitate which current date it
     * currently is.
     *
     * @author Johan Widén
     * @return int with the current date.
     */
    public int getCurrentDate() {
        currentDate.set(java.util.Calendar.YEAR, 2014);
        currentDate.set(java.util.Calendar.MONTH, 11);
        currentDate.set(java.util.Calendar.DAY_OF_MONTH, CURRENT_DATE);
        return currentDate.get(java.util.Calendar.DATE);
    }



}
