package se.lnu.xmascal;

import org.primefaces.model.ByteArrayContent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * @author Johan Widén.
 */
@FacesConverter("se.lnu.xmascal.TextConverter")
public class TextConverter implements Converter{
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        return "Hejsan";
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        ByteArrayContent stream = (ByteArrayContent) o;
        return "Hej " + stream.toString() + "      ";
    }
}
