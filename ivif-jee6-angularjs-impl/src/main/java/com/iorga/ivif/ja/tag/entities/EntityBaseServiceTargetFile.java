package com.iorga.ivif.ja.tag.entities;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.JavaTargetFile;
import com.iorga.ivif.ja.tag.RenderPart;
import com.iorga.ivif.ja.tag.ServiceTargetFileId;
import com.iorga.ivif.ja.tag.configurations.JAConfiguration;
import com.iorga.ivif.ja.tag.configurations.JAConfigurationPreparedWaiter;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile.EntityTargetFileId;
import com.iorga.ivif.ja.tag.util.TargetFileUtils;
import com.iorga.ivif.tag.TargetPreparedWaiter;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class EntityBaseServiceTargetFile extends JavaTargetFile<ServiceTargetFileId> {
    private List<RenderPart> renderParts = new ArrayList<>();
    private EntityTargetFileId entityTargetFileId;
    private String qEntitySimpleClassName;
    private String qEntityClassName;
    private String entityVariableName;
    private String entityClassName;
    private String entitySimpleClassName;
    private EntityTargetFile entityTargetFile;

    public EntityBaseServiceTargetFile(ServiceTargetFileId id, JAGeneratorContext context) {
        super(id, context);
    }


    public void addRenderPart(RenderPart renderPart) {
        renderParts.add(renderPart);
    }

    @Override
    public void prepare(final JAGeneratorContext context) throws Exception {
        super.prepare(context);
        // Compute entity name & qentity name
        // TODO handle entities in another package
        this.entitySimpleClassName = StringUtils.substringBeforeLast(getSimpleClassName(), "BaseService");
        context.waitForEvent(new JAConfigurationPreparedWaiter(this) {

            @Override
            protected void onConfigurationPrepared(JAConfiguration configuration) throws Exception {
                entityTargetFileId = new EntityTargetFileId(entitySimpleClassName, configuration);
                entityClassName = entityTargetFileId.getClassName();
                entityVariableName = TargetFileUtils.getVariableNameFromName(entitySimpleClassName);
                qEntitySimpleClassName = "Q" + entitySimpleClassName;
                qEntityClassName = entityTargetFileId.getPackageName() + "." + qEntitySimpleClassName;

                context.waitForEvent(new TargetPreparedWaiter<EntityTargetFile, EntityTargetFileId, JAGeneratorContext>(EntityTargetFile.class, entityTargetFileId, EntityBaseServiceTargetFile.this) {
                    @Override
                    protected void onTargetPrepared(EntityTargetFile entityTargetFile) throws Exception {
                        EntityBaseServiceTargetFile.this.entityTargetFile = entityTargetFile;
                    }
                });
            }
        });
    }

    @Override
    protected ByteArrayOutputStream renderBody(JAGeneratorContext context) throws IOException, TemplateException {
        SimpleHash freemarkerContext = context.createSimpleHash();
        freemarkerContext.put("model", getFreemarkerModel());
        freemarkerContext.put("util", util);
        freemarkerContext.put("context", context);
        // First process body start
        Template template = context.getTemplate("entities/EntityBaseService_bodyStart.java.ftl");
        ByteArrayOutputStream bodyStream = new ByteArrayOutputStream();
        OutputStreamWriter out = new OutputStreamWriter(bodyStream);
        template.process(freemarkerContext, out);
        // Then each body parts
        for (RenderPart renderPart : renderParts) {
            template = context.getTemplate(renderPart.getFreemarkerTemplateName());
            SimpleHash partContext = context.createSimpleHash();
            partContext.put("baseModel", getFreemarkerModel());
            partContext.put("model", renderPart.getModel());
            partContext.put("util", util);
            partContext.put("context", context);
            template.process(partContext, out);
        }
        // Then body end
        template = context.getTemplate("entities/EntityBaseService_bodyEnd.java.ftl");
        template.process(freemarkerContext, out);
        return bodyStream;
    }

    /// Getters & Setters


    public String getEntityVariableName() {
        return entityVariableName;
    }

    public String getqEntityClassName() {
        return qEntityClassName;
    }

    public String getqEntitySimpleClassName() {
        return qEntitySimpleClassName;
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public String getEntitySimpleClassName() {
        return entitySimpleClassName;
    }

    public EntityTargetFile getEntityTargetFile() {
        return entityTargetFile;
    }
}
