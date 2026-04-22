
package com.SmartCampus.config;



import com.SmartCampus.exception.GenericExceptionMapper;
import com.SmartCampus.exception.LinkedResourceNotFoundExceptionMapper;
import com.SmartCampus.exception.RoomNotEmptyExceptionMapper;
import com.SmartCampus.exception.SensorUnavailableExceptionMapper;
import com.SmartCampus.filter.LoggingFilter;
import com.SmartCampus.resource.DiscoveryResource;
import com.SmartCampus.resource.RoomResource;
import com.SmartCampus.resource.SensorResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api/v1")
public class RestConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(DiscoveryResource.class);
        classes.add(RoomResource.class);
        classes.add(SensorResource.class);
        classes.add(RoomNotEmptyExceptionMapper.class);
        classes.add(LinkedResourceNotFoundExceptionMapper.class);
        classes.add(SensorUnavailableExceptionMapper.class);
        classes.add(GenericExceptionMapper.class);
        classes.add(LoggingFilter.class);        
        return classes;
    }
}