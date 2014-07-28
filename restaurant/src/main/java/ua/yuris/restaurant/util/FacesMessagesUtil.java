package ua.yuris.restaurant.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 27.07.14
 * Time: 15:48
 * To change this template use File | Settings | File Templates.
 */
public class FacesMessagesUtil {

    public static void addInfoMessageToFacesContext(String summary, String detail) {
        FacesMessage msg = new FacesMessage(summary, detail);
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public static void addInfoMessageToFacesContext(String summary) {
        addInfoMessageToFacesContext(summary, summary);
    }

    public static void addErrorMessageToFacesContext(String summary, String detail) {
        FacesMessage msg = new FacesMessage(summary, detail);
        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public static void addErrorMessageToFacesContext(String summary) {
        addErrorMessageToFacesContext(summary, summary);
    }

}
