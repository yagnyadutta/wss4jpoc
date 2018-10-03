package com.wss4j.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.message.Message;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.dom.validate.Credential;
import org.apache.wss4j.dom.validate.Validator;
import org.opensaml.saml.saml2.core.Assertion;
import org.springframework.beans.factory.annotation.Autowired;

public class XuaValidator implements Validator{
    private final Log log = LogFactory.getLog(XuaValidator.class);

    public static final String XUA_USER_NAME = "xuaUserName";
    public static final String XUA_USER_ROLE = "xuaUserRole";

    @Autowired(required = true)
    private Validator samlAssertionValidator;

    @Override
    public Credential validate(Credential credential, RequestData requestData) throws WSSecurityException
    {
        //Assertion samlToken = credential.getAssertion().getSaml2();
        Assertion samlToken = credential.getSamlAssertion().getSaml2();
        try {
           /* ((Message)requestData.getMsgContext()).setContextualProperty(
                    XUA_USER_NAME, samlToken.getSubject().getNameID().getValue());*/
            ((Message)requestData.getMsgContext()).put(
                    XUA_USER_NAME, samlToken.getSubject().getNameID().getValue());
        } catch(Exception e) {
            LogUtil.warn(log, "failed to get the user name from the token - {0}", e.getLocalizedMessage());
        }

        try {

            LogUtil.debug(log, "Validating SAML Token issued by {0}", samlToken.getIssuer().getValue());
            if (!credential.getSamlAssertion().isSigned())
            {
                LogUtil.error(log, "Token Validation failed");
                throw new WSSecurityException(WSSecurityException.ErrorCode.INVALID_SECURITY_TOKEN);
            }
            Credential newCredential = samlAssertionValidator.validate(credential, requestData);
            LogUtil.debug(log, "Token Validation is successful");
            return newCredential;
        } catch(Exception e) {
            LogUtil.error(log, e.getLocalizedMessage());
            throw new WSSecurityException(WSSecurityException.ErrorCode.INVALID_SECURITY_TOKEN);
        }

    }
}
