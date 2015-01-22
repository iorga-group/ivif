package com.iorga.ivif.ja.tag;

import com.iorga.ivif.ja.tag.util.RenderUtils;
import com.iorga.ivif.tag.TargetFile;

import java.nio.file.Path;

public abstract class JsTargetFile<I> extends TargetFile<I, JAGeneratorContext> {

    public JsTargetFile(I id, JAGeneratorContext context) {
        super(id, context);
    }

    protected abstract String getFreemarkerTemplateName();

    public abstract Path getPathRelativeToWebappPath(JAGeneratorContext context);

    @Override
    public void render(JAGeneratorContext context) throws Exception {
        RenderUtils.simpleRender(getFreemarkerTemplateName(), this, context);
    }

    @Override
    public Path getPathRelativeToTargetPath(JAGeneratorContext context) {
        return context.getWebappBaseGenerationPathRelativeToTargetPath().resolve(getPathRelativeToWebappPath(context));
    }
}
