package com.iorga.ivif.ja.tag.entities;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.tag.DocumentToProcess;
import com.iorga.ivif.tag.JAXBSourceFile;
import com.iorga.ivif.tag.JAXBSourceFileHandler;
import com.iorga.ivif.tag.bean.Entities;
import com.iorga.ivif.tag.bean.Entity;

public class EntitiesSourceFileHandler extends JAXBSourceFileHandler<Entities, JAGeneratorContext, JAXBSourceFile<Entities>> {
    public EntitiesSourceFileHandler() {
        super(Entities.class);
    }

    @Override
    protected JAXBSourceFile<Entities> createSourceFile(Entities rootElement, DocumentToProcess documentToProcess, JAGeneratorContext context) {
        return new JAXBSourceFile<>(rootElement, documentToProcess.getPath());
    }

    @Override
    public void init(JAXBSourceFile<Entities> sourceFile, JAGeneratorContext context) throws Exception {
        super.init(sourceFile, context);
        for (final Entity entity : sourceFile.getContext().getEntity()) {
            // declare each entity that will be created
            context.declareCreatedTargetFile(this, context.getOrCreateTargetFile(EntityTargetFile.class, entity.getName()));
        }
    }

    @Override
    public void prepare(JAXBSourceFile<Entities> sourceFile, JAGeneratorContext context) throws Exception {
        for (final Entity entity : sourceFile.getContext().getEntity()) {
            // Create an entity target file for each declared entity
            EntityTargetFile entityTargetFile = context.getOrCreateTargetFile(EntityTargetFile.class, entity.getName());
            entityTargetFile.setEntity(entity);
        }
    }
}
