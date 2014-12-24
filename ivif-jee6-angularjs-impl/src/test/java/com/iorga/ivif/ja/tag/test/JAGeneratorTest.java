package com.iorga.ivif.ja.tag.test;

import com.iorga.ivif.ja.tag.JAGenerator;
import com.iorga.ivif.ja.tag.JAGeneratorContext;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JAGeneratorTest {

    @Test
    public void simpleTest() throws Exception {
        JAGenerator generator = new JAGenerator();
        JAGeneratorContext context = new JAGeneratorContext();
        Path baseProjectPath = Paths.get(getClass().getResource("/").toURI()).getParent().getParent();
        context.setBasePath(baseProjectPath);
        generator.parseAndGenerate(Paths.get(getClass().getResource("/simpleTest").toURI()), context);
    }
}
