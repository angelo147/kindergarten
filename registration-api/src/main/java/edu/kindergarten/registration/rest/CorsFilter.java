package edu.kindergarten.registration.rest;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author sbatziopoulos
 */
@Provider
@Priority(1)
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext crc, ContainerResponseContext crc1) {
        crc1.getHeaders().add(
                "Access-Control-Allow-Origin", "*");
        crc1.getHeaders().add(
                "Access-Control-Allow-Credentials", "true");
        crc1.getHeaders().add("Access-Control-Expose-Headers", "Authorization");
        crc1.getHeaders().add(
                "Access-Control-Allow-Headers",
                "origin, content-type, accept, authorization");
        crc1.getHeaders().add(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        crc1.getHeaders().add("Access-Control-Max-Age", "1209600");
    }

}
