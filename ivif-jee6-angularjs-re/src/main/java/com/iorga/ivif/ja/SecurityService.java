package com.iorga.ivif.ja;

import org.jboss.resteasy.spi.UnauthorizedException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

@ApplicationScoped
public class SecurityService {

    @Inject
    @CurrentRoles
    private Collection<String> currentRoles;

    public void checkRolesAllowed(String... rolesAllowed) {
        checkRolesAllowed(Arrays.asList(rolesAllowed));
    }

    public void checkRolesAllowed(Collection<String> rolesAllowed) {
        // check if we have at least one common role
        final HashSet<String> rolesAllowedSet = new HashSet<>(rolesAllowed);
        rolesAllowedSet.retainAll(currentRoles);
        if (rolesAllowedSet.isEmpty()) {
            throw new UnauthorizedException("Current roles don't match required roles");
        }
    }
}
