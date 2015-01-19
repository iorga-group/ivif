package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.util.RenderUtils;
import com.iorga.ivif.ja.tag.util.TargetFileUtils;
import com.iorga.ivif.tag.TargetFile;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ActionOpenViewServiceJsTargetFile extends TargetFile<JAGeneratorContext, String> {

    private ActionOpenViewSourceTagHandler actionOpenViewSourceTagHandler;
    private String gridPath;

    public ActionOpenViewServiceJsTargetFile(String id, JAGeneratorContext context) {
        super(id, context);
    }

    @Override
    public void prepare(JAGeneratorContext context) throws Exception {
        super.prepare(context);

        this.gridPath = "/" + TargetFileUtils.getVariableNameFromName(actionOpenViewSourceTagHandler.getElement().getGridName());
    }

    @Override
    public void render(JAGeneratorContext context) throws Exception {
        RenderUtils.simpleRender("views/ActionOpenViewService.js.ftl", this, context);
    }

    @Override
    public Path getPathRelativeToTargetPath(JAGeneratorContext context) {
        return context.getWebappBaseGenerationPathRelativeToTargetPath().resolve(Paths.get("scripts", "services")).resolve(StringUtils.capitalize(getId())+"ActionService.js");
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
}
