/*
 * Copyright (c) 2009-2011. EMC Corporation. All Rights Reserved.
 */
package com.emc.healthcare.xds.registry.commons

import org.apache.camel.spring.SpringRouteBuilder

public class SoapRouteBuilder extends SpringRouteBuilder
{
    def xuaEnabled;
    def endpointName;

    public void setXuaEnabled(def input) {
        xuaEnabled = input;
    }

    public void setEndpointName(def input) {
        endpointName = input;
    }
    @Override
    public void configure () throws Exception
    {
        def options = ""
        if(xuaEnabled == "true") {
            options = "?features=#policyFeature"
        }

        // Register Document Set (ITI-42)
        from("xds-iti42:" + endpointName + options)
            .to('log:worked')

    }
}
