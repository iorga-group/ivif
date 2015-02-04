package com.iorga.ivif.ja.tag.entities;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.ServiceTargetFileId;
import com.iorga.ivif.ja.tag.configurations.JAConfiguration;
import com.iorga.ivif.ja.tag.configurations.JAConfigurationPreparedWaiter;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile.EntityTargetFileId;
import com.iorga.ivif.tag.JAXBSourceTagHandler;
import com.iorga.ivif.tag.TargetFactory;
import com.iorga.ivif.tag.bean.Entity;

import javax.xml.bind.JAXBException;

public class EntitySourceTagHandler extends JAXBSourceTagHandler<Entity, JAGeneratorContext> {

    public EntitySourceTagHandler() throws JAXBException {
        super(Entity.class);
    }

    @Override
    public void declareTargets(final JAGeneratorContext context) throws Exception {
        // Create an entity target file for each declared entity
        context.waitForEvent(new JAConfigurationPreparedWaiter(this) {
            @Override
            protected void onConfigurationPrepared(JAConfiguration configuration) throws Exception {
                final EntityTargetFileId entityTargetFileId = new EntityTargetFileId(element.getName(), element.getPackage(), configuration);
                context.getOrCreateTarget(EntityTargetFile.class, entityTargetFileId, new TargetFactory<EntityTargetFile, EntityTargetFileId, JAGeneratorContext>() {
                    @Override
                    public EntityTargetFile createTarget() throws Exception {
                        return new EntityTargetFile(entityTargetFileId, context, element);
                    }
                });

                // Create Java Entity Base Service
                context.getOrCreateTarget(EntityBaseServiceTargetFile.class, new ServiceTargetFileId(element.getName() + "BaseService", null, configuration));
            }
        });
    }
}
