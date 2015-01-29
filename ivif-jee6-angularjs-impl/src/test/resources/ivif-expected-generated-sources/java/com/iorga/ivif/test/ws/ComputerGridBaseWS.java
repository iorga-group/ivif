package com.iorga.ivif.test.ws;

import com.iorga.ivif.ja.Generated;
import com.iorga.ivif.ja.GridSearchParam;
import com.iorga.ivif.test.service.ComputerBaseService;
import com.iorga.ivif.test.ws.ComputerGridBaseWS.ComputerGridSearchFilter;
import com.iorga.ivif.test.ws.ComputerGridBaseWS.ComputerGridSearchParam;
import com.iorga.ivif.test.ws.ComputerGridBaseWS.ComputerGridSearchResult;
import com.iorga.ivif.test.ws.ComputerGridBaseWS.OpenComputerGridFromUser;
import com.mysema.query.SearchResults;
import java.lang.Integer;
import java.lang.String;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/computerGrid")
@Generated
@Stateless
public class ComputerGridBaseWS {
    @Inject @Generated
    private ComputerBaseService computerBaseService;


    public static class ComputerGridSearchResult {
        public String name;
        public String user_name;
        public Integer user_id;

        public ComputerGridSearchResult() {}
        public ComputerGridSearchResult(String name, String user_name, Integer user_id) {
            this.name = name;
            this.user_name = user_name;
            this.user_id = user_id;
        }
    }
    public static class OpenComputerGridFromUser {
        public Integer userid;
    }
    public static class ComputerGridSearchFilter extends ComputerGridSearchResult {
        public OpenComputerGridFromUser openComputerGridFromUser;
    }
    public static class ComputerGridSearchParam extends GridSearchParam<ComputerGridSearchFilter> {}
    @POST
    @Path("/search")
    @Consumes("application/json")
    @Produces("application/json")
    public SearchResults<ComputerGridSearchResult> search(ComputerGridSearchParam searchParam) {
        return computerBaseService.search(searchParam);
    }
}

