package ua.yuris.restaurant.security;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * Created with IntelliJ IDEA.
 * User: yuris
 * Date: 27.05.14
 * Time: 2:48
 * To change this template use File | Settings | File Templates.
 */
public class MySimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {
    private static final Logger LOG =
            LoggerFactory.getLogger(MySimpleUrlLogoutSuccessHandler.class);
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication)
            throws IOException, ServletException {
        handle(request, response, authentication);
    }

    protected void handle(HttpServletRequest request, HttpServletResponse response,
                          Authentication authentication)
            throws IOException {

        String targetUrl = determineTargetUrl(authentication);


        if (response.isCommitted()) {
            LOG.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (roles.contains("ROLE_USER")) {
            return "/menu.xhtml";
        } else if (roles.contains("ROLE_COOK")) {
            return "/admin.xhtml";
        } else if (roles.contains("ROLE_WAITER")) {
            return "/admin.xhtml";
        } else if (roles.contains("ROLE_ADMIN")) {
            return "/admin.xhtml";
        } else if (roles.contains("ROLE_SECURITY_OFFICER")) {
            return "/admin.xhtml";
        } else {
            return "/";
        }
    }

    public RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }
}
