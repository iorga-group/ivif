package com.iorga.ivif.test.ws;

import com.iorga.ivif.ja.Generated;
import com.iorga.ivif.ja.GridSearchParam;
import com.iorga.ivif.ja.RolesAllowed;
import com.iorga.ivif.test.entity.User;
import com.iorga.ivif.test.entity.select.UserStatusType;
import com.iorga.ivif.test.service.UserBaseService;
import com.mysema.query.SearchResults;
import java.lang.Boolean;
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
        public UserStatusType status;
        public String commentTemp;
        public Boolean enabled;
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
            entityToSave.setStatus(saveParam.status);
            entityToSave.setCommentTemp(saveParam.commentTemp);
            entityToSave.setEnabled(saveParam.enabled);
            // Set version for optimistic lock
            userBaseService.detach(entityToSave);
            entityToSave.setVersion(saveParam.version);

            // Ask for save
            userBaseService.save(entityToSave);
        }
    }

    public static class EditableUserGridSearchResult extends EditableUserGridSaveParam {
        public String firstName;
        public String profile_description;
        public Integer profile_id;
        public String profile_name;

        public EditableUserGridSearchResult() {}
        public EditableUserGridSearchResult(String firstName, String name, UserStatusType status, String profile_description, Boolean enabled, Integer profile_id, String profile_name, Integer id, Long version) {
            this.firstName = firstName;
            this.name = name;
            this.status = status;
            this.profile_description = profile_description;
            this.enabled = enabled;
            this.profile_id = profile_id;
            this.profile_name = profile_name;
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
