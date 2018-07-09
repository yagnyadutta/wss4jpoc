package com.poc.test.server;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.AfterClass;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.util.Random;

public class JettyTestContainer
{
    public static void startServer (Servlet servlet, String contextName)
    {
        startServer(servlet, contextName, false);
    }

    public static void startServer (Servlet servlet, String contextName, boolean secure)
    {
        log.info(String.format("Publishing services on port: %s", getPort()));

        servletServer = new JettyServer();
        servletServer.setContextResource(getContextURI(contextName).toString());
        if (secure)
            servletServer.setSecurePort(getPort());
        else
            servletServer.setPort(getPort());
        servletServer.setContextPath("");
        servletServer.setServletPath("/*");
        servletServer.setServlet(servlet);
        servletServer.start();

        ServletContext servletContext = servlet.getServletConfig().getServletContext();

        springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        producerTemplate = (ProducerTemplate) springContext.getBean("template");
        camelContext = (CamelContext) springContext.getBean("camelContext");
    }

    public static void startSecureAndUnSecureServer(Servlet servlet, String contextName, String keystore,
                                                    String keystorePassword, String truststore, String truststorePassword)
    {
        log.info(String.format("Publishing services on port: %s", getPort()));

        servletServer = new JettyServer();
        servletServer.setContextResource(getContextURI(contextName).toString());
        servletServer.setSecurePort(getSecurePort());
        servletServer.setPort(getPort());
        servletServer.setContextPath("");
        servletServer.setServletPath("/*");
        servletServer.setServlet(servlet);
        servletServer.setKeyStoreFile(keystore);
        servletServer.setKeyStorePassword(keystorePassword);
        servletServer.setTrustStoreFile(truststore);
        servletServer.setTrustStorePassword(truststorePassword);
        servletServer.start();

        ServletContext servletContext = servlet.getServletConfig().getServletContext();

        springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        producerTemplate = (ProducerTemplate) springContext.getBean("template");
        camelContext = (CamelContext) springContext.getBean("camelContext");
    }

    @AfterClass
    public static void stopServer ()
    {
        if (servletServer != null)
        {
            servletServer.stop();
            servletServer = null;
        }
        // TODO Too much of this class is static, it should be refactored to allow for better reuse among the test cases.
        // Even though the port reports as closed, the second test case coming through and reusing this
        // statically allocated port will fail. We need a fresh port for each test case. This resets it
        // however given enough test cases, we could eventually have failures due to port reuse.
        // Reset the port for next iteration.
        port = 0;

    }

    protected final <T> T send (String endpoint, Object body, Class<T> outputClass) throws Exception
    {
        Exchange exchange = newExchange(body);
        Exchange result = send(endpoint, exchange);

        return result.getOut().getBody(outputClass);
    }

    protected final Exchange send (String endpoint, Exchange exchange) throws Exception
    {
        Exchange result = producerTemplate.send(endpoint, exchange);
        Exception exception = result.getException();
        if (exception != null)
            throw exception;

        return result;
    }

    protected final Exchange newExchange ()
    {
        return new DefaultExchange(camelContext);
    }

    protected final Exchange newExchange (Object body)
    {
        Exchange exchange = newExchange();
        exchange.getIn().setBody(body);
        return exchange;
    }

    public static int getPort ()
    {
        if (port == 0)
            port = findFreePort();
        return port;
    }


    public static int getSecurePort ()
    {
        if (securePort == 0)
            securePort = findFreePort();
        return securePort;
    }

    public static ProducerTemplate getProducerTemplate ()
    {
        return producerTemplate;
    }

    public static CamelContext getCamelContext ()
    {
        return camelContext;
    }

    public static WebApplicationContext getSpringContext ()
    {
        return springContext;
    }

    private static URI getContextURI (String contextName)
    {
        try
        {
            return new ClassPathResource(contextName).getURI();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Unable to get URI for Spring context configuration", e);
        }
    }

    /**
     * @return a free port for testing between 8000-9999.
     */
    private static int findFreePort ()
    {
        int port = 8000;
        boolean portFree = false;
        while (!portFree)
        {
            port = 8000 + random.nextInt(2000);
            portFree = isPortFree(port);
        }
        return port;
    }

    private static boolean isPortFree (int portNumber)
    {
        try
        {
            new ServerSocket(portNumber).close();
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    private static final Random random = new Random();
    private static int port;
    private static int securePort;
    private static ProducerTemplate producerTemplate;
    private static CamelContext camelContext;
    private static ServletServer servletServer;
    private static WebApplicationContext springContext;

    private final static Logger log = LoggerFactory.getLogger(JettyTestContainer.class);
}
