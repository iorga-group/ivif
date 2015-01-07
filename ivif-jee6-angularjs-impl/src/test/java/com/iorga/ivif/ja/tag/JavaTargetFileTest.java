package com.iorga.ivif.ja.tag;

import com.iorga.ivif.ja.tag.JavaTargetFile.JavaTargetFileId;
import org.junit.Assert;
import org.junit.Test;

public class JavaTargetFileTest {

    public static class SimpleJavaTargetFile extends JavaTargetFile<JavaTargetFileId> {
        public SimpleJavaTargetFile(JavaTargetFileId id, JAGeneratorContext context) {
            super(id, context);
        }

        @Override
        protected String getFreemarkerBodyTemplateName() {
            return null;
        }
    }

    @Test
    public void idTest() throws Exception {
        JAGeneratorContext context = new JAGeneratorContext();
        SimpleJavaTargetFile targetFile1 = context.getOrCreateTargetFile(SimpleJavaTargetFile.class, new JavaTargetFileId("test", null, null, context));
        SimpleJavaTargetFile targetFile2 = context.getOrCreateTargetFile(SimpleJavaTargetFile.class, new JavaTargetFileId("test", null, null, context));
        Assert.assertEquals(targetFile1, targetFile2);
    }
}
