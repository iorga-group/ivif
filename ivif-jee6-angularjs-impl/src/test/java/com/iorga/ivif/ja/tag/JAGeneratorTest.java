package com.iorga.ivif.ja.tag;

import org.junit.Ignore;
import org.junit.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JAGeneratorTest {

    @Ignore
    @Test
    public void simpleTest() throws Exception {
        JAGenerator generator = new JAGenerator();
        generator.parseAndGenerate(
                Paths.get(getClass().getResource("/simpleTest").toURI()),
                getTargetPath());
        //TODO check if there were no exceptions during render
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
