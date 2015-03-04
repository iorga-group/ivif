package com.iorga.ivif.test.ws;

import com.iorga.ivif.ja.Generated;
import com.iorga.ivif.ja.GridSearchParam;
import com.iorga.ivif.ja.RolesAllowed;
import com.iorga.ivif.test.service.ProfileBaseService;
import com.mysema.query.SearchResults;
import java.lang.Integer;
import java.lang.String;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Path("/profileGrid")
@Generated
@Stateless
@RolesAllowed("manager")
public class ProfileGridBaseWS {

    @Inject @Generated
    private ProfileBaseService profileBaseService;


    public static class ProfileGridFilterResult {
        public String name;
    }
    public static class ProfileGridSearchResult extends ProfileGridFilterResult {

        public ProfileGridSearchResult() {}
        public ProfileGridSearchResult(String name) {
            this.name = name;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OpenProfileGridFromUser {
        public Integer profileId;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProfileGridSearchFilter extends ProfileGridFilterResult {
        public OpenProfileGridFromUser openProfileGridFromUser;
    }
    public static class ProfileGridSearchParam extends GridSearchParam<ProfileGridSearchFilter> {}
    @POST
    @Path("/search")
    @Consumes("application/json")
    @Produces("application/json")
    public SearchResults<ProfileGridSearchResult> search(ProfileGridSearchParam searchParam) {
        return profileBaseService.search(searchParam);
    }
}
