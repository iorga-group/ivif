package com.iorga.ivif.ja.tag;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import static org.assertj.core.api.Assertions.*;

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
        // Now compare expected hierarchy
        final Path generatedFilesBasePath = getTargetPath().resolve("ivif-generated-sources");
        final Path expectedFilesBasePath = Paths.get(getClass().getResource("/ivif-expected-generated-sources").toURI());

        Files.walkFileTree(expectedFilesBasePath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path expectedFile, BasicFileAttributes attrs) throws IOException {
                Path relativePath = expectedFilesBasePath.relativize(expectedFile);
                assertThat(generatedFilesBasePath.resolve(relativePath).toFile()).hasContentEqualTo(expectedFile.toFile());
                return super.visitFile(expectedFile, attrs);
            }
        });
    }
}
