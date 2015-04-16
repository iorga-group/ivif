package com.iorga.ivif.test.ws;

import com.iorga.ivif.ja.Generated;
import com.iorga.ivif.ja.GridSearchParam;
import com.iorga.ivif.test.service.ComputerBaseService;
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

@Path("/computerGrid")
@Generated
@Stateless
public class ComputerGridBaseWS {

    @Inject @Generated
    private ComputerBaseService computerBaseService;


    public static class ComputerGridFilterResult {
        public String name;
        public String user_name;
    }
    public static class ComputerGridSearchResult extends ComputerGridFilterResult {
        public Integer user_id;

        public ComputerGridSearchResult() {}
        public ComputerGridSearchResult(String name, String user_name, Integer user_id) {
            this.name = name;
            this.user_name = user_name;
            this.user_id = user_id;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OpenComputerGridFromUser {
        public Integer userid;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ComputerGridSearchFilter extends ComputerGridFilterResult {
        public OpenComputerGridFromUser openComputerGridFromUser;
    }
    public static class ComputerGridSearchParam extends GridSearchParam<ComputerGridSearchFilter> {}
    @POST
    @Path("/search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResults<ComputerGridSearchResult> search(ComputerGridSearchParam searchParam) {
        return computerBaseService.search(searchParam);
    }
}
