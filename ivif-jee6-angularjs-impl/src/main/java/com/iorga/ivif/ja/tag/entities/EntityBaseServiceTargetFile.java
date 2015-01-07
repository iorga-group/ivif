package com.iorga.ivif.ja.tag.entities;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.JavaTargetFile;
import com.iorga.ivif.ja.tag.RenderPart;
import com.iorga.ivif.ja.tag.ServiceTargetFileId;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile.EntityTargetFileId;
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
    private EntityTargetFile entityTargetFile;
    private String qEntitySimpleClassName;
    private String qEntityClassName;

    public EntityBaseServiceTargetFile(ServiceTargetFileId id, JAGeneratorContext context) {
        super(id, context);
    }


    public void addRenderPart(RenderPart renderPart) {
        renderParts.add(renderPart);
    }

    @Override
    public void prepare(JAGeneratorContext context) throws Exception {
        super.prepare(context);
        // Compute entity name & qentity name
        // TODO handle entities in another package
        String entitySimpleClassName = StringUtils.substringBeforeLast(getSimpleClassName(), "BaseService");
        entityTargetFile = context.getOrCreateTargetFile(EntityTargetFile.class, new EntityTargetFileId(entitySimpleClassName, context));
        qEntitySimpleClassName = "Q" + entityTargetFile.getSimpleClassName();
        qEntityClassName = entityTargetFile.getPackageName() + "." + qEntitySimpleClassName;
    }

    @Override
    protected ByteArrayOutputStream renderBody(JAGeneratorContext context) throws IOException, TemplateException {
        SimpleHash freemarkerContext = context.createSimpleHash();
        freemarkerContext.put("model", getFreemarkerModel());
        freemarkerContext.put("util", util);
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
            template.process(partContext, out);
        }
        // Then body end
        template = context.getTemplate("entities/EntityBaseService_bodyEnd.java.ftl");
        template.process(freemarkerContext, out);
        return bodyStream;
    }

    /// Getters & Setters

    public EntityTargetFile getEntityTargetFile() {
        return entityTargetFile;
    }

    public String getqEntityClassName() {
        return qEntityClassName;
    }

    public String getqEntitySimpleClassName() {
        return qEntitySimpleClassName;
    }
}
