/*
 * Copyright (c) 2009-2010. EMC Corporation. All Rights Reserved.
 */
package org.apache.cxf.sts.token.provider;


import com.wss4j.test.XuaConstants;
import org.apache.wss4j.common.saml.bean.AttributeBean;
import org.apache.wss4j.common.saml.bean.AttributeStatementBean;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.XSURI;
import org.opensaml.xml.schema.impl.XSAnyBuilder;
import org.opensaml.xml.schema.impl.XSURIBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class CustomAttributeStatementProvider implements AttributeStatementProvider {
    private final String purposeOfUseElement;
    private final String roleElement;
    private boolean authzConsent = true;

    /**
     * Constructs purpose of use and role elements.
     */
    public CustomAttributeStatementProvider() {
        purposeOfUseElement =
                getCodedElement(XuaConstants.PURPOSE_OF_USE_ELEMENT_NAME, "OPERATIONS", "2.16.840.1.113883.3.18.7.1");
        roleElement =
                getCodedElement(XuaConstants.ROLE_ELEMENT_NAME, "Social Worker", "2.16.840.1.113883.3.18.7.1");
    }

    public boolean isAuthzConsent()
    {
        return authzConsent;
    }

    public void setAuthzConsent(boolean authzConsent)
    {
        this.authzConsent = authzConsent;
    }

    /**
     * Get an AttributeStatementBean containing purpose of use, role and authz consent attributes.
     * @param tokenProviderParameters
     * @return AttributeStatementBean
     */
    @Override
    public AttributeStatementBean getStatement(TokenProviderParameters tokenProviderParameters) {
        AttributeStatementBean attrStmtBean = new AttributeStatementBean();
        List<AttributeBean> attributes = new ArrayList<AttributeBean>();
        attributes.add(getAttributeBean(XuaConstants.PURPOSE_OF_USE_ATTRIBUTE_NAME,
                purposeOfUseElement));
        attributes.add(getAttributeBean("urn:oasis:names:tc:xacml:2.0:subject:role",
                roleElement));

        /*XSURI anyUri = new XSURIBuilder().buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSURI.TYPE_NAME);
        anyUri.setValue("urn:oid:1.2.3.xxx");
        attributes.add(getAttributeBean(AUTHZ_CONSENT_DOCUMENT_ID_ATTRIBUTE_NAME,
                "name format", "Consent Document", anyUri));*/

        if (authzConsent)
        {
            attributes.add(getAttributeBean("urn:ihe:iti:bppc:2007", "InstanceAccessConsentPolicy", "urn:oid:1.2.3.xxx"));
        }

        attrStmtBean.setSamlAttributes(attributes);
        return attrStmtBean;
    }

    /**
     * constructs XSAny type of AttributeBean
     * @param name name of the attribute
     * @param format format of the attribute
     * @param value value of the attribute
     * @return AttributeBean
     */
    private AttributeBean getAttributeBean(String name, String format, String value) {
        XSAny any = new XSAnyBuilder().buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
        any.setTextContent(value);
        return getAttributeBean(name, format, name, any);
    }

    /**
     * constructs XSAny type of AttributeBean
     * @param name name of the attribute
     * @param value value of the attribute
     * @return AttributeBean
     */
    private AttributeBean getAttributeBean(String name, String value) {
        XSAny any = new XSAnyBuilder().buildObject(AttributeValue.DEFAULT_ELEMENT_NAME);
        any.setTextContent(value);
        return getAttributeBean(name, name, name, any);
    }



    /**
     * Constructs AttributeBean and populates data into the bean.
     * @param name name of the attribute
     * @param nameFormat format of the attribute
     * @param friendlyName simple name of the attribute
     * @param value value of the attribute
     * @return AttributeBean
     */
    private AttributeBean getAttributeBean(String name, String nameFormat, String friendlyName, XMLObject value) {
        AttributeBean attrBean = new AttributeBean();
        attrBean.setQualifiedName(name);
        attrBean.setNameFormat(nameFormat);
        attrBean.setSimpleName(friendlyName);
        List<XMLObject> xmlObjects = Collections.singletonList(value);
        List<Object> values=new ArrayList<Object>();
        for(XMLObject xmlObj:xmlObjects){
            values.add(xmlObj);
        }
        attrBean.setAttributeValues(values);
        return attrBean;
    }

    /**
     * template to construct code element
     * @param tagName code tag name
     * @param code code value
     * @param codeSystem code system value
     * @return String code tag string
     */
    private String getCodedElement(String tagName, String code, String codeSystem) {
        StringBuilder input = new StringBuilder();
        input.append("<").append(tagName).
                append(" xmlns=\"urn:hl7-org:v3\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"").
                append(" xsi:type=\"CE\"").
                append(" code=\"").append(code).append("\"").
                append(" codeSystem=\"").append(codeSystem).append("\" codeSystemName=\"nhin-purpose\"").
                append(" displayName=\"Healthcare Operations\"/>");
        return input.toString();
    }
}
