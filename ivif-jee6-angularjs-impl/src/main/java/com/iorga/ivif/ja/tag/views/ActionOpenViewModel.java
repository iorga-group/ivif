package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.tag.AbstractTarget;
import com.iorga.ivif.tag.bean.ActionOpenView;

public class ActionOpenViewModel extends AbstractTarget<String, JAGeneratorContext> {
    private final ActionOpenView element;
    private final QueryModel queryModel;
    private final GridBaseWSTargetFile gridBaseWSTargetFile;

    public ActionOpenViewModel(String id, ActionOpenView element, QueryModel queryModel, GridBaseWSTargetFile gridBaseWSTargetFile) {
        super(id);
        this.element = element;
        this.queryModel = queryModel;
        this.gridBaseWSTargetFile = gridBaseWSTargetFile;
    }

    public ActionOpenView getElement() {
        return element;
    }

    public QueryModel getQueryModel() {
        return queryModel;
    }

    public GridBaseWSTargetFile getGridBaseWSTargetFile() {
        return gridBaseWSTargetFile;
    }
}
