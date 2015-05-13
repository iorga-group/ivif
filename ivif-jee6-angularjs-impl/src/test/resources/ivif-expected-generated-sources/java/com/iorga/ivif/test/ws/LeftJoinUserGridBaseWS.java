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

@Path("/leftJoinUserGrid")
@Generated
@Stateless
public class LeftJoinUserGridBaseWS {

    @Inject @Generated
    private UserBaseService userBaseService;


    public static class LeftJoinUserGridFilterResult {
        public String name;
        public String __profile_name;
    }
    public static class LeftJoinUserGridSearchResult extends LeftJoinUserGridFilterResult {
        public String __profile_description;
        public Integer __profile_id;

        public LeftJoinUserGridSearchResult() {}
        public LeftJoinUserGridSearchResult(String name, String __profile_name, String __profile_description, Integer __profile_id) {
            this.name = name;
            this.__profile_name = __profile_name;
            this.__profile_description = __profile_description;
            this.__profile_id = __profile_id;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LeftJoinUserGridSearchFilter extends LeftJoinUserGridFilterResult {
    }
    public static class LeftJoinUserGridSearchParam extends GridSearchParam<LeftJoinUserGridSearchFilter> {}
    @POST
    @Path("/search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResults<LeftJoinUserGridSearchResult> search(LeftJoinUserGridSearchParam searchParam) {
        return userBaseService.search(searchParam);
    }
}
