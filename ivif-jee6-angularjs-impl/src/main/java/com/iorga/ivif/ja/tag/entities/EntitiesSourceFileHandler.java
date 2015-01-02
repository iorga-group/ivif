package com.iorga.ivif.ja.tag.entities;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.tag.JAXBSourceFile;
import com.iorga.ivif.tag.SimpleJAXBSourceFileHandler;
import com.iorga.ivif.tag.bean.Entities;
import com.iorga.ivif.tag.bean.Entity;

public class EntitiesSourceFileHandler extends SimpleJAXBSourceFileHandler<Entities, JAGeneratorContext> {
    public EntitiesSourceFileHandler() {
        super(Entities.class);
    }

    @Override
    public void init(JAXBSourceFile<Entities> sourceFile, JAGeneratorContext context) throws Exception {
        super.init(sourceFile, context);
    }

    @Override
    public void prepareTargetFiles(JAXBSourceFile<Entities> sourceFile, JAGeneratorContext context) throws Exception {
        for (final Entity entity : sourceFile.getContext().getEntity()) {
            // Create an entity target file for each declared entity
            EntityTargetFile entityTargetFile = context.getOrCreateTargetFile(EntityTargetFile.class, entity.getName());
            entityTargetFile.setEntity(entity);
        }
    }
}
