package com.poc.test.server;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;

/**
 * A servlet server based on Jetty.
 * <p/>
 * This is our own version of the JettyServer class that is part of the IPF XDS test framework. This extended version
 * handles the case of multiple connectors (one to listen on a secure port and one to listen on a non-secure port)
 */
class JettyServer extends ServletServer
{
    private Server server;

    @Override
    @SuppressWarnings("unchecked")
    public void start ()
    {
        if (getPort() == 0 && getSecurePort() == 0)
            throw new IllegalStateException("No ports have been configured");
        if (getPort() == getSecurePort())
            throw new IllegalStateException("Secure and non-secure port can not be the same");

        server = new Server();

        if (getMBeanServer() != null)
        {
            MBeanContainer mbeanContainer = new MBeanContainer(getMBeanServer());
            server.getContainer().addEventListener(mbeanContainer);
            server.addBean(mbeanContainer);
            mbeanContainer.addBean(Log.getLog());
        }

        if (getPort() != 0)
            server.addConnector(createConnector(getPort()));

        if (getSecurePort() != 0)
            server.addConnector(createSecureConnector(getSecurePort()));

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setResourceBase("/");
        ContextLoaderListener listener = new ContextLoaderListener();

        if (getParentContext() != null)
            context.getInitParams().put(ContextLoader.LOCATOR_FACTORY_KEY_PARAM, getParentContext().getId());
        context.getInitParams().put(ContextLoader.CONFIG_LOCATION_PARAM, getContextResource());
        context.addEventListener(listener);

        context.setContextPath(getContextPath());
        ServletHolder holder = new ServletHolder(getServlet());
        context.addServlet(holder, getServletPath());

        server.setHandler(context);

        try
        {
            server.start();
        }
        catch (Exception e)
        {
            try
            {
                server.stop();
            }
            catch (Exception e1)
            {
                // Ignore exceptions during cleanup attempt
            }
            throw new AssertionError(e);
        }
    }

    private Connector createConnector (int port)
    {
        Connector connector = new SelectChannelConnector();
        connector.setPort(port);

        return connector;
    }

    private Connector createSecureConnector (int port)
    {
        SslSocketConnector connector = new SslSocketConnector();
        connector.setPort(port);
        if (!isNullOrEmpty(getKeyStoreFile()))
            connector.setKeystore(getKeyStoreFile());
        if (!isNullOrEmpty(getKeyStorePassword()))
            connector.setKeyPassword(getKeyStorePassword());
        if (!isNullOrEmpty(getTrustStoreFile()))
            connector.setTruststore(getTrustStoreFile());
        if (!isNullOrEmpty(getTrustStorePassword()))
            connector.setTrustPassword(getTrustStorePassword());
        connector.setNeedClientAuth(true);
        connector.setWantClientAuth(true);

        return connector;
    }

    private boolean isNullOrEmpty (String value)
    {
        return value == null || value.length() == 0;
    }

    @Override
    public void stop ()
    {
        if (server != null)
        {
            try
            {
                server.stop();
            }
            catch (Exception e)
            {
                throw new AssertionError(e);
            }
        }
    }
}