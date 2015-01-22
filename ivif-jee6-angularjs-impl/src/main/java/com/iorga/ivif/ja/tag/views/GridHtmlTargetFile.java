package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.util.RenderUtils;
import com.iorga.ivif.tag.TargetFile;
import com.iorga.ivif.tag.TargetPreparedWaiter;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GridHtmlTargetFile extends TargetFile<String, JAGeneratorContext> {

    private GridModel gridModel;

    public GridHtmlTargetFile(String name, JAGeneratorContext context) {
        super(name, context);
    }

    @Override
    public void prepare(JAGeneratorContext context) throws Exception {
        super.prepare(context);

        context.waitForEvent(new TargetPreparedWaiter<GridModel, String, JAGeneratorContext>(GridModel.class, getId(), this) {
            @Override
            protected void onTargetPrepared(GridModel gridModel) throws Exception {
                GridHtmlTargetFile.this.gridModel = gridModel;
            }
        });
    }

    @Override
    public void render(JAGeneratorContext context) throws Exception {
        RenderUtils.simpleRender("views/Grid.html.ftl", this, context);
    }

    @Override
    public Path getPathRelativeToTargetPath(JAGeneratorContext context) {
        return context.getWebappBaseGenerationPathRelativeToTargetPath().resolve(Paths.get("templates", "views")).resolve(getId()+".html");
    }

    public GridModel getGrid() {
        return gridModel;
    }

    /// Getters & Setters
}
