package com.iorga.ivif.ja.tag.views;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.entities.EntityAttribute;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile;
import com.iorga.ivif.ja.tag.util.TargetFileUtils;
import com.iorga.ivif.tag.PartWaiter;
import com.iorga.ivif.tag.TargetFile;
import com.iorga.ivif.tag.bean.Column;
import com.iorga.ivif.tag.bean.Grid;
import freemarker.template.SimpleHash;
import freemarker.template.Template;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

public class GridTargetFileModel {

    protected Grid grid;
    protected String variableName;
    protected List<GridColumn> columns;
    protected TargetFile targetFile;
    protected EntityTargetFile entityTargetFile;


    public void prepare(JAGeneratorContext context, TargetFile targetFile) throws Exception {
        this.targetFile = targetFile;

        variableName = TargetFileUtils.getVariableNameFromName(grid.getName());

        // Preparing columns
        columns = new ArrayList<>();
        entityTargetFile = context.getOrCreateTargetFile(EntityTargetFile.class, new EntityTargetFile.EntityTargetFileId(grid.getEntity(), context));
        for (Column column : grid.getColumn()) {
            Deque<String> refPath = new LinkedList<>(Arrays.asList(column.getRef().split("\\.")));
            waitToPrepareGridColumnRecursive(refPath, column, entityTargetFile, context, targetFile);
        }
    }

    private void waitToPrepareGridColumnRecursive(final Deque<String> refPath, final Column column, EntityTargetFile entityTargetFile, final JAGeneratorContext context, final TargetFile targetFile) throws Exception {
        String curPath = refPath.remove();
        entityTargetFile.waitForPartToBePrepared(EntityAttribute.class, curPath, new PartWaiter<EntityAttribute, String, JAGeneratorContext>(targetFile, curPath) {
            @Override
            public void onPrepared(EntityAttribute entityAttribute) throws Exception {
                if (refPath.isEmpty()) {
                    // that was the last part of the path, we can build the column
                    columns.add(new GridColumn(column, entityAttribute));
                } else {
                    // this is not the last part of the path, let's wait on next part
                    GridTargetFileModel.this.waitToPrepareGridColumnRecursive(refPath, column, context.getOrCreateTargetFile(EntityTargetFile.class, new EntityTargetFile.EntityTargetFileId(entityAttribute.getType(), context)), context, targetFile);
                }
            }
        });
    }

    public void render(String templateName, JAGeneratorContext context) throws Exception {
        SimpleHash freemarkerContext = context.createSimpleHash();
        freemarkerContext.put("model", this);
        freemarkerContext.put("grid", grid);
        // First process body
        Template template = context.getTemplate(templateName);
        File file = targetFile.getPath(context).toFile();
        // create file structure
        file.getParentFile().mkdirs();
        // before writing to it
        FileOutputStream outputStream = new FileOutputStream(file);
        template.process(freemarkerContext, new OutputStreamWriter(outputStream));
    }

    /// Getters & Setters

    public Grid getGrid() {
        return grid;
    }

    public String getVariableName() {
        return variableName;
    }

    public List<GridColumn> getColumns() {
        return columns;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }
}
