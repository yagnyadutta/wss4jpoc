/*
 * Copyright (c) 2009-2010. EMC Corporation. All Rights Reserved.
 */
package com.wss4j.test.hanlder;

import com.wss4j.test.LogUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.Map;

/**
 * Security Callback Handler class used by Security Service to set private key password for a server's public key.
 */
public class XuaCallbackHandler implements CallbackHandler
{
    public void setCredentials(Map<String, String> credentials)
    {
        this.credentials = credentials;
    }

    /**
     * sets private key password for the server public key
     *
     * @param callbacks an array of Callback objects provided
     *                  by an underlying security service which contains
     *                  the information requested to be retrieved or displayed.
     * @throws IOException          if an input or output error occurs.
     * @throws UnsupportedCallbackException if the implementation of this
     *                                      method does not support one or more of the Callbacks
     *                                      specified in the callbacks parameter.
     */
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException
    {

        for (Callback callback : callbacks)
        {
            if (callback instanceof WSPasswordCallback)
            {
                WSPasswordCallback pc = (WSPasswordCallback) callback;
                String password = credentials.get(pc.getIdentifier());
                if (password != null)
                {
                    LogUtil.debug(log, "setting password for the key - {0}" + pc.getIdentifier());
                    pc.setPassword(password.isEmpty() ? null : password);
                }
                else
                {
                    LogUtil.error(log, "failed to set password, unknown server public key {0}", pc.getIdentifier());
                }
            }
        }

    }

    private Map<String, String> credentials;
    private final Log log = LogFactory.getLog(XuaCallbackHandler.class);
}
