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
        public void prepare(SimpleSourceFile sourceFile, SimpleContext context) throws Exception {

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

    private static class TestPrepareDependencies {
        boolean singleRootPrepared = false;
        boolean rootPrepared = false;
        boolean aPrepared = false;
        boolean bPrepared = false;
        boolean aChildPrepared = false;
    }
    @Test
    public void testPrepareDependencies() throws Exception {

        SimpleGenerator generator = new SimpleGenerator();
        SimpleContext context = generator.createGeneratorContext();
        final TestPrepareDependencies testPrepareDependencies = new TestPrepareDependencies();

        generator.parseDocumentToProcessThenInitSourceFileHandler(context, null, new SimpleSourceFileHandler() {
            @Override
            public void init(SimpleSourceFile sourceFile, SimpleContext context) throws Exception {
                super.init(sourceFile, context);
                context.declareCreatedTargetFile(this, context.getOrCreateTargetFile(SimpleTargetFile.class, "singleRoot"));
            }

            @Override
            public void prepare(SimpleSourceFile sourceFile, SimpleContext context) throws Exception {
                super.prepare(sourceFile, context);
                assert !testPrepareDependencies.singleRootPrepared;
                testPrepareDependencies.singleRootPrepared = true;
            }
        });
        generator.parseDocumentToProcessThenInitSourceFileHandler(context, null, new SimpleSourceFileHandler() {
            @Override
            public void init(SimpleSourceFile sourceFile, SimpleContext context) throws Exception {
                super.init(sourceFile, context);
                context.declareCreatedTargetFile(this, context.getOrCreateTargetFile(SimpleTargetFile.class, "root"));
            }

            @Override
            public void prepare(SimpleSourceFile sourceFile, SimpleContext context) throws Exception {
                super.prepare(sourceFile, context);
                assert !testPrepareDependencies.aPrepared;
                assert !testPrepareDependencies.bPrepared;
                testPrepareDependencies.rootPrepared = true;
            }
        });
        generator.parseDocumentToProcessThenInitSourceFileHandler(context, null, new SimpleSourceFileHandler() {
            @Override
            public void init(SimpleSourceFile sourceFile, SimpleContext context) throws Exception {
                super.init(sourceFile, context);
                context.declareRequiredTargetFile(this, context.getOrCreateTargetFile(SimpleTargetFile.class, "root"));
                context.declareCreatedTargetFile(this, context.getOrCreateTargetFile(SimpleTargetFile.class, "a"));
            }

            @Override
            public void prepare(SimpleSourceFile sourceFile, SimpleContext context) throws Exception {
                super.prepare(sourceFile, context);
                assert testPrepareDependencies.rootPrepared;
                assert !testPrepareDependencies.aChildPrepared;
                testPrepareDependencies.aPrepared = true;
            }
        });
        generator.parseDocumentToProcessThenInitSourceFileHandler(context, null, new SimpleSourceFileHandler() {
            @Override
            public void init(SimpleSourceFile sourceFile, SimpleContext context) throws Exception {
                super.init(sourceFile, context);
                context.declareRequiredTargetFile(this, context.getOrCreateTargetFile(SimpleTargetFile.class, "root"));
                context.declareCreatedTargetFile(this, context.getOrCreateTargetFile(SimpleTargetFile.class, "b"));
            }

            @Override
            public void prepare(SimpleSourceFile sourceFile, SimpleContext context) throws Exception {
                super.prepare(sourceFile, context);
                assert testPrepareDependencies.rootPrepared;
                assert !testPrepareDependencies.bPrepared;
                testPrepareDependencies.bPrepared = true;
            }
        });
        generator.parseDocumentToProcessThenInitSourceFileHandler(context, null, new SimpleSourceFileHandler() {
            @Override
            public void init(SimpleSourceFile sourceFile, SimpleContext context) throws Exception {
                super.init(sourceFile, context);
                context.declareRequiredTargetFile(this, context.getOrCreateTargetFile(SimpleTargetFile.class, "a"));
                context.declareCreatedTargetFile(this, context.getOrCreateTargetFile(SimpleTargetFile.class, "aChild"));
            }

            @Override
            public void prepare(SimpleSourceFile sourceFile, SimpleContext context) throws Exception {
                super.prepare(sourceFile, context);
                assert testPrepareDependencies.aPrepared;
                testPrepareDependencies.aChildPrepared = true;
            }
        });

        generator.prepareSourceFileHandlers(context);

        assert testPrepareDependencies.aChildPrepared;
        assert testPrepareDependencies.singleRootPrepared;
        assert testPrepareDependencies.bPrepared;
    }
}
