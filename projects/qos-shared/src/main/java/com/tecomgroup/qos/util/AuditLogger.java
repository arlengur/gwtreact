package com.tecomgroup.qos.util;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Arrays;

/**
 * Created by stroganov.d on 16.12.2015.
 */
@Component
public class AuditLogger implements InitializingBean{

    private static Logger AUDITLOG = LoggerFactory.getLogger(AuditLogger.class);
    private static Logger APPLOG = LoggerFactory.getLogger("com.tecomgroup.qos.QOS_AUDIT");
    private static String SYSLOG_PREFIX_PATTERN = "{} | {} | {} | {} | {} | ";

    @Value("${qos.audit.enabled}")
    private boolean isAuditEnabled;

    private static boolean auditEnabled;

    public enum SyslogCategory {
        USER,GRAPH,REPORT,NOTIFICATION,ALERT,RBAC,POLICY,VIDEO,PROBE,AUDIT,VISUAL,CONNECT;
    }

    public enum SyslogActionStatus {
        OK, NOK
    }

    public enum Severity {
        CRITICAL, MAJOR, WARNING, INFO, NOTIFY, SECURITY;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        String[] args;
        AuditLogger.auditEnabled=isAuditEnabled;
        if(isAuditEnabled) {
             args = new String[]{"ON"};
        }else{
             args = new String[]{"OFF"};
        }
        try {
            Object[] first = new String[]{Severity.SECURITY.toString(),"unknown" ,"Application",
                    SyslogCategory.AUDIT.toString(), SyslogActionStatus.OK.toString()};
            Object[] params = ArrayUtils.addAll(first, args);
            AUDITLOG.warn(SYSLOG_PREFIX_PATTERN.concat("Server started, AUDIT is : {}"), params);
        } catch (Exception e) {
            APPLOG.error("Unable to log action", e);
        }
    }

    private static String getCurrentUserName() {
        try {
            final Authentication authentication = SecurityContextHolder
                    .getContext().getAuthentication();
            if (authentication != null
                    && !(authentication instanceof AnonymousAuthenticationToken)) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof UserDetails) {
                    UserDetails user = (UserDetails) principal;
                    return user.getUsername();
                }
            }
        }catch (Exception e)
        {
            APPLOG.error("Unable to get user ", e);
        }
        return "Anonymous";
    }

    private static void log(Severity severity,SyslogCategory category, SyslogActionStatus status,
                            String pattern, String... args) {
        try {
            if(auditEnabled) {
                Object[] first = new String[]{severity.toString(), getCurrentSessionId(), getCurrentUserName(),
                        category.toString(), status.toString()};
                Object[] params = ArrayUtils.addAll(first, args);
                switch(severity)
                {
                    case CRITICAL:
                        AUDITLOG.warn(SYSLOG_PREFIX_PATTERN.concat(pattern), params);
                        break;
                    case MAJOR:
                        AUDITLOG.warn(SYSLOG_PREFIX_PATTERN.concat(pattern), params);
                        break;
                    case WARNING:
                        AUDITLOG.warn(SYSLOG_PREFIX_PATTERN.concat(pattern), params);
                        break;
                    case SECURITY:
                        AUDITLOG.warn(SYSLOG_PREFIX_PATTERN.concat(pattern), params);
                        break;
                    case NOTIFY:
                        AUDITLOG.info(SYSLOG_PREFIX_PATTERN.concat(pattern), params);
                        break;
                    case INFO:
                        AUDITLOG.info(SYSLOG_PREFIX_PATTERN.concat(pattern), params);
                        break;
                }

            }
        } catch (Exception e) {
            APPLOG.error("Unable to log action", e);
        }
    }

    public static void loginAction(SyslogActionStatus status, String username, String pattern,
                                   String... args) {
        try {
            if(auditEnabled) 
                {
                    RequestContextHolder.currentRequestAttributes().getSessionId();
                    Object[] first = new String[]{Severity.INFO.toString(), getCurrentSessionId(), username,
                            SyslogCategory.USER.toString(), status.toString()};
                    Object[] params = ArrayUtils.addAll(first, args);
                    AUDITLOG.info(SYSLOG_PREFIX_PATTERN.concat(pattern), params);
                }
        } catch (Exception e) {
            APPLOG.error("Unable to log action", e);
        }
    }


    private static String getCurrentSessionId() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            if (requestAttributes != null) {
                String sessionId = requestAttributes.getSessionId();
                if (sessionId != null) {
                    return sessionId;
                }
            }
        }catch (Exception e){
            APPLOG.error("Unable to get session id",e);
        }
        return "unknown";
    }

    public static void notify(SyslogCategory category, SyslogActionStatus status,
                           String pattern, String... args) {
        log(Severity.NOTIFY,category,status,pattern,args);
    }

    public static void info(SyslogCategory category, SyslogActionStatus status,
                              String pattern, String... args) {
        log(Severity.INFO,category,status,pattern,args);
    }

    public static void warning(SyslogCategory category, SyslogActionStatus status,
                            String pattern, String... args) {
        log(Severity.WARNING,category,status,pattern,args);
    }

    public static void major(SyslogCategory category, SyslogActionStatus status,
                               String pattern, String... args) {
        log(Severity.MAJOR,category,status,pattern,args);
    }

    public static void critical(SyslogCategory category, SyslogActionStatus status,
                             String pattern, String... args) {
        log(Severity.CRITICAL,category,status,pattern,args);
    }

    public static void security(SyslogCategory category, SyslogActionStatus status,
                                String pattern, String... args) {
        log(Severity.SECURITY,category,status,pattern,args);
    }
}
