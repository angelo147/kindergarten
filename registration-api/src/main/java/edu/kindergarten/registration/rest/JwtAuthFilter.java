package edu.kindergarten.registration.rest;

import edu.kindergarten.registration.security.JWTUtil;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Provider
@ValidateUser
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthFilter implements ContainerRequestFilter {
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Bearer";
    private static final ServerResponse ACCESS_DENIED = new ServerResponse("Access denied for this resource", 401, new Headers<>());
    private static final ServerResponse ACCESS_FORBIDDEN = new ServerResponse("Nobody can access this resource", 403, new Headers<>());
    private static final ServerResponse SERVER_ERROR = new ServerResponse("INTERNAL SERVER ERROR", 500, new Headers<>());
    @Inject
    private JWTUtil jwtUtil;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) requestContext.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
        Method method = methodInvoker.getMethod();
        //Access allowed for all
        if (!method.isAnnotationPresent(PermitAll.class)) {
            //Access denied for all
            if (method.isAnnotationPresent(DenyAll.class)) {
                requestContext.abortWith(ACCESS_FORBIDDEN);
                return;
            }
            //Get userId from pathParam
            final MultivaluedMap< String, String > pathParametersMultivaluedMap = requestContext.getUriInfo().getPathParameters();
            final List<String> userIdHeader = pathParametersMultivaluedMap.get("userId");
            if (userIdHeader == null || userIdHeader.isEmpty()) {
                requestContext.abortWith(ACCESS_DENIED);
                return;
            }
            final Long userId = Long.valueOf(userIdHeader.get(0));
            //Get request headers
            final MultivaluedMap<String, String> headers = requestContext.getHeaders();
            //Fetch authorization header
            final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);
            //If no authorization information present; block access
            if (authorization == null || authorization.isEmpty()) {
                requestContext.abortWith(ACCESS_DENIED);
                return;
            }
            //Get encoded username and password
            final String jwt = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");
            if(!jwtUtil.verifyJWT(jwt, userId)) {
                requestContext.abortWith(ACCESS_DENIED);
                return;
            }
            //Verify user access
            if (method.isAnnotationPresent(RolesAllowed.class)) {
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                Set<String> rolesSet = new HashSet<>(Arrays.asList(rolesAnnotation.value()));
                //Is user valid?
                if (!isUserAllowed(jwtUtil.getRoles(jwt), rolesSet)) {
                    requestContext.abortWith(ACCESS_DENIED);
                }
            }
        }
    }

    private boolean isUserAllowed(final List<Role> role, final Set<String> rolesSet) {
        /*boolean isAllowed = false;
        //Step 2. Verify user role
        if (rolesSet.containsAll(role.stream().map(Role::toString).collect(Collectors.toList()))) {
            isAllowed = true;
        }
        return isAllowed*/;
        return rolesSet.stream().anyMatch(role.stream().map(Role::toString).collect(Collectors.toList())::contains);
    }
}
