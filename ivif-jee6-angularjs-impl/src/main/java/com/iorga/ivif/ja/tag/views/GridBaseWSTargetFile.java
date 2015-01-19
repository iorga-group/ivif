package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.JavaTargetFile;
import com.iorga.ivif.ja.tag.RenderPart;
import com.iorga.ivif.ja.tag.WSTargetFileId;
import com.iorga.ivif.ja.tag.entities.EntityBaseServiceTargetFile;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import com.iorga.ivif.ja.tag.views.GridSourceTagHandler.GridColumn;

public class GridBaseWSTargetFile extends JavaTargetFile<WSTargetFileId> {

    private EntityBaseServiceTargetFile baseService;
    private GridSourceTagHandler gridSourceTagHandler;

    private String searchResultSimpleClassName;
    private String searchResultClassName;
    private String searchFilterSimpleClassName;
    private String searchFilterClassName;
    private String searchParamSimpleClassName;
    private String searchParamClassName;

    private List<OpenViewAction> openViewActions = new ArrayList<>();

    public class OpenViewAction {
        private final ActionOpenViewSourceTagHandler action;
        private final String simpleClassName;
        private final String className;
        private final String variableName;

        public OpenViewAction(ActionOpenViewSourceTagHandler action) {
            this.action = action;
            this.variableName = action.getElement().getName();
            this.simpleClassName = StringUtils.capitalize(this.variableName);
            this.className = GridBaseWSTargetFile.this.getClassName() + "." + simpleClassName;
        }

        public ActionOpenViewSourceTagHandler getAction() {
            return action;
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
    }

    public GridBaseWSTargetFile(WSTargetFileId id, JAGeneratorContext context) {
        super(id, context);
    }

    @Override
    public void prepare(JAGeneratorContext context) throws Exception {
        super.prepare(context);

        String gridName = gridSourceTagHandler.getElement().getName();

        this.searchResultSimpleClassName = gridName + "SearchResult";
        this.searchResultClassName = GridBaseWSTargetFile.this.getClassName() + "." + searchResultSimpleClassName;
        this.searchFilterSimpleClassName = gridName + "SearchFilter";
        this.searchFilterClassName = GridBaseWSTargetFile.this.getClassName() + "." + searchFilterSimpleClassName;
        this.searchParamSimpleClassName = gridName + "SearchParam";
        this.searchParamClassName = GridBaseWSTargetFile.this.getClassName() + "." + searchParamSimpleClassName;

        // Adding the search method
        baseService.addRenderPart(new RenderPart("entities/EntityBaseService_gridSearch_bodyPart.java.ftl", this));
    }

    @Override
    protected String getFreemarkerBodyTemplateName() {
        return "views/GridBaseWS_body.java.ftl";
    }

    public void addActionOpenView(ActionOpenViewSourceTagHandler action) {
        openViewActions.add(new OpenViewAction(action));
    }



    public GridSourceTagHandler getGrid() {
        return gridSourceTagHandler;
    }


    public String getSearchRelationMethodForGridColumn(GridColumn column) {
        switch (column.getEntityAttribute().getType()) {
            case "java.lang.String":
                return "containsIgnoreCase";
            default:
                return "eq";
        }
    }

    /// Getters & Setters

    public void setGridSourceTagHandler(GridSourceTagHandler gridSourceTagHandler) {
        this.gridSourceTagHandler = gridSourceTagHandler;
    }

    public GridSourceTagHandler getGridSourceTagHandler() {
        return gridSourceTagHandler;
    }

    public EntityBaseServiceTargetFile getBaseService() {
        return baseService;
    }

    public void setBaseService(EntityBaseServiceTargetFile baseService) {
        this.baseService = baseService;
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
}
