/*
 * Copyright (c) 2009-2010. EMC Corporation. All Rights Reserved.
 */
package com.poc.xua.test;

import com.poc.test.server.JettyTestContainer;
import com.wss4j.test.config.XuaConfig;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.sts.token.provider.CustomAttributeStatementProvider;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.cxf.ws.security.trust.STSClient;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openehealth.ipf.commons.ihe.xds.core.SampleData;
import org.openehealth.ipf.commons.ihe.xds.core.requests.RegisterDocumentSet;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.lcm.SubmitObjectsRequest;
import org.openehealth.ipf.commons.ihe.xds.iti42.Iti42PortType;
import org.openehealth.ipf.platform.camel.ihe.xds.core.converters.EbXML30Converters;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPFaultException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * XUA Test Class
 */
public class XuaTest extends JettyTestContainer {

    static private XuaConfig configuration;
    static private STSClient stsClient;
    static private final String SUCCESS = "success";
    static private final String FAILURE = "failure";
    static private final String FAILURE_MSG = "Error processing the request";
    static private SubmitObjectsRequest submitObjectsRequest;

    /**
     * Starts jetty server and exposes ihe iti42 and sts service endpoints.
     * @throws Exception on failure to start jetty server
     */
    @BeforeClass
    public static void setUpClass () throws Exception    {
        String certificateFile = XuaTest.class.getClassLoader().getResource("certificates/stsstore.jks").getFile();
        startSecureAndUnSecureServer(new CXFServlet(), "spring/xua-test-server-context.xml", certificateFile, "stskpass",
                certificateFile, "stsspass");
        configuration = (XuaConfig)getSpringContext().getBean("xuaConfiguration");
        RegisterDocumentSet request = SampleData.createRegisterDocumentSet();
        submitObjectsRequest = EbXML30Converters.convert(request);
    }

    /**
     * Tests XUA success scenario
     * iti42 endpoint has enabled with XUA using CXF Policy Feature
     * uses un-secure sts endpoints
     */
    @Test
    public void testXUAEnabledWithSecurityToken() {
        test(SUCCESS, null);
    }


    /**
     * Sends request without security token.
     * @param endpointName endpoint to be used
     */
    private void testWithoutSecurityToken(String endpointName) {
        try {
            ProducerTemplate template = new DefaultCamelContext().createProducerTemplate();
            template.requestBody(
                    "xds-iti42://localhost:" + getPort() + "/" + endpointName, new SubmitObjectsRequest());
            Assert.fail();
        } catch(Exception ex) {
            Assert.assertTrue(
                    ex.getCause().getMessage().contains("These policy alternatives can not be satisfied"));
        }
    }

    /**
     * changes configurations to test failure scenarios.
     * @param method setter method of the configuration
     * @throws Exception on un expected exception scenarios.
     */
    private void test(Method method) throws Exception {
        Set<String> set= (Set<String>)method.invoke(configuration);
        Set<String> temp = new HashSet<String>();
        temp.addAll(set);
        set.clear();
        set.add("test");
        try {
            test(FAILURE, FAILURE_MSG);
        } finally {
            set.clear();
            set.addAll(temp);
        }
    }

    /**
     * tests against xua enabled endpoint.
     * @param status result status
     * @param msg message to be matched against return value.
     */
    private void test(String status, String msg) {
        test(status, msg, "http://localhost:" + getPort() + "/xds-iti42-xuaEnabled");
    }

    /**
     * sends iti42 request to iti42 endpoint and asserts return value
     * @param status result status
     * @param msg message to be matched against return value
     * @param serviceURL url of iti42 service endpoint
     */
    private void test(String status, String msg, String serviceURL) {
        SpringBusFactory bf = new SpringBusFactory();
	    Bus bus = bf.createBus("spring/xua-test-client-context.xml");
	    SpringBusFactory.setDefaultBus(bus);
	    SpringBusFactory.setThreadDefaultBus(bus);

        Iti42PortType client =
                getClient("wsdl/iti42-with-policy.wsdl", serviceURL);

        STSClient stsClient = (STSClient)((BindingProvider)client).getRequestContext().get("ws-security.sts.client");
        stsClient.setWsdlLocation("http://localhost:" + getPort() + "/ut?wsdl");


        try {
            client.documentRegistryRegisterDocumentSetB(submitObjectsRequest);
            if (FAILURE.equals(status)) {
                Assert.fail();
            }
        } catch(SOAPFaultException ex) {
            if (SUCCESS.equals(status)) {
                Assert.fail();
            } else {
                Assert.assertTrue(
                    ex.getCause().getMessage().contains(msg));
            }
        } finally {
            SpringBusFactory.setThreadDefaultBus(null);
            SpringBusFactory.setDefaultBus(null);
        }
    }

    /**
     * creates iti42 client.
     * @param wsdlLocation wsdl location
     * @param serviceURL service location
     * @return Iti42PortType
     */
    private static Iti42PortType getClient(String wsdlLocation, String serviceURL) {
        URL wsdlURL = XuaTest.class.getClassLoader().getResource(wsdlLocation);
        Service service = Service.create(wsdlURL, new QName("urn:ihe:iti:xds-b:2007", "DocumentRegistry_Service", "ihe"));
        Iti42PortType client = service.getPort(Iti42PortType.class);

        BindingProvider bindingProvider = (BindingProvider) client;
        Map<String, Object> reqContext = bindingProvider.getRequestContext();
        reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceURL);
        return client;
    }

}
