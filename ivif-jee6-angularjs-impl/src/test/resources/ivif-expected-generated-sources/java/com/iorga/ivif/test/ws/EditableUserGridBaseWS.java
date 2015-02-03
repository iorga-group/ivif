package com.iorga.ivif.test.ws;

import com.iorga.ivif.ja.Generated;
import com.iorga.ivif.ja.GridSearchParam;
import com.iorga.ivif.ja.RolesAllowed;
import com.iorga.ivif.test.entity.User;
import com.iorga.ivif.test.service.UserBaseService;
import com.iorga.ivif.test.ws.EditableUserGridBaseWS.EditableUserGridSearchFilter;
import com.iorga.ivif.test.ws.EditableUserGridBaseWS.EditableUserGridSearchParam;
import com.iorga.ivif.test.ws.EditableUserGridBaseWS.EditableUserGridSearchResult;
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

@Path("/editableUserGrid")
@Generated
@Stateless
@RolesAllowed({"admin", "manager"})
public class EditableUserGridBaseWS {
    @Inject @Generated
    private UserBaseService userBaseService;

    public static class EditableUserGridSaveParam {
        public String name;
        public Integer id;
        public Long version;
    }
    @POST
    @Path("/save")
    @Consumes("application/json")
    @TransactionAttribute
    public void save(List<EditableUserGridSaveParam> saveParams) {
        for (EditableUserGridSaveParam saveParam : saveParams) {
            // Search for this entityToSave
            User entityToSave = userBaseService.find(saveParam.id);
            if (entityToSave == null) {
                // Must create a new entityToSave
                entityToSave = new User();
            }
            // Apply modifications
            entityToSave.setName(saveParam.name);
            // Set version for optimistic lock
            userBaseService.detach(entityToSave);
            entityToSave.setVersion(saveParam.version);

            // Ask for save
            userBaseService.save(entityToSave);
        }
    }

    public static class EditableUserGridSearchResult extends EditableUserGridSaveParam {
        public String firstName;
        public Integer profile_id;

        public EditableUserGridSearchResult() {}
        public EditableUserGridSearchResult(String firstName, String name, Integer profile_id, Integer id, Long version) {
            this.firstName = firstName;
            this.name = name;
            this.profile_id = profile_id;
            this.id = id;
            this.version = version;
        }
    }
    public static class EditableUserGridSearchFilter extends EditableUserGridSearchResult {
    }
    public static class EditableUserGridSearchParam extends GridSearchParam<EditableUserGridSearchFilter> {}
    @POST
    @Path("/search")
    @Consumes("application/json")
    @Produces("application/json")
    public SearchResults<EditableUserGridSearchResult> search(EditableUserGridSearchParam searchParam) {
        return userBaseService.search(searchParam);
    }
}

