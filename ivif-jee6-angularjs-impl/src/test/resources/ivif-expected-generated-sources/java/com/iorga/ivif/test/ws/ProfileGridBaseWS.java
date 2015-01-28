package com.iorga.ivif.test.ws;

import com.iorga.ivif.ja.Generated;
import com.iorga.ivif.ja.GridSearchParam;
import com.iorga.ivif.test.service.ProfileBaseService;
import com.iorga.ivif.test.ws.ProfileGridBaseWS.OpenProfileGridFromUser;
import com.iorga.ivif.test.ws.ProfileGridBaseWS.ProfileGridSearchFilter;
import com.iorga.ivif.test.ws.ProfileGridBaseWS.ProfileGridSearchParam;
import com.iorga.ivif.test.ws.ProfileGridBaseWS.ProfileGridSearchResult;
import com.mysema.query.SearchResults;
import java.lang.Integer;
import java.lang.String;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/profileGrid")
@Generated
@Stateless
public class ProfileGridBaseWS {
    @Inject @Generated
    private ProfileBaseService profileBaseService;


    public static class ProfileGridSearchResult {
        public String name;

        public ProfileGridSearchResult() {}
        public ProfileGridSearchResult(String name) {
            this.name = name;
        }
    }
    public static class OpenProfileGridFromUser {
        public Integer profileId;
    }
    public static class ProfileGridSearchFilter extends ProfileGridSearchResult {
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

