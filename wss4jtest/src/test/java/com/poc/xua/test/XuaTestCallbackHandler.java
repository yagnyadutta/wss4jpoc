/*
 * Copyright (c) 2009-2010. EMC Corporation. All Rights Reserved.
 */
package com.poc.xua.test;

import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 * Test Security Callback Handler class used by test Services.
 */
public class XuaTestCallbackHandler implements CallbackHandler {

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
    public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof WSPasswordCallback) { // CXF
                WSPasswordCallback pc = (WSPasswordCallback) callback;
                if ("myclientkey".equals(pc.getIdentifier())) {
                    pc.setPassword("ckpass");
                    break;
                } else if ("myservicekey".equals(pc.getIdentifier())) {
                    pc.setPassword("skpass");
                    break;
                } else if ("alice".equals(pc.getIdentifier())) {
                    pc.setPassword("clarinet");
                    break;
                } else if ("bob".equals(pc.getIdentifier())) {
                    pc.setPassword("trombone");
                    break;
                } else if ("eve".equals(pc.getIdentifier())) {
                    pc.setPassword("evekpass");
                    break;
                } else if ("mystskey".equals(pc.getIdentifier())) {
                    pc.setPassword("stskpass");
                    break;
                }
            }
        }
    }
}
