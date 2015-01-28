package com.iorga.ivif.test.ws;

import com.iorga.ivif.ja.Generated;
import com.iorga.ivif.ja.GridSearchParam;
import com.iorga.ivif.test.service.DesktopSessionBaseService;
import com.iorga.ivif.test.ws.DesktopSessionGridBaseWS.DesktopSessionGridSearchFilter;
import com.iorga.ivif.test.ws.DesktopSessionGridBaseWS.DesktopSessionGridSearchParam;
import com.iorga.ivif.test.ws.DesktopSessionGridBaseWS.DesktopSessionGridSearchResult;
import com.iorga.ivif.test.ws.DesktopSessionGridBaseWS.OpenCurrentUserDesktopSessionGridFromComputer;
import com.iorga.ivif.test.ws.DesktopSessionGridBaseWS.OpenDesktopSessionGridFromComputer;
import com.mysema.query.SearchResults;
import java.lang.Integer;
import java.lang.String;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/desktopSessionGrid")
@Generated
@Stateless
public class DesktopSessionGridBaseWS {
    @Inject @Generated
    private DesktopSessionBaseService desktopSessionBaseService;


    public static class DesktopSessionGridSearchResult {
        public Integer computerId;
        public String name;

        public DesktopSessionGridSearchResult() {}
        public DesktopSessionGridSearchResult(Integer computerId, String name) {
            this.computerId = computerId;
            this.name = name;
        }
    }
    public static class OpenDesktopSessionGridFromComputer {
        public Integer userId;
        public Integer computerId;
    }
    public static class OpenCurrentUserDesktopSessionGridFromComputer {
        public Integer computerId;
    }
    public static class DesktopSessionGridSearchFilter extends DesktopSessionGridSearchResult {
        public OpenDesktopSessionGridFromComputer openDesktopSessionGridFromComputer;
        public OpenCurrentUserDesktopSessionGridFromComputer openCurrentUserDesktopSessionGridFromComputer;
    }
    public static class DesktopSessionGridSearchParam extends GridSearchParam<DesktopSessionGridSearchFilter> {}
    @POST
    @Path("/search")
    @Consumes("application/json")
    @Produces("application/json")
    public SearchResults<DesktopSessionGridSearchResult> search(DesktopSessionGridSearchParam searchParam) {
        return desktopSessionBaseService.search(searchParam);
    }
}

