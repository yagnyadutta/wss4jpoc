/*
 * Copyright (c) 2009-2010. EMC Corporation. All Rights Reserved.
 */
package com.wss4j.test.interceptor;

import com.wss4j.test.LogUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
//import org.apache.cxf.ws.policy.PolicyException;
import org.apache.cxf.ws.policy.PolicyException;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.openhealthtools.ihe.atna.auditor.codes.rfc3881.RFC3881EventCodes;
import org.openhealthtools.ihe.atna.auditor.events.dicom.UserAuthenticationEvent;
import org.openhealthtools.ihe.atna.auditor.codes.dicom.DICOMEventTypeCodes;
import org.openehealth.ipf.commons.ihe.core.atna.AuditorManager;
/*import org.openehealth.ipf.commons.ihe.core.atna.AuditorManager;
import org.openhealthtools.ihe.atna.auditor.codes.dicom.DICOMEventTypeCodes;
import org.openhealthtools.ihe.atna.auditor.codes.rfc3881.RFC3881EventCodes;
import org.openhealthtools.ihe.atna.auditor.events.dicom.UserAuthenticationEvent;*/

/*
 * XUA Out Fault Interceptor class.
 */
public class XuaOutFaultInterceptor extends AbstractPhaseInterceptor<Message>
{

    /**
     * Default Public Constructor.
     */
    public XuaOutFaultInterceptor()
    {
        super(Phase.SETUP);
    }

    /**
     * Fires authentication failure audit message if there is any policy or security exception
     * @param message message
     * @throws Fault throws fault if it fails to handle the message
     */
    @Override
    public void handleMessage(Message message) throws Fault
    {
        Throwable exception = message.getContent(Exception.class);
        if (exception == null)
            return;
        exception = ObjectUtils.defaultIfNull(exception.getCause(), exception);
        LogUtil.debug(log, "Exception in the message - {0}", exception.getMessage());
        if(exception instanceof WSSecurityException || exception instanceof PolicyException)
        {
            UserAuthenticationEvent event = new UserAuthenticationEvent(
                    RFC3881EventCodes.RFC3881EventOutcomeCodes.MINOR_FAILURE, new DICOMEventTypeCodes.Login());
            AuditorManager.getRegistryAuditor().audit(event);
            LogUtil.debug(log, "Authentication failure audit message fired");
        }
    }

    private final Log log = LogFactory.getLog(XuaOutFaultInterceptor.class);
}
