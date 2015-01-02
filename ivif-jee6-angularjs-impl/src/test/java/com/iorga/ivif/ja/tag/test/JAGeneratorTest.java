package com.iorga.ivif.ja.tag.test;

import com.iorga.ivif.ja.tag.JAGenerator;
import com.iorga.ivif.ja.tag.JAGeneratorContext;
import org.junit.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JAGeneratorTest {

    @Test
    public void simpleTest() throws Exception {
        JAGenerator generator = new JAGenerator();
        generator.parseAndGenerate(
                Paths.get(getClass().getResource("/simpleTest").toURI()),
                getTargetPath());
    }

    protected Path getTargetPath() throws URISyntaxException {
        return Paths.get(getClass().getResource("/").toURI()).getParent().getParent().resolve("target");
    }

    @Test
    public void testWithPackage() throws Exception {
        JAGenerator generator = new JAGenerator();
        generator.parseAndGenerate(
                Paths.get(getClass().getResource("/ivif").toURI()),
                getTargetPath());
    }
}
