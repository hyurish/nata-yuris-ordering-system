package ua.yuris.restaurant.web;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 22.05.14
 * Time: 15:20
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@RequestScoped
public class LoginBackingBean implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(LoginBackingBean.class);

    public LoginBackingBean() {
    }

    public String doLogin() throws ServletException, IOException {
        String path = "/j_spring_security_check";
        forward(path);
        return null;
    }

    public String doLogout() throws ServletException, IOException {
        String path = "/j_spring_security_logout";
        forward(path);
        return null;
    }

    private void forward(String path) throws ServletException, IOException {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

        RequestDispatcher dispatcher = ((ServletRequest) context.getRequest())
                .getRequestDispatcher(path);

        dispatcher.forward((ServletRequest) context.getRequest(),
                (ServletResponse) context.getResponse());

        FacesContext.getCurrentInstance().responseComplete();
    }
}
