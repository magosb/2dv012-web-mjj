package se.lnu.xmascal.inprogress;

import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;
import se.lnu.xmascal.DataService;
import se.lnu.xmascal.ejb.CalendarManager;
import se.lnu.xmascal.model.Calendar;
import se.lnu.xmascal.model.Window;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * @author Johan Widén
 * @author Jerry Strand
 */
@Named
@ViewScoped
public class ViewWindowBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private Calendar calendar;
    @EJB
    private CalendarManager calendarManager;

    @Inject
    private DataService dataService;

    private StreamedContent mediaContent;
    private int date;
    private String text;
    private String url;

    public ViewWindowBean() {
    }

    public void preRenderListen(ComponentSystemEvent event) throws AbortProcessingException {
        if(calendar == null) {
            String calName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("cal");
            calendar = calendarManager.getCalendar(calName);
        }
    }

    public String getName() {
        return calendar.getName();
    }

    public void setName(String name) {
        calendar.setName(name);
    }

    public List<Window> getWindows() {
        return calendar.getWindows();
    }

    public void open() {
        String calName = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("cal");
        setDate(Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("day")));
        String type = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("type");
        System.out.println("open calender: " + calName +" window: " + date + " type: " + type);

        if(type.equals(Window.ContentType.TEXT.toString())) {
            try {
                setText(dataService.getTextContent(calName, date));
                System.out.println("TEXT is: " + text);
            } catch (Exception e) {
                setText("No data for this window.");
            }
        } else if (type.equals(Window.ContentType.URL.toString())){
            try {
                setUrl(dataService.getTextContent(calName, date));
                System.out.println("URL is: " + url);
            } catch (Exception e) {
                setUrl("No data for this window.");
            }
        } else if (type.equals(Window.ContentType.VIDEO.toString())) {
            try {
                setMediaContent(dataService.getStreamedContent());
                System.out.println("MediaV is: " + mediaContent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equals(Window.ContentType.PICTURE.toString())) {
            try {
                setMediaContent(dataService.getStreamedContent());
                System.out.println("MediaP is: " + mediaContent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public StreamedContent getMediaContent(){
        System.out.println("getMediaContent: " + mediaContent);
        return mediaContent;
    }

    public void setMediaContent(StreamedContent mediaContent) {
        System.out.println("setMediaContent: " + mediaContent);
        this.mediaContent = mediaContent;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
