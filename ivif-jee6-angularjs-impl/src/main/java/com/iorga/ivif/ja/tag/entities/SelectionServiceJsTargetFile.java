package com.iorga.ivif.ja.tag.entities;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.JsTargetFile;
import com.iorga.ivif.ja.tag.configurations.JAConfiguration;
import com.iorga.ivif.ja.tag.configurations.JAConfigurationPreparedWaiter;
import com.iorga.ivif.tag.bean.Selection;
import com.iorga.ivif.util.TargetFileUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SelectionServiceJsTargetFile extends JsTargetFile<String> {

    private JAConfiguration configuration;
    private SelectionModel selectionModel;

    public SelectionServiceJsTargetFile(String id, SelectionModel selectionModel, JAConfiguration configuration, JAGeneratorContext context) {
        super(id, context);
        this.selectionModel = selectionModel;
        this.configuration = configuration;
    }

    @Override
    protected String getFreemarkerTemplateName() {
        return "entities/SelectionService.js.ftl";
    }

    @Override
    public Path getPathRelativeToWebappPath(JAGeneratorContext context) {
        return Paths.get("scripts", "services", StringUtils.capitalize(getId()) + "SelectionService.js");
    }

    /// Getters & Setters

    public JAConfiguration getConfiguration() {
        return configuration;
    }

    public SelectionModel getSelectionModel() {
        return selectionModel;
    }
}
