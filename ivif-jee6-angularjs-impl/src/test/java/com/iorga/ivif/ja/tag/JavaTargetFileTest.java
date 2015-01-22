package com.iorga.ivif.ja.tag;

import com.iorga.ivif.ja.tag.JavaTargetFile.JavaTargetFileId;
import com.iorga.ivif.ja.tag.bean.AngularModuleName;
import com.iorga.ivif.ja.tag.bean.BasePackage;
import com.iorga.ivif.ja.tag.bean.Configurations;
import com.iorga.ivif.ja.tag.configurations.JAConfiguration;
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
        final Configurations element = new Configurations();
        final AngularModuleName angularModuleName = new AngularModuleName();
        angularModuleName.setValue("test");
        element.setAngularModuleName(angularModuleName);
        final BasePackage basePackage = new BasePackage();
        basePackage.setValue("com.iorga.test");
        element.setBasePackage(basePackage);
        JAConfiguration configuration = new JAConfiguration(element);
        SimpleJavaTargetFile targetFile1 = context.getOrCreateTarget(SimpleJavaTargetFile.class, new JavaTargetFileId("test", null, null, configuration));
        SimpleJavaTargetFile targetFile2 = context.getOrCreateTarget(SimpleJavaTargetFile.class, new JavaTargetFileId("test", null, null, configuration));
        Assert.assertEquals(targetFile1, targetFile2);
    }
}
