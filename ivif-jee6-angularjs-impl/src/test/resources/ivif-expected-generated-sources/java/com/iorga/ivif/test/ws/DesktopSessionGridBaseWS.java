package com.iorga.ivif.test.ws;

import com.iorga.ivif.ja.Generated;
import com.iorga.ivif.ja.GridSearchParam;
import com.iorga.ivif.test.service.DesktopSessionBaseService;
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

@Path("/desktopSessionGrid")
@Generated
@Stateless
public class DesktopSessionGridBaseWS {

    @Inject @Generated
    private DesktopSessionBaseService desktopSessionBaseService;


    public static class DesktopSessionGridFilterResult {
        public Integer computerId;
        public String name;
    }
    public static class DesktopSessionGridSearchResult extends DesktopSessionGridFilterResult {

        public DesktopSessionGridSearchResult() {}
        public DesktopSessionGridSearchResult(Integer computerId, String name) {
            this.computerId = computerId;
            this.name = name;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OpenDesktopSessionGridFromComputer {
        public Integer userId;
        public Integer computerId;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OpenCurrentUserDesktopSessionGridFromComputer {
        public Integer computerId;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DesktopSessionGridSearchFilter extends DesktopSessionGridFilterResult {
        public OpenDesktopSessionGridFromComputer openDesktopSessionGridFromComputer;
        public OpenCurrentUserDesktopSessionGridFromComputer openCurrentUserDesktopSessionGridFromComputer;
    }
    public static class DesktopSessionGridSearchParam extends GridSearchParam<DesktopSessionGridSearchFilter> {}
    @POST
    @Path("/search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResults<DesktopSessionGridSearchResult> search(DesktopSessionGridSearchParam searchParam) {
        return desktopSessionBaseService.search(searchParam);
    }
}
