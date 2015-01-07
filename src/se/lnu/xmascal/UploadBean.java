package se.lnu.xmascal;

import org.primefaces.event.FileUploadEvent;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class handles the file uploading
 *
 * @author Johan Widén
 */
@ViewScoped
@ManagedBean
public class UploadBean {

    public void handleFileUpload(FileUploadEvent event) {
        try {
            handleFile(event.getFile().getFileName(), event.getFile().getInputstream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void handleFile(String fileName, InputStream is) {
        try {
            byte[] data = new byte[is.available()];
            is.read(data);
            is.close();
            // TODO Send fileName and data to the database or somewhere...
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}