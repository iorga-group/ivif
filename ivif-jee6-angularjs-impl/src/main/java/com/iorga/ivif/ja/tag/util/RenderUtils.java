package com.iorga.ivif.ja.tag.util;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.tag.TargetFile;
import freemarker.template.SimpleHash;
import freemarker.template.Template;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class RenderUtils {

    public static void simpleRender(String templateName, TargetFile targetFile, JAGeneratorContext context) throws Exception {
        SimpleHash freemarkerContext = context.createSimpleHash();
        freemarkerContext.put("model", targetFile);
        freemarkerContext.put("context", context);
        // First process body
        Template template = context.getTemplate(templateName);
        File file = targetFile.getPath(context).toFile();
        // create file structure
        file.getParentFile().mkdirs();
        // before writing to it
        FileOutputStream outputStream = new FileOutputStream(file);
        template.process(freemarkerContext, new OutputStreamWriter(outputStream));
    }
}
