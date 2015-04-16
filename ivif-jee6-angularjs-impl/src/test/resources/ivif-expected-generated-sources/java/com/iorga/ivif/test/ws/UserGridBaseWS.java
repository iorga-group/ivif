package com.iorga.ivif.test.ws;

import com.iorga.ivif.ja.Generated;
import com.iorga.ivif.ja.GridSearchParam;
import com.iorga.ivif.test.service.UserBaseService;
import com.mysema.query.SearchResults;
import java.lang.Integer;
import java.lang.String;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Path("/userGrid")
@Generated
@Stateless
public class UserGridBaseWS {

    @Inject @Generated
    private UserBaseService userBaseService;


    public static class UserGridFilterResult {
        public String name;
        public Integer profile_id;
    }
    public static class UserGridSearchResult extends UserGridFilterResult {

        public UserGridSearchResult() {}
        public UserGridSearchResult(String name, Integer profile_id) {
            this.name = name;
            this.profile_id = profile_id;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OpenUserGridFromComputer {
        public Integer userId;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserGridSearchFilter extends UserGridFilterResult {
        public OpenUserGridFromComputer openUserGridFromComputer;
    }
    public static class UserGridSearchParam extends GridSearchParam<UserGridSearchFilter> {}
    @POST
    @Path("/search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResults<UserGridSearchResult> search(UserGridSearchParam searchParam) {
        return userBaseService.search(searchParam);
    }
}
