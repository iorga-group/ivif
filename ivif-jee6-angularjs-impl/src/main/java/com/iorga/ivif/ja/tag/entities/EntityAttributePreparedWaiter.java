package com.iorga.ivif.ja.tag.entities;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile.EntityTargetFileId;
import com.iorga.ivif.tag.TargetPartPreparedWaiter;

public abstract class EntityAttributePreparedWaiter extends TargetPartPreparedWaiter<EntityAttribute, String, EntityTargetFile, EntityTargetFileId, JAGeneratorContext> {
    public EntityAttributePreparedWaiter(String targetPartId, EntityTargetFile target, Object waiterSource) {
        super(EntityAttribute.class, targetPartId, target, waiterSource);
    }

    public EntityAttributePreparedWaiter(String targetPartId, EntityTargetFileId targetId, Object waiterSource) {
        super(EntityAttribute.class, targetPartId, EntityTargetFile.class, targetId, waiterSource);
    }

    @Override
    protected void onTargetPartPrepared(EntityAttribute entityAttribute) throws Exception {
        onEntityAttributePrepared(entityAttribute);
    }

    protected abstract void onEntityAttributePrepared(EntityAttribute entityAttribute) throws Exception;
}
