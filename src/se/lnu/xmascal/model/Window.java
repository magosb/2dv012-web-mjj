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

    @Id     // TODO: Both day and calendar_name make the key. Do two @Id work like this?
    @Column(name = "day")
    private int day;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Column(name = "content")
    private byte[] content;

    @Column(name = "type")
    private String type; // TODO: Change this to ContentType and see if translation to String happens automatically

    /*
    @ManyToOne
    @JoinColumn(name="name")
    private Calendar calendar;

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
    */

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
        //this.calendar = new Calendar(); this.calendar.setName(calendarName); // TODO: Remove this after testing
        this.day = day;
        this.content = content;
        this.type = type.toString();
    }

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public enum ContentType {
        PICTURE("picture"),
        AUDIO("audio"),
        VIDEO("video"),
        URL("URL"),
        TEXT("text");

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