package com.iorga.ivif.ja.tag.entities;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.JavaTargetFile;
import com.iorga.ivif.ja.tag.configurations.JAConfiguration;
import com.iorga.ivif.ja.tag.entities.EnumSelectionTargetFile.EnumSelectionTargetFileId;
import com.iorga.ivif.ja.tag.JavaTargetFile.JavaTargetFileId;

public class EnumSelectionTargetFile extends JavaTargetFile<EnumSelectionTargetFileId> {


    private final SelectionModel selectionModel;


    public static class EnumSelectionTargetFileId extends JavaTargetFileId {
        public EnumSelectionTargetFileId(String simpleOrFullClassName, JAConfiguration configuration) {
            super(simpleOrFullClassName, null, "entity.select", configuration);
        }
    }

    public EnumSelectionTargetFile(EnumSelectionTargetFileId id, SelectionModel selectionModel, JAGeneratorContext context) {
        super(id, context);
        this.selectionModel = selectionModel;
    }

    @Override
    protected String getFreemarkerBodyTemplateName() {
        return "entities/EnumSelection_body.java.ftl";
    }

    public SelectionModel getSelectionModel() {
        return selectionModel;
    }
}
