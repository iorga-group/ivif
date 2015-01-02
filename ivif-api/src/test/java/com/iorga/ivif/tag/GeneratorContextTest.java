package com.iorga.ivif.tag;

import org.junit.Test;
import org.w3c.dom.Document;

import java.nio.file.Path;

public class GeneratorContextTest {

    public static class SimpleContext extends GeneratorContext<SimpleContext> {
    }
    public static class SimpleGenerator extends Generator<SimpleContext> {

        @Override
        public SimpleContext createGeneratorContext() {
            return new SimpleContext();
        }

        @Override
        public SourceFileHandler<SimpleContext, ?> getSourceFileHandler(Document document) {
            return null;
        }
    }
    public static class SimpleSourceFile implements SourceFile {
        @Override
        public Path getPath() {
            return null;
        }
    }
    public static class SimpleSourceFileHandler implements SourceFileHandler<SimpleContext,SimpleSourceFile> {
        @Override
        public SimpleSourceFile parse(DocumentToProcess documentToProcess, SimpleContext context) throws Exception {
            return null;
        }

        @Override
        public void init(SimpleSourceFile sourceFile, SimpleContext context) throws Exception {

        }

        @Override
        public void prepareTargetFiles(SimpleSourceFile sourceFile, SimpleContext context) throws Exception {

        }
    }
    public static class SimpleTargetFile extends TargetFile<SimpleContext, String> {
        public SimpleTargetFile(String id, SimpleContext context) {
            super(id, context);
        }

        @Override
        public void render(SimpleContext context) throws Exception {

        }
    }

}
