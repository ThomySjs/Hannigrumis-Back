package com.Hannigrumis.api.property;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class RouteService {
    @Autowired
    private ObjectFactory<HttpServletRequest> requestFactory;

    public String getAppUrl(){
        HttpServletRequest request = requestFactory.getObject();
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }
}
