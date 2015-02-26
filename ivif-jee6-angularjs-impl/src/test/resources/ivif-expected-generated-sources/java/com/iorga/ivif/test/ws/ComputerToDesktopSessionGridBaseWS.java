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

@Path("/computerToDesktopSessionGrid")
@Generated
@Stateless
public class ComputerToDesktopSessionGridBaseWS {

    @Inject @Generated
    private ComputerBaseService computerBaseService;


    public static class ComputerToDesktopSessionGridFilterResult {
        public Integer id;
        public String name;
        public Integer user_id;
    }
    public static class ComputerToDesktopSessionGridSearchResult extends ComputerToDesktopSessionGridFilterResult {

        public ComputerToDesktopSessionGridSearchResult() {}
        public ComputerToDesktopSessionGridSearchResult(Integer id, String name, Integer user_id) {
            this.id = id;
            this.name = name;
            this.user_id = user_id;
        }
    }
    public static class ComputerToDesktopSessionGridSearchFilter extends ComputerToDesktopSessionGridFilterResult {
    }
    public static class ComputerToDesktopSessionGridSearchParam extends GridSearchParam<ComputerToDesktopSessionGridSearchFilter> {}
    @POST
    @Path("/search")
    @Consumes("application/json")
    @Produces("application/json")
    public SearchResults<ComputerToDesktopSessionGridSearchResult> search(ComputerToDesktopSessionGridSearchParam searchParam) {
        return computerBaseService.search(searchParam);
    }
}
