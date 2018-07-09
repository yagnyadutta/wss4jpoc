package com.poc.test.server;

import org.springframework.context.ApplicationContext;

import javax.management.MBeanServer;
import javax.servlet.Servlet;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Simple abstraction of an embedded servlet server (e.g. Jetty or Tomcat).
 * <p/>
 * Note: any exceptions thrown are treated as assertion failures.
 * <p/>
 * This version is one that EMC extended to support multiple connectors (one listening on a TLS port and one listening
 * on a non-TLS port.
 *
 * @author Jens Riemschneider
 */
abstract class ServletServer
{
    private Servlet servlet;
    private int port;
    private int securePort;
    private String contextPath;
    private String servletPath;
    private String contextResource;
    private String keyStoreFile;
    private String keyStorePassword;
    private String trustStoreFile;
    private String trustStorePassword;
    private ApplicationContext parentContext;
    private MBeanServer mBeanServer;

    /**
     * Starts the server.
     * <p/>
     * Note: any exceptions thrown are treated as assertion failures.
     */
    public abstract void start ();

    /**
     * Stops the server if it was running.
     * <p/>
     * Note: any exceptions thrown are treated as assertion failures.
     */
    public abstract void stop ();

    /**
     * @param servletPath the path of the servlet itself (including patterns if needed).
     */
    public void setServletPath (String servletPath)
    {
        notNull(servletPath, "servletPath cannot be null");
        this.servletPath = servletPath;
    }

    /**
     * @return the path of the servlet itself (including patterns if needed).
     */
    public String getServletPath ()
    {
        return servletPath;
    }

    /**
     * @param contextPath the path of the context in which the servlet is running.
     */
    public void setContextPath (String contextPath)
    {
        notNull(contextPath, "contextPath cannot be null");
        this.contextPath = contextPath;
    }

    /**
     * @return the path of the context in which the servlet is running.
     */
    public String getContextPath ()
    {
        return contextPath;
    }

    /**
     * @param port the port that the server is started on.
     */
    public void setPort (int port)
    {
        this.port = port;
    }

    /**
     * @return the port that the server is started on.
     */
    public int getPort ()
    {
        return port;
    }

    /**
     * @param securePort the port that the server is started on.
     */
    public void setSecurePort (int securePort)
    {
        this.securePort = securePort;
    }

    /**
     * @return the port that the server is started on.
     */
    public int getSecurePort ()
    {
        return securePort;
    }

    /**
     * The servlet to use within the servlet.
     *
     * @param servlet the servlet.
     */
    public void setServlet (Servlet servlet)
    {
        notNull(servlet, "servlet cannot be null");
        this.servlet = servlet;
    }

    /**
     * @return the servlet to use within the servlet.
     */
    public Servlet getServlet ()
    {
        return servlet;
    }

    /**
     * @return key store location.
     */
    public String getKeyStoreFile ()
    {
        return keyStoreFile;
    }

    /**
     * @param keyStoreFile key store location.
     */
    public void setKeyStoreFile (String keyStoreFile)
    {
        this.keyStoreFile = keyStoreFile;
    }

    /**
     * @return key store password.
     */
    public String getKeyStorePassword ()
    {
        return keyStorePassword;
    }

    /**
     * @param keyStorePassword key store password.
     */
    public void setKeyStorePassword (String keyStorePassword)
    {
        this.keyStorePassword = keyStorePassword;
    }

    /**
     * @return trust store location.
     */
    public String getTrustStoreFile ()
    {
        return trustStoreFile;
    }

    /**
     * @param trustStoreFile trust store location.
     */
    public void setTrustStoreFile (String trustStoreFile)
    {
        this.trustStoreFile = trustStoreFile;
    }

    /**
     * @return trust store password.
     */
    public String getTrustStorePassword ()
    {
        return trustStorePassword;
    }

    /**
     * @param trustStorePassword trust store password.
     */
    public void setTrustStorePassword (String trustStorePassword)
    {
        this.trustStorePassword = trustStorePassword;
    }

    /**
     * @return location of a spring application context to be started with the web-app.
     */
    public String getContextResource ()
    {
        return contextResource;
    }

    /**
     * @param contextResource location of a spring application context to be started with the web-app.
     */
    public void setContextResource (String contextResource)
    {
        this.contextResource = contextResource;
    }

    /**
     * @return the parent spring context (if any) for the spring application context to be started with
     * the web-app.
     */
    public ApplicationContext getParentContext ()
    {
        return parentContext;
    }

    /**
     * @param parentContext the parent spring context (if any) for the spring application context to be started with
     * the web-app.
     */
    public void setParentContext (ApplicationContext parentContext)
    {
        this.parentContext = parentContext;
    }

    public MBeanServer getMBeanServer ()
    {
        return mBeanServer;
    }

    public void setMBeanServer (MBeanServer mBeanServer)
    {
        this.mBeanServer = mBeanServer;
    }
}