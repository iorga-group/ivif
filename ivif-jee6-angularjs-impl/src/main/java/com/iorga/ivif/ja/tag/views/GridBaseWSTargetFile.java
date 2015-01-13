package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.JavaTargetFile;
import com.iorga.ivif.ja.tag.RenderPart;
import com.iorga.ivif.ja.tag.WSTargetFileId;
import com.iorga.ivif.ja.tag.entities.EntityBaseServiceTargetFile;
import com.iorga.ivif.tag.TargetFile;
import com.iorga.ivif.tag.bean.Grid;

public class GridBaseWSTargetFile extends JavaTargetFile<WSTargetFileId> {

    private GridBaseWSTargetFileModel model = new GridBaseWSTargetFileModel();
    private EntityBaseServiceTargetFile baseService;

    public class GridBaseWSTargetFileModel extends GridTargetFileModel {
        private String searchResultSimpleClassName;
        private String searchResultClassName;
        private String searchFilterSimpleClassName;
        private String searchFilterClassName;
        private String searchParamSimpleClassName;
        private String searchParamClassName;

        @Override
        public void prepare(JAGeneratorContext context, TargetFile targetFile) throws Exception {
            super.prepare(context, targetFile);

            this.searchResultSimpleClassName = grid.getName() + "SearchResult";
            this.searchResultClassName = GridBaseWSTargetFile.this.getClassName() + "." + searchResultSimpleClassName;
            this.searchFilterSimpleClassName = grid.getName() + "SearchFilter";
            this.searchFilterClassName = GridBaseWSTargetFile.this.getClassName() + "." + searchFilterSimpleClassName;
            this.searchParamSimpleClassName = grid.getName() + "SearchParam";
            this.searchParamClassName = GridBaseWSTargetFile.this.getClassName() + "." + searchParamSimpleClassName;

            // Adding the search method
            baseService.addRenderPart(new RenderPart("entities/EntityBaseService_gridSearch_bodyPart.java.ftl", this));
        }

        public String getSearchRelationMethodForGridColumn(GridColumn column) {
            switch (column.getEntityAttribute().getType()) {
                case "java.lang.String":
                    return "containsIgnoreCase";
                default:
                    return "eq";
            }
        }

        public EntityBaseServiceTargetFile getBaseService() {
            return baseService;
        }

        /// Getters & Setters

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
    }

    public GridBaseWSTargetFile(WSTargetFileId id, JAGeneratorContext context) {
        super(id, context);
    }

    @Override
    public void prepare(JAGeneratorContext context) throws Exception {
        super.prepare(context);

        model.prepare(context, this);
    }

    @Override
    protected String getFreemarkerBodyTemplateName() {
        return "views/GridBaseWS_body.java.ftl";
    }

    @Override
    public Object getFreemarkerModel() {
        return model;
    }

    public void setGrid(Grid grid) {
        model.setGrid(grid);
    }

    public void setBaseService(EntityBaseServiceTargetFile baseService) {
        this.baseService = baseService;
    }
}
