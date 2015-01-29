package com.iorga.ivif.test.ws;

import com.iorga.ivif.ja.Generated;
import com.iorga.ivif.ja.GridSearchParam;
import com.iorga.ivif.test.service.UserBaseService;
import com.iorga.ivif.test.ws.ToolbarUserGridBaseWS.ToolbarUserGridSearchFilter;
import com.iorga.ivif.test.ws.ToolbarUserGridBaseWS.ToolbarUserGridSearchParam;
import com.iorga.ivif.test.ws.ToolbarUserGridBaseWS.ToolbarUserGridSearchResult;
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
        public Integer profile_id;
        public Integer id;

        public ToolbarUserGridSearchResult() {}
        public ToolbarUserGridSearchResult(String name, Integer profile_id, Integer id) {
            this.name = name;
            this.profile_id = profile_id;
            this.id = id;
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
