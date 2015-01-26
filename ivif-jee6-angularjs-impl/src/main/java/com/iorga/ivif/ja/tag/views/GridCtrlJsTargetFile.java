package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.JsTargetFile;
import com.iorga.ivif.ja.tag.configurations.JAConfiguration;
import com.iorga.ivif.ja.tag.configurations.JAConfigurationPreparedWaiter;
import com.iorga.ivif.ja.tag.util.RenderUtils;
import com.iorga.ivif.tag.TargetPreparedWaiter;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GridCtrlJsTargetFile extends JsTargetFile<String> {

    public static final Pattern METHOD_NAME_PATTERN = Pattern.compile("(?<![\\w$])([\\p{Alpha}$_][\\w$\\.]*)\\(");

    private String onOpenCode;
    private String onOpenMethod; // TODO handle multiple functions
    private boolean actionOpenViewDefined = false;
    private JAConfiguration configuration;
    private GridModel gridModel;


    public GridCtrlJsTargetFile(String id, JAGeneratorContext context) {
        super(id, context);
    }

    @Override
    public void prepare(final JAGeneratorContext context) throws Exception {
        super.prepare(context);

        context.waitForEvent(new TargetPreparedWaiter<GridModel, String, JAGeneratorContext>(GridModel.class, getId(), this) {
            @Override
            protected void onTargetPrepared(GridModel gridModel) throws Exception {
                GridCtrlJsTargetFile.this.gridModel = gridModel;

                // Replace action line parameters with their flatten ("_") real variable name
                // TODO handle multiple functions
                String onOpenAction = gridModel.getElement().getOnOpen();
                if (StringUtils.isNotBlank(onOpenAction)) {
                    Matcher matcher = GridModel.LINE_REF_PATTERN.matcher(onOpenAction);
                    StringBuffer onOpenCodeBuilder = new StringBuffer(onOpenAction.length());
                    while (matcher.find()) {
                        String ref = matcher.group();
                        ref = "\\$line." + StringUtils.substringAfter(ref, "$line.").replaceAll("\\.", "_");
                        matcher.appendReplacement(onOpenCodeBuilder, ref);
                    }
                    matcher.appendTail(onOpenCodeBuilder);
                    String onOpenCode = onOpenCodeBuilder.toString();
                    // extracting function name + replacing action name
                    matcher = METHOD_NAME_PATTERN.matcher(onOpenCode);
                    onOpenCodeBuilder = new StringBuffer();
                    if (matcher.find()) {
                        String methodName = matcher.group(1) + "Action";
                        onOpenMethod = methodName;
                        matcher.appendReplacement(onOpenCodeBuilder, methodName + "(");
                    } else {
                        throw new IllegalStateException("Couldn't find a function name for onOpenCode '" + onOpenCode + "' while generating " + getPath(context));
                    }
                    matcher.appendTail(onOpenCodeBuilder);
                    GridCtrlJsTargetFile.this.onOpenCode = onOpenCodeBuilder.toString();
                }
            }
        });

        context.waitForEvent(new JAConfigurationPreparedWaiter(this) {
            @Override
            protected void onConfigurationPrepared(JAConfiguration configuration) throws Exception {
                GridCtrlJsTargetFile.this.configuration = configuration;
            }
        });
    }


    @Override
    protected String getFreemarkerTemplateName() {
        return "views/GridCtrl.js.ftl";
    }

    @Override
    public Path getPathRelativeToWebappPath(JAGeneratorContext context) {
        return Paths.get("scripts", "controllers", getId() + "Ctrl.js");
    }

    public GridModel getGrid() {
        return gridModel;
    }

    /// Getters & Setters

    public String getOnOpenCode() {
        return onOpenCode;
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

    public JAConfiguration getConfiguration() {
        return configuration;
    }
}
