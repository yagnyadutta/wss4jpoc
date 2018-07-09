/*
 * Copyright (c) 2009-2010. EMC Corporation. All Rights Reserved.
 */
package com.wss4j.test.config;

import com.wss4j.test.LogUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.util.Set;

/**
 * XUA Configuration Class
 */
@ManagedResource(
    description = "XUA Configuration")
public class XuaConfig implements InitializingBean
{

    /**
     * Invoked by a BeanFactory after it has set all bean properties supplied
     *
     * @throws Exception in the event of misconfiguration
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        if (roles.isEmpty() ? !isEmpty(roleCodeSystem) : isEmpty(roleCodeSystem))
        {
            LogUtil.error(log, "Either Role Code Values or role code system not configured");
            throw new Exception("Supported Roles Codes and Code System should be configured");
        }
        if (purposeOfUseCodes.isEmpty() ? !isEmpty(purposeOfUseCodeSystem) : isEmpty(purposeOfUseCodeSystem))
        {
            LogUtil.error(log, "Purpose of Use code values or code system not configured");
            throw new Exception("Supported Purpose of Use code values or code system should be configured");
        }
        LogUtil.info(log, "XUA Configuration - {0}", this);

    }

    @ManagedAttribute(description = "Supported Authentication Methods.")
    public Set<String> getAuthenticationMethods()
    {
        return authenticationMethods;
    }

    public void setAuthenticationMethods(Set<String> authenticationMethods)
    {
        this.authenticationMethods = authenticationMethods;
    }

    @ManagedAttribute(description = "Returns Authorization Consent profile option flag.")
    public boolean isAuthzConsentOption()
    {
        return authzConsentOption;
    }

    @ManagedAttribute(description = "Sets Authorization Consent profile option flag.")
    public void setAuthzConsentOption(boolean authzConsentOption)
    {
        this.authzConsentOption = authzConsentOption;
    }

    @ManagedAttribute(description = "Purpose Of Use Code System.")
    public String getPurposeOfUseCodeSystem()
    {
        return purposeOfUseCodeSystem;
    }

    public void setPurposeOfUseCodeSystem(String purposeOfUseCodeSystem)
    {
        this.purposeOfUseCodeSystem = purposeOfUseCodeSystem;
    }

    @ManagedAttribute(description = "Purpose Of Use Code Values.")
    public Set<String> getPurposeOfUseCodes()
    {
        return purposeOfUseCodes;
    }

    public void setPurposeOfUseCodes(Set<String> purposeOfUseCodes)
    {
        this.purposeOfUseCodes = purposeOfUseCodes;
    }

    @ManagedAttribute(description = "Supported role code system")
    public String getRoleCodeSystem()
    {
        return roleCodeSystem;
    }

    public void setRoleCodeSystem(String roleCodeSystem)
    {
        this.roleCodeSystem = roleCodeSystem;
    }

    @ManagedAttribute(description = "User Roles.")
    public Set<String> getRoles()
    {
        return roles;
    }

    public void setRoles(Set<String> roles)
    {
        this.roles = roles;
    }

    @ManagedAttribute(description = "Server's Service Endpoint regular expression.")
    public String getServiceEndpoint()
    {
        return serviceEndpoint;
    }

    public void setServiceEndpoint(String serviceEndpoint)
    {
        this.serviceEndpoint = serviceEndpoint;
    }

    private boolean isEmpty(String data)
    {
        return (data == null || data.isEmpty());
    }

    @Override
    public String toString()
    {
        return "XuaConfig{" +
            "authenticationMethods=" + authenticationMethods +
            ", authzConsentOption=" + authzConsentOption +
            ", purposeOfUseCodeSystem='" + purposeOfUseCodeSystem + '\'' +
            ", purposeOfUseCodes=" + purposeOfUseCodes +
            ", roleCodeSystem='" + roleCodeSystem + '\'' +
            ", roles=" + roles +
            ", serviceEndpoint='" + serviceEndpoint + '\'' +
            '}';
    }

    private Set<String> authenticationMethods;
    private boolean authzConsentOption;
    private String purposeOfUseCodeSystem;
    private Set<String> purposeOfUseCodes;
    private String roleCodeSystem;
    private Set<String> roles;
    private String serviceEndpoint;
    private final Log log = LogFactory.getLog(XuaConfig.class);
}
