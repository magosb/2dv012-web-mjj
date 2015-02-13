package se.lnu.xmascal.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * This class represents a Window of a Calendar in the Christmas Calendar web application.
 *
 * @author Jerry Strand
 */
@Entity
@Table(name = "window")
@NamedQuery(name = "Window.findAll", query = "SELECT w FROM Window w")
public class Window implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "calendar_name")
    private String calendarName;

    @Id
    @Column(name = "day")
    private int day;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Column(name = "content")
    private byte[] content;

    @Column(name = "type")
    private String type;

    public Window() {
    }

    /**
     * @param calendarName the name of the <code>Calendar</code> that this <code>Window</code> belongs to
     * @param day the day on which this <code>Window</code> should no longer be locked
     * @param content the content of the <code>Window</code>
     * @param type the type of the content
     */
    public Window(String calendarName, int day, byte[] content, ContentType type) {
        this.calendarName = calendarName;
        this.day = day;
        this.content = content;
        this.type = type.toString();
    }

    /**
     * @return the name of the <code>Calendar</code> that this <code>Window</code> belongs to
     */
    public String getCalendarName() {
        return calendarName;
    }

    /**
     * @param calendarName the name of the <code>Calendar</code> that this <code>Window</code> shall belong to
     */
    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    /**
     * @return the day on which this <code>Window</code> will no longer be locked
     */
    public int getDay() {
        return day;
    }

    /**
     * @return the day on which this <code>Window</code> shall no longer be locked
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * @return binary content data of this <code>Window</code>
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @param content binary content data that this <code>Window</code> shall contain
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * @return the type of content in this <code>Window</code>
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type of content that this <code>Window</code> shall contain
     */
    public void setType(String type) {
        this.type = type;
    }

    public enum ContentType {
        PICTURE("Picture"),
        AUDIO("Audio"),
        VIDEO("Video"),
        URL("URL"),
        TEXT("Text");

        private String type;
        ContentType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Window)) return false;

        Window window = (Window) o;
        if (day != window.day) return false;
        if (!calendarName.equals(window.calendarName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = day;
        result = result * 31 + (calendarName == null ? 0 : calendarName.hashCode());
        return result;
    }

}