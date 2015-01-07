package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
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

    private GridTargetFileModel model = new GridTargetFileModel();

    public GridHtmlTargetFile(String name, JAGeneratorContext context) {
        super(name, context);
    }

    @Override
    public void prepare(JAGeneratorContext context) throws Exception {
        super.prepare(context);

        model.prepare(context, this);
    }

    @Override
    public void render(JAGeneratorContext context) throws Exception {
        model.render("views/Grid.html.ftl", context);
    }

    @Override
    public Path getPathRelativeToTargetPath(JAGeneratorContext context) {
        return context.getWebappBaseGenerationPathRelativeToTargetPath().resolve(Paths.get("templates", "views")).resolve(getId()+".html");
    }

    public void setGrid(Grid grid) {
        model.setGrid(grid);
    }

    /// Getters & Setters
}
