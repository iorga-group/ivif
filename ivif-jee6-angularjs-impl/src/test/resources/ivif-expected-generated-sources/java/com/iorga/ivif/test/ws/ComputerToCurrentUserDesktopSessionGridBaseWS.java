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

@Path("/computerToCurrentUserDesktopSessionGrid")
@Generated
@Stateless
public class ComputerToCurrentUserDesktopSessionGridBaseWS {

    @Inject @Generated
    private ComputerBaseService computerBaseService;


    public static class ComputerToCurrentUserDesktopSessionGridSearchResult {
        public String name;
        public Integer id;

        public ComputerToCurrentUserDesktopSessionGridSearchResult() {}
        public ComputerToCurrentUserDesktopSessionGridSearchResult(String name, Integer id) {
            this.name = name;
            this.id = id;
        }
    }
    public static class ComputerToCurrentUserDesktopSessionGridSearchFilter extends ComputerToCurrentUserDesktopSessionGridSearchResult {
    }
    public static class ComputerToCurrentUserDesktopSessionGridSearchParam extends GridSearchParam<ComputerToCurrentUserDesktopSessionGridSearchFilter> {}
    @POST
    @Path("/search")
    @Consumes("application/json")
    @Produces("application/json")
    public SearchResults<ComputerToCurrentUserDesktopSessionGridSearchResult> search(ComputerToCurrentUserDesktopSessionGridSearchParam searchParam) {
        return computerBaseService.search(searchParam);
    }
}
