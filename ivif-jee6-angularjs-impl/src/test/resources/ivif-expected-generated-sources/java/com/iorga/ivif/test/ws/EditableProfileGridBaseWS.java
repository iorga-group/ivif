package com.iorga.ivif.test.ws;

import com.iorga.ivif.ja.Generated;
import com.iorga.ivif.ja.GridSearchParam;
import com.iorga.ivif.ja.test.ProfileService;
import com.iorga.ivif.test.entity.Profile;
import com.iorga.ivif.test.service.ProfileBaseService;
import com.mysema.query.SearchResults;
import java.lang.Integer;
import java.lang.String;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Path("/editableProfileGrid")
@Generated
@Stateless
public class EditableProfileGridBaseWS {

    @Inject @Generated
    private ProfileBaseService profileBaseService;

    @Inject
    private ProfileService saveService;


    public static class EditableProfileGridEditableFilterResult {
        public String name;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EditableProfileGridSaveParam extends EditableProfileGridEditableFilterResult {
        public Integer id;
    }
    @POST
    @Path("/save")
    @Consumes("application/json")
    @TransactionAttribute
    public void save(List<EditableProfileGridSaveParam> saveParams) {
        for (EditableProfileGridSaveParam saveParam : saveParams) {
            // Search for this entityToSave
            Profile entityToSave = profileBaseService.find(saveParam.id);
            if (entityToSave == null) {
                // Must create a new entityToSave
                entityToSave = new Profile();
            }
            // Apply modifications
            entityToSave.setName(saveParam.name);

            // Ask for save
            saveService.save(entityToSave);
        }
    }

    public static class EditableProfileGridSearchResult extends EditableProfileGridEditableFilterResult {
        public Integer id;

        public EditableProfileGridSearchResult() {}
        public EditableProfileGridSearchResult(String name, Integer id) {
            this.name = name;
            this.id = id;
        }
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EditableProfileGridSearchFilter extends EditableProfileGridEditableFilterResult {
    }
    public static class EditableProfileGridSearchParam extends GridSearchParam<EditableProfileGridSearchFilter> {}
    @POST
    @Path("/search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResults<EditableProfileGridSearchResult> search(EditableProfileGridSearchParam searchParam) {
        return profileBaseService.search(searchParam);
    }
}
