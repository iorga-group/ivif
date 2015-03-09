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
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Path("/editableUserGrid")
@Generated
@Stateless
@RolesAllowed({"admin", "manager"})
public class EditableUserGridBaseWS {

    @Inject @Generated
    private UserBaseService userBaseService;


    public static class EditableUserGridEditableFilterResult {
        public String name;
        public UserStatusType status;
        public Boolean enabled;
        public String bigComment;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EditableUserGridSaveParam extends EditableUserGridEditableFilterResult {
        public String commentTemp;
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
            entityToSave.setCommentTemp(saveParam.commentTemp);
            entityToSave.setName(saveParam.name);
            entityToSave.setStatus(saveParam.status);
            entityToSave.setEnabled(saveParam.enabled);
            entityToSave.setBigComment(saveParam.bigComment);
            // Set version for optimistic lock
            userBaseService.detach(entityToSave);
            entityToSave.setVersion(saveParam.version);

            // Ask for save
            userBaseService.save(entityToSave);
        }
    }

    public static class EditableUserGridFilterResult extends EditableUserGridEditableFilterResult {
        public String firstName;
    }
    public static class EditableUserGridSearchResult extends EditableUserGridFilterResult {
        public String profile_description;
        public Integer profile_id;
        public String profile_name;
        public Integer id;
        public Long version;

        public EditableUserGridSearchResult() {}
        public EditableUserGridSearchResult(String firstName, String name, UserStatusType status, String profile_description, Boolean enabled, String bigComment, Integer profile_id, String profile_name, Integer id, Long version) {
            this.firstName = firstName;
            this.name = name;
            this.status = status;
            this.profile_description = profile_description;
            this.enabled = enabled;
            this.bigComment = bigComment;
            this.profile_id = profile_id;
            this.profile_name = profile_name;
            this.id = id;
            this.version = version;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EditableUserGridSearchFilter extends EditableUserGridFilterResult {
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
