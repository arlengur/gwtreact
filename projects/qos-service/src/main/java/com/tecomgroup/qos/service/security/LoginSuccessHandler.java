package com.tecomgroup.qos.service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

/**
 * Created by galin.a
 */
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String LOCALE = "locale";

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
        SavedRequest savedRequest = this.requestCache.getRequest(request, response);
        String locationUrl;
        if (savedRequest == null) {
            // Get default location url based on context path
            String contextPath = request.getContextPath();
            if (contextPath.startsWith("/")) {
                locationUrl = contextPath;
            } else {
                locationUrl = "/" + contextPath;
            }
        } else {
            URL targetUrl = new URL(savedRequest.getRedirectUrl());
            StringBuilder customURL = new StringBuilder();
            customURL.append(targetUrl.getPath()).append("?");
            if (targetUrl.getQuery() == null) {
                customURL.append(LOCALE).append("=").append(request.getParameter(LOCALE));
            } else if (targetUrl.getQuery().contains(LOCALE)) {
                customURL.append(
                        targetUrl.getQuery().replaceAll("locale=..", "locale=" + request.getParameter(LOCALE))
                );
            } else {
                customURL.append(LOCALE).append("=").append(request.getParameter(LOCALE)).append("&").append(targetUrl.getQuery().replaceAll("&hash=", ""));
            }
            customURL.append(request.getParameter("hash") != null ? request.getParameter("hash") : "");
            locationUrl = customURL.toString();
        }

        response.addHeader("Location", locationUrl);
        clearAuthenticationAttributes(request);
    }
}
