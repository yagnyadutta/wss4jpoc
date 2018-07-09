/*
 * Copyright (c) 2009-2010. EMC Corporation. All Rights Reserved.
 */
package org.apache.cxf.sts.token.provider;

import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.common.saml.bean.AuthenticationStatementBean;
import org.apache.wss4j.common.saml.bean.SubjectLocalityBean;
import org.apache.wss4j.common.saml.builder.SAML1Constants;
import org.apache.wss4j.common.saml.builder.SAML2Constants;

/**
 * A custom AuthenticationStatementProvider implementation for use in the tests.
 */
public class CustomAuthenticationProvider implements AuthenticationStatementProvider {

    /**
     * Get an AuthenticationStatementBean
     * @param providerParameters
     * @return AuthenticationStatementBean
     */
    public AuthenticationStatementBean getStatement(TokenProviderParameters providerParameters) {
        AuthenticationStatementBean authBean = new AuthenticationStatementBean();
        
        SubjectLocalityBean subjectLocality = new SubjectLocalityBean();
        subjectLocality.setIpAddress("127.0.0.1");
        authBean.setSubjectLocality(subjectLocality);
        if (WSConstants.WSS_SAML_TOKEN_TYPE.equals(
                providerParameters.getTokenRequirements().getTokenType())) {
            authBean.setAuthenticationMethod(SAML1Constants.AUTH_METHOD_X509);
        } else {
            authBean.setAuthenticationMethod(SAML2Constants.AUTH_CONTEXT_CLASS_REF_X509);
        }
        return authBean;
    }
    
}
