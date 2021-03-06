package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.*;
import com.iorga.ivif.ja.tag.entities.EntityBaseServiceTargetFile;
import com.iorga.ivif.ja.tag.views.GridModel.GridColumn;
import com.iorga.ivif.tag.TargetPreparedWaiter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class GridBaseWSTargetFile extends JavaTargetFile<WSTargetFileId> {

    private final String gridName;
    private GridModel gridModel;
    private EntityBaseServiceTargetFile baseService;

    private String searchResultSimpleClassName;
    private String searchResultClassName;
    private String searchFilterSimpleClassName;
    private String searchFilterClassName;
    private String searchParamSimpleClassName;
    private String searchParamClassName;
    private String saveParamSimpleClassName;
    private String saveParamClassName;

    private List<OpenViewAction> openViewActions = new ArrayList<>();

    public class OpenViewAction {
        private final ActionOpenViewModel action;
        private final String simpleClassName;
        private final String className;
        private final String variableName;
        private final List<String> rolesAllowed;

        public OpenViewAction(ActionOpenViewModel action) {
            this.action = action;
            this.variableName = action.getElement().getName();
            this.simpleClassName = StringUtils.capitalize(this.variableName);
            this.className = GridBaseWSTargetFile.this.getClassName() + "." + simpleClassName;
            this.rolesAllowed = new ArrayList<>(action.getElement().getRolesAllowed());
        }

        public ActionOpenViewModel getAction() {
            return action;
        }

        public QueryModel getQueryModel() {
            return action.getQueryModel();
        }

        public String getSimpleClassName() {
            return simpleClassName;
        }

        public String getClassName() {
            return className;
        }

        public String getVariableName() {
            return variableName;
        }

        public List<String> getRolesAllowed() {
            return rolesAllowed;
        }
    }

    public GridBaseWSTargetFile(WSTargetFileId id, JAGeneratorContext context, String gridName) {
        super(id, context);
        this.gridName = gridName;
    }

    @Override
    public void prepare(final JAGeneratorContext context) throws Exception {
        super.prepare(context);

        searchResultSimpleClassName = gridName + "SearchResult";
        searchResultClassName = getClassName() + "." + searchResultSimpleClassName;
        searchFilterSimpleClassName = gridName + "SearchFilter";
        searchFilterClassName = getClassName() + "." + searchFilterSimpleClassName;
        searchParamSimpleClassName = gridName + "SearchParam";
        searchParamClassName = getClassName() + "." + searchParamSimpleClassName;
        saveParamSimpleClassName = gridName + "SaveParam";
        saveParamClassName = getClassName() + "." + saveParamSimpleClassName;

        context.waitForEvent(new TargetPreparedWaiter<GridModel, String, JAGeneratorContext>(GridModel.class, gridName, this) {

            @Override
            protected void onTargetPrepared(GridModel gridModel) throws Exception {
                GridBaseWSTargetFile.this.gridModel = gridModel;

                context.waitForEvent(new TargetPreparedWaiter<EntityBaseServiceTargetFile, ServiceTargetFileId, JAGeneratorContext>(EntityBaseServiceTargetFile.class, gridModel.getServiceTargetFileId(), GridBaseWSTargetFile.this) {
                    @Override
                    protected void onTargetPrepared(EntityBaseServiceTargetFile baseService) throws Exception {
                        GridBaseWSTargetFile.this.baseService = baseService;

                        // Adding the search method
                        baseService.addRenderPart(new RenderPart("entities/EntityBaseService_gridSearch_bodyPart.java.ftl", GridBaseWSTargetFile.this, GridBaseWSTargetFile.this));
                    }
                });
            }
        });
    }

    @Override
    protected String getFreemarkerBodyTemplateName() {
        return "views/GridBaseWS_body.java.ftl";
    }

    public void addActionOpenView(ActionOpenViewModel action) {
        openViewActions.add(new OpenViewAction(action));
    }



    public GridModel getGrid() {
        return gridModel;
    }


    /// Getters & Setters

    public EntityBaseServiceTargetFile getBaseService() {
        return baseService;
    }

    public String getSearchResultSimpleClassName() {
        return searchResultSimpleClassName;
    }

    public String getSearchResultClassName() {
        return searchResultClassName;
    }

    public String getSearchFilterSimpleClassName() {
        return searchFilterSimpleClassName;
    }

    public String getSearchFilterClassName() {
        return searchFilterClassName;
    }

    public String getSearchParamSimpleClassName() {
        return searchParamSimpleClassName;
    }

    public String getSearchParamClassName() {
        return searchParamClassName;
    }

    public List<OpenViewAction> getOpenViewActions() {
        return openViewActions;
    }

    public String getSaveParamClassName() {
        return saveParamClassName;
    }

    public String getSaveParamSimpleClassName() {
        return saveParamSimpleClassName;
    }
}
