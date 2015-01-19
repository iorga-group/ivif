package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.util.RenderUtils;
import com.iorga.ivif.tag.TargetFile;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GridCtrlJsTargetFile extends TargetFile<JAGeneratorContext, String> {

    public static final Pattern METHOD_NAME_PATTERN = Pattern.compile("(?<![\\w$])([\\p{Alpha}$_][\\w$\\.]*)\\(");

    private GridSourceTagHandler gridSourceTagHandler;
    private String onOpenCode;
    private String onOpenMethod; // TODO handle multiple functions
    private boolean actionOpenViewDefined = false;


    public GridCtrlJsTargetFile(String id, JAGeneratorContext context) {
        super(id, context);
    }

    @Override
    public void prepare(JAGeneratorContext context) throws Exception {
        super.prepare(context);

        // Replace action line parameters with their flatten ("_") real variable name
        // TODO handle multiple functions
        String onOpenAction = gridSourceTagHandler.getElement().getOnOpen();
        if (StringUtils.isNotBlank(onOpenAction)) {
            Matcher matcher = gridSourceTagHandler.LINE_REF_PATTERN.matcher(onOpenAction);
            StringBuffer onOpenCodeBuilder = new StringBuffer(onOpenAction.length());
            while (matcher.find()) {
                String ref = matcher.group();
                ref = "line." + StringUtils.substringAfter(ref, "line.").replaceAll("\\.", "_");
                matcher.appendReplacement(onOpenCodeBuilder, ref);
            }
            matcher.appendTail(onOpenCodeBuilder);
            String onOpenCode = onOpenCodeBuilder.toString();
            // extracting function name + replacing action name
            matcher = METHOD_NAME_PATTERN.matcher(onOpenCode);
            onOpenCodeBuilder = new StringBuffer();
            if (matcher.find()) {
                String methodName = matcher.group(1) + "Action";
                this.onOpenMethod = methodName;
                matcher.appendReplacement(onOpenCodeBuilder, methodName + "(");
            } else {
                throw new IllegalStateException("Couldn't find a function name for onOpenCode '" + onOpenCode + "' while generating " + getPath(context));
            }
            matcher.appendTail(onOpenCodeBuilder);
            this.onOpenCode = onOpenCodeBuilder.toString();
        }
    }

    @Override
    public void render(JAGeneratorContext context) throws Exception {
        RenderUtils.simpleRender("views/GridCtrl.js.ftl", this, context);
    }

    @Override
    public Path getPathRelativeToTargetPath(JAGeneratorContext context) {
        return context.getWebappBaseGenerationPathRelativeToTargetPath().resolve(Paths.get("scripts", "controllers")).resolve(getId()+"Ctrl.js");
    }

    public GridSourceTagHandler getGrid() {
        return gridSourceTagHandler;
    }

    /// Getters & Setters

    public String getOnOpenCode() {
        return onOpenCode;
    }

    public void setGridSourceTagHandler(GridSourceTagHandler gridSourceTagHandler) {
        this.gridSourceTagHandler = gridSourceTagHandler;
    }

    public String getOnOpenMethod() {
        return onOpenMethod;
    }

    public void setActionOpenViewDefined(boolean actionOpenViewDefined) {
        this.actionOpenViewDefined = actionOpenViewDefined;
    }

    public boolean isActionOpenViewDefined() {
        return actionOpenViewDefined;
    }
}
