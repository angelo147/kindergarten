package edu.kindergarten.registration.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
@ApplicationPath("/")
public class RestApplication extends Application {
    /*public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<>();
        s.add(KinderGartenEndpoint.class);
        s.add(JwtAuthFilter.class);
        return s;
    }*/
}
