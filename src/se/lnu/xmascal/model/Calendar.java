package se.lnu.xmascal.model;

import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.StreamedContent;

import javax.persistence.*;
import java.util.List;

/**
 * This class represents a Calendar in the Christmas Calendar web application.
 *
 * @author Jerry Strand
 */
@Entity
@Table(name = "calendar")
@NamedQuery(name = "Calendar.findAll", query = "SELECT c FROM Calendar c")
public class Calendar {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "name")
    private String name;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Column(name = "background")
    private byte[] background;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Column(name = "thumbnail")
    private byte[] thumbnail;

    @Column(name = "pass_phrase")
    private String passPhrase;

    @OneToMany(mappedBy="calendarName", fetch=FetchType.LAZY, cascade={CascadeType.MERGE, CascadeType.REFRESH}) // TODO: Are these cascades correct?
    private List<Window> windows;

    public Calendar() {
    }

    /**
     * @param name the name of the code>Calendar</code>
     * @param background the background picture data of the code>Calendar</code>
     * @param thumbnail the thumbnail picture data of the code>Calendar</code>
     * @param passPhrase the pass phrase of the code>Calendar</code>
     */
    public Calendar(String name, byte[] background, byte[] thumbnail, String passPhrase) {
        this.name = name;
        this.background = background;
        this.thumbnail = thumbnail;
        this.passPhrase = passPhrase;
    }
    // TODO: Add isPrivate() { private == null }
    /**
     * @return the name of this <code>Calendar</code>
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set for this code>Calendar</code>
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the background picture data of this <code>Calendar</code>
     */
    public byte[] getBackground() {
        return background;
    }

    /**
     * @param background the background picture data to set for this code>Calendar</code>
     */
    public void setBackground(byte[] background) {
        this.background = background;
    }

    /**
     * @return the thumbnail picture data of this <code>Calendar</code>
     */
    public byte[] getThumbnail() {
        return thumbnail;
    }

    /**
     * @param thumbnail the thumbnail picture data to set for this <code>Calendar</code>
     */
    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * @return the pass phrase of this <code>Calendar</code>
     */
    public String getPassPhrase() {
        return passPhrase;
    }

    /**
     * @param passPhrase the pass phrase to set for this code>Calendar</code>
     */
    public void setPassPhrase(String passPhrase) {
        this.passPhrase = passPhrase;
    }

    /**
     * @return <code>true</code> if this calendar is private, otherwise <code>false</code>
     */
    public boolean isPrivate() {
        return (passPhrase == null || passPhrase.isEmpty());
    }

    /**
     * @return the <code>Windows</code> of this <code>Calendar</code>
     */
    public List<Window> getWindows() {
        return windows;
    }

    /**
     * @param windows the <code>Window</code>s to set for this <code>Calendar</code>
     */
    public void setWindows(List<Window> windows) {
        this.windows = windows;
    }

    /**
     *
     * @return
     */
    public StreamedContent getBackgroundContent() {
        return new ByteArrayContent(background);
    }

    /**
     *
     * @return
     */
    public StreamedContent getThumbnailContent() {
        return new ByteArrayContent(thumbnail);
    }
}
