package com.iorga.ivif.ja.tag.entities;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile.EntityTargetFileId;
import com.iorga.ivif.tag.JAXBSourceTagHandler;
import com.iorga.ivif.tag.bean.Entity;

import javax.xml.bind.JAXBException;

public class EntitySourceTagHandler extends JAXBSourceTagHandler<Entity, JAGeneratorContext> {

    public EntitySourceTagHandler() throws JAXBException {
        super(Entity.class);
    }

    @Override
    public void prepareTargetFiles(JAGeneratorContext context) throws Exception {
        // Create an entity target file for each declared entity
        EntityTargetFile entityTargetFile = context.getOrCreateTargetFile(EntityTargetFile.class, new EntityTargetFileId(element.getName(), element.getPackage(), context));
        entityTargetFile.setEntity(element);
    }
}
