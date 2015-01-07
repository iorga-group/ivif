package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.tag.TargetFile;
import com.iorga.ivif.tag.bean.Grid;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GridCtrlJsTargetFile extends TargetFile<JAGeneratorContext, String> {

    private GridTargetFileModel model = new GridTargetFileModel();


    public GridCtrlJsTargetFile(String id, JAGeneratorContext context) {
        super(id, context);
    }


    @Override
    public void prepare(JAGeneratorContext context) throws Exception {
        super.prepare(context);

        model.prepare(context, this);
    }

    @Override
    public void render(JAGeneratorContext context) throws Exception {
        model.render("views/GridCtrl.js.ftl", context);
    }

    @Override
    public Path getPathRelativeToTargetPath(JAGeneratorContext context) {
        return context.getWebappBaseGenerationPathRelativeToTargetPath().resolve(Paths.get("scripts", "controllers")).resolve(getId()+"Ctlr.js");
    }

    public void setGrid(Grid grid) {
        model.setGrid(grid);
    }
}
