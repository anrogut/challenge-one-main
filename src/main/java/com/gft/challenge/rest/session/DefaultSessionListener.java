package com.gft.challenge.rest.session;

import com.gft.challenge.service.EndpointProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Component
public class DefaultSessionListener implements HttpSessionListener {

    private final EndpointProviderService endpointProviderService;

    @Autowired
    DefaultSessionListener(EndpointProviderService endpointProviderService) {
        this.endpointProviderService = endpointProviderService;
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        endpointProviderService.addEndpoint(se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        endpointProviderService.removeEndpoint(se.getSession().getId());
    }
}
