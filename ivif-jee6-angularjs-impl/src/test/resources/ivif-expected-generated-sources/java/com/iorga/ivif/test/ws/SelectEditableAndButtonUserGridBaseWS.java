package com.iorga.ivif.test.ws;

import com.iorga.ivif.ja.Generated;
import com.iorga.ivif.ja.GridSearchParam;
import com.iorga.ivif.test.entity.User;
import com.iorga.ivif.test.service.UserBaseService;
import com.mysema.query.SearchResults;
import java.lang.Integer;
import java.lang.Long;
import java.lang.String;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Path("/selectEditableAndButtonUserGrid")
@Generated
@Stateless
public class SelectEditableAndButtonUserGridBaseWS {

    @Inject @Generated
    private UserBaseService userBaseService;


    public static class SelectEditableAndButtonUserGridEditableFilterResult {
        public Integer id;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SelectEditableAndButtonUserGridSaveParam extends SelectEditableAndButtonUserGridEditableFilterResult {
        public Long version;
    }
    @POST
    @Path("/save")
    @Consumes("application/json")
    @TransactionAttribute
    public void save(List<SelectEditableAndButtonUserGridSaveParam> saveParams) {
        for (SelectEditableAndButtonUserGridSaveParam saveParam : saveParams) {
            // Search for this entityToSave
            User entityToSave = userBaseService.find(saveParam.id);
            if (entityToSave == null) {
                // Must create a new entityToSave
                entityToSave = new User();
            }
            // Apply modifications
            entityToSave.setId(saveParam.id);
            // Set version for optimistic lock
            userBaseService.detach(entityToSave);
            entityToSave.setVersion(saveParam.version);

            // Ask for save
            userBaseService.save(entityToSave);
        }
    }

    public static class SelectEditableAndButtonUserGridFilterResult extends SelectEditableAndButtonUserGridEditableFilterResult {
        public String name;
    }
    public static class SelectEditableAndButtonUserGridSearchResult extends SelectEditableAndButtonUserGridFilterResult {
        public String profile_name;
        public Long version;

        public SelectEditableAndButtonUserGridSearchResult() {}
        public SelectEditableAndButtonUserGridSearchResult(String name, Integer id, String profile_name, Long version) {
            this.name = name;
            this.id = id;
            this.profile_name = profile_name;
            this.version = version;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SelectEditableAndButtonUserGridSearchFilter extends SelectEditableAndButtonUserGridFilterResult {
    }
    public static class SelectEditableAndButtonUserGridSearchParam extends GridSearchParam<SelectEditableAndButtonUserGridSearchFilter> {}
    @POST
    @Path("/search")
    @Consumes("application/json")
    @Produces("application/json")
    public SearchResults<SelectEditableAndButtonUserGridSearchResult> search(SelectEditableAndButtonUserGridSearchParam searchParam) {
        return userBaseService.search(searchParam);
    }
}
