package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.util.RenderUtils;
import com.iorga.ivif.tag.TargetFile;
import com.iorga.ivif.tag.bean.Grid;
import freemarker.template.SimpleHash;
import freemarker.template.Template;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GridHtmlTargetFile extends TargetFile<JAGeneratorContext, String> {

    private GridSourceTagHandler gridSourceTagHandler;

    public GridHtmlTargetFile(String name, JAGeneratorContext context) {
        super(name, context);
    }

    @Override
    public void render(JAGeneratorContext context) throws Exception {
        RenderUtils.simpleRender("views/Grid.html.ftl", this, context);
    }

    @Override
    public Path getPathRelativeToTargetPath(JAGeneratorContext context) {
        return context.getWebappBaseGenerationPathRelativeToTargetPath().resolve(Paths.get("templates", "views")).resolve(getId()+".html");
    }

    public GridSourceTagHandler getGrid() {
        return gridSourceTagHandler;
    }

    /// Getters & Setters

    public void setGridSourceTagHandler(GridSourceTagHandler gridSourceTagHandler) {
        this.gridSourceTagHandler = gridSourceTagHandler;
    }
}
