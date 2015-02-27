package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.JsTargetFile;
import com.iorga.ivif.ja.tag.configurations.JAConfiguration;
import com.iorga.ivif.ja.tag.configurations.JAConfigurationPreparedWaiter;
import com.iorga.ivif.ja.tag.views.GridModel.ToolbarButton;
import com.iorga.ivif.ja.tag.views.JsExpressionParser.JsExpression;
import com.iorga.ivif.tag.TargetPreparedWaiter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class GridCtrlJsTargetFile extends JsTargetFile<String> {

    public static final Pattern METHOD_NAME_PATTERN = Pattern.compile("(?<![\\w$])([\\p{Alpha}$_][\\w$\\.]*)\\(");

    private boolean actionOpenViewDefined = false;
    private JAConfiguration configuration;
    private GridModel gridModel;
    private Set<String> injections;


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

                // Compute injections
                injections = new LinkedHashSet<>();
                addInjectionsIfExpressionExists(gridModel.getOnOpen());
                addInjectionsIfExpressionExists(gridModel.getOnSelect());
                for (ToolbarButton toolbarButton : gridModel.getToolbarButtons()) {
                    final JsExpression actionExpression = toolbarButton.getActionExpression();
                    if (actionExpression != null) {
                        injections.addAll(actionExpression.getInjections());
                    }
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

    private void addInjectionsIfExpressionExists(JsExpression expression) {
        if (expression != null) {
            injections.addAll(expression.getInjections());
        }
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

    public void setActionOpenViewDefined(boolean actionOpenViewDefined) {
        this.actionOpenViewDefined = actionOpenViewDefined;
    }

    public boolean isActionOpenViewDefined() {
        return actionOpenViewDefined;
    }

    public JAConfiguration getConfiguration() {
        return configuration;
    }

    public Set<String> getInjections() {
        return injections;
    }
}
