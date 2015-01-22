package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.JsTargetFile;
import com.iorga.ivif.ja.tag.configurations.JAConfiguration;
import com.iorga.ivif.ja.tag.configurations.JAConfigurationPreparedWaiter;
import com.iorga.ivif.ja.tag.util.TargetFileUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ActionOpenViewServiceJsTargetFile extends JsTargetFile<String> {

    private ActionOpenViewSourceTagHandler actionOpenViewSourceTagHandler;
    private String gridPath;
    private JAConfiguration configuration;

    public ActionOpenViewServiceJsTargetFile(String id, JAGeneratorContext context) {
        super(id, context);
    }

    @Override
    public void prepare(JAGeneratorContext context) throws Exception {
        super.prepare(context);

        this.gridPath = "/" + TargetFileUtils.getVariableNameFromName(actionOpenViewSourceTagHandler.getElement().getGridName());

        context.waitForEvent(new JAConfigurationPreparedWaiter(this) {

            @Override
            protected void onConfigurationPrepared(JAConfiguration configuration) throws Exception {
                ActionOpenViewServiceJsTargetFile.this.configuration = configuration;
            }
        });
    }

    @Override
    protected String getFreemarkerTemplateName() {
        return "views/ActionOpenViewService.js.ftl";
    }

    @Override
    public Path getPathRelativeToWebappPath(JAGeneratorContext context) {
        return Paths.get("scripts", "services", StringUtils.capitalize(getId()) + "ActionService.js");
    }

    public ActionOpenViewSourceTagHandler getActionOpenView() {
        return actionOpenViewSourceTagHandler;
    }

    /// Getters & Setters

    public void setActionOpenViewSourceTagHandler(ActionOpenViewSourceTagHandler actionOpenViewSourceTagHandler) {
        this.actionOpenViewSourceTagHandler = actionOpenViewSourceTagHandler;
    }

    public ActionOpenViewSourceTagHandler getActionOpenViewSourceTagHandler() {
        return actionOpenViewSourceTagHandler;
    }

    public String getGridPath() {
        return gridPath;
    }

    public JAConfiguration getConfiguration() {
        return configuration;
    }
}
