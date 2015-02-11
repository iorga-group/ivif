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

@Path("/toolbarUserGrid")
@Generated
@Stateless
public class ToolbarUserGridBaseWS {

    @Inject @Generated
    private UserBaseService userBaseService;


    public static class ToolbarUserGridSearchResult {
        public String name;
        public Integer id;
        public String profile_name;
        public Integer profile_id;

        public ToolbarUserGridSearchResult() {}
        public ToolbarUserGridSearchResult(String name, Integer id, String profile_name, Integer profile_id) {
            this.name = name;
            this.id = id;
            this.profile_name = profile_name;
            this.profile_id = profile_id;
        }
    }
    public static class ToolbarUserGridSearchFilter extends ToolbarUserGridSearchResult {
    }
    public static class ToolbarUserGridSearchParam extends GridSearchParam<ToolbarUserGridSearchFilter> {}
    @POST
    @Path("/search")
    @Consumes("application/json")
    @Produces("application/json")
    public SearchResults<ToolbarUserGridSearchResult> search(ToolbarUserGridSearchParam searchParam) {
        return userBaseService.search(searchParam);
    }
}
