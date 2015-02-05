package com.iorga.ivif.test.ws;

import com.iorga.ivif.ja.Generated;
import com.iorga.ivif.ja.GridSearchParam;
import com.iorga.ivif.test.service.ComputerBaseService;
import com.mysema.query.SearchResults;
import java.lang.String;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/computerForConnectedUserGrid")
@Generated
@Stateless
public class ComputerForConnectedUserGridBaseWS {

    @Inject @Generated
    private ComputerBaseService computerBaseService;


    public static class ComputerForConnectedUserGridSearchResult {
        public String name;

        public ComputerForConnectedUserGridSearchResult() {}
        public ComputerForConnectedUserGridSearchResult(String name) {
            this.name = name;
        }
    }
    public static class ComputerForConnectedUserGridSearchFilter extends ComputerForConnectedUserGridSearchResult {
    }
    public static class ComputerForConnectedUserGridSearchParam extends GridSearchParam<ComputerForConnectedUserGridSearchFilter> {}
    @POST
    @Path("/search")
    @Consumes("application/json")
    @Produces("application/json")
    public SearchResults<ComputerForConnectedUserGridSearchResult> search(ComputerForConnectedUserGridSearchParam searchParam) {
        return computerBaseService.search(searchParam);
    }
}
