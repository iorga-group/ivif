package com.iorga.ivif.test.ws;

import com.iorga.ivif.ja.Generated;
import com.iorga.ivif.ja.GridSearchParam;
import com.iorga.ivif.test.UserService;
import com.iorga.ivif.test.entity.select.UserStatusType;
import com.iorga.ivif.test.service.UserBaseService;
import com.mysema.query.SearchResults;
import java.lang.Boolean;
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

@Path("/specificSearchUserGrid")
@Generated
@Stateless
public class SpecificSearchUserGridBaseWS {

    @Inject @Generated
    private UserBaseService userBaseService;

    @Inject
    private UserService searchService;


    public static class SpecificSearchUserGridFilterResult {
        public String name;
    }
    public static class SpecificSearchUserGridSearchResult extends SpecificSearchUserGridFilterResult {
        public String profile_name;
        public Integer id;

        public SpecificSearchUserGridSearchResult() {}
        public SpecificSearchUserGridSearchResult(String name, String profile_name, Integer id) {
            this.name = name;
            this.profile_name = profile_name;
            this.id = id;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SpecificSearchUserGridSearchFilter extends SpecificSearchUserGridFilterResult {
        public String firstName;
        public Boolean testFlag;
        public UserStatusType userType;
    }
    public static class SpecificSearchUserGridSearchParam extends GridSearchParam<SpecificSearchUserGridSearchFilter> {}
    @POST
    @Path("/search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResults<SpecificSearchUserGridSearchResult> search(SpecificSearchUserGridSearchParam searchParam) {
        return searchService.search(searchParam);
    }
}
