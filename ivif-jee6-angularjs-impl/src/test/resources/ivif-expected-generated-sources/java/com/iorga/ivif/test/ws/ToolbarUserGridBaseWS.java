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

@Path("/toolbarUserGrid")
@Generated
@Stateless
public class ToolbarUserGridBaseWS {

    @Inject @Generated
    private UserBaseService userBaseService;


    public static class ToolbarUserGridFilterResult {
        public String name;
    }
    public static class ToolbarUserGridSearchResult extends ToolbarUserGridFilterResult {
        public String profile_name;
        public Integer profile_id;
        public Integer id;

        public ToolbarUserGridSearchResult() {}
        public ToolbarUserGridSearchResult(String name, String profile_name, Integer profile_id, Integer id) {
            this.name = name;
            this.profile_name = profile_name;
            this.profile_id = profile_id;
            this.id = id;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ToolbarUserGridSearchFilter extends ToolbarUserGridFilterResult {
        public String firstName;
    }
    public static class ToolbarUserGridSearchParam extends GridSearchParam<ToolbarUserGridSearchFilter> {}
    @POST
    @Path("/search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResults<ToolbarUserGridSearchResult> search(ToolbarUserGridSearchParam searchParam) {
        return userBaseService.search(searchParam);
    }
}
