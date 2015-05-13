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

@Path("/leftJoinComputerGrid")
@Generated
@Stateless
public class LeftJoinComputerGridBaseWS {

    @Inject @Generated
    private ComputerBaseService computerBaseService;


    public static class LeftJoinComputerGridFilterResult {
        public Integer id;
        public String name;
    }
    public static class LeftJoinComputerGridSearchResult extends LeftJoinComputerGridFilterResult {
        public String __defaultProfile_description;
        public String __user_name;

        public LeftJoinComputerGridSearchResult() {}
        public LeftJoinComputerGridSearchResult(Integer id, String name, String __defaultProfile_description, String __user_name) {
            this.id = id;
            this.name = name;
            this.__defaultProfile_description = __defaultProfile_description;
            this.__user_name = __user_name;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LeftJoinComputerGridSearchFilter extends LeftJoinComputerGridFilterResult {
    }
    public static class LeftJoinComputerGridSearchParam extends GridSearchParam<LeftJoinComputerGridSearchFilter> {}
    @POST
    @Path("/search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResults<LeftJoinComputerGridSearchResult> search(LeftJoinComputerGridSearchParam searchParam) {
        return computerBaseService.search(searchParam);
    }
}
