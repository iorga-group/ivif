package com.iorga.ivif.tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public abstract class Generator<C extends GeneratorContext<C>> {
    private final static Logger LOG = LoggerFactory.getLogger(Generator.class);

    protected DocumentBuilder documentBuilder;

    {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Couldn't initialize "+getClass(), e);
        }
    }

    public abstract C createGeneratorContext();

    public abstract SourceFileHandler<C, ?> getSourceFileHandler(Document document);

    public void parseAndGenerate(Path sourceDirectory) throws Exception {
        final C context = createGeneratorContext();

        parseAndGenerate(sourceDirectory, context);
    }

    public void parseAndGenerate(Path sourceDirectory, C context) throws Exception {
        // First, we will parse XML files to DOM and find their associated FileHandlers
        addDocumentToProcess(sourceDirectory, context);

        // For each sources files, create a new file handler and init it
        createSourceFileHandlersThenParseAndInit(context);

        // Now, we "prepare", that is to say, every fileHandler will create target files in the generator context using getOrCreateTargetFile method, and add modifications to it using addFileModification method
        prepareSourceFileHandlers(context);

        prepareTargetFiles(context);

        // End finally render the target files
        renderTargetFiles(context);
    }

    public void prepareTargetFiles(C context) throws Exception {
        for (TargetFile targetFile : context.getTargetFiles()) {
            targetFile.prepare(context);
        }
    }

    public void renderTargetFiles(C context) throws Exception {
        for (TargetFile targetFile : context.getTargetFiles()) {
            targetFile.render(context);
        }
    }

    public void prepareSourceFileHandlers(C context) throws Exception {
        for (GeneratorContext<C>.SourceFileAndHandler sourceFileAndHandler : context.iterateOnSourceFileHandlers()) {
            sourceFileAndHandler.getSourceFileHandler().prepare(sourceFileAndHandler.getSourceFile(), context);
        }
    }

    public void createSourceFileHandlersThenParseAndInit(C context) throws Exception {
        for (DocumentToProcess documentToProcess : context.processDocument()) {
            Document document = documentToProcess.getDocument();
            SourceFileHandler sourceFileHandler = getSourceFileHandler(document);
            if (sourceFileHandler != null) {
                parseDocumentToProcessThenInitSourceFileHandler(context, documentToProcess, sourceFileHandler);
            } else {
                LOG.warn("Ignoring {} as we couldn't found a FileHandler for it", documentToProcess.getPath());
            }
        }
    }

    public void parseDocumentToProcessThenInitSourceFileHandler(C context, DocumentToProcess documentToProcess, SourceFileHandler sourceFileHandler) throws Exception {
        SourceFile sourceFile = sourceFileHandler.parse(documentToProcess, context);
        sourceFileHandler.init(sourceFile, context);
    }

    public void addDocumentToProcess(Path sourceDirectory, final C context) throws IOException {
        Files.walkFileTree(sourceDirectory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    Document document = documentBuilder.parse(file.toFile());
                    context.addDocumentToProcess(file, document);
                    /*
                    SourceFileHandler<C, ?> sourceFileHandler = getOrCreateSourceFileHandler(document);
                    if (sourceFileHandler != null) {
                        SourceFile sourceFile = sourceFileHandler.parse(document, context);
                        fileHandlerToSourceFiles.put(sourceFileHandler, sourceFile);
                    } else {
                        LOG.warn("Ignoring {} as we couldn't found a FileHandler for it", file);
                    }
                    */
                } catch (Exception e) {
                    LOG.warn("Ignoring {} as it must not be a parseable source file (Problem: [{}] {})", file, e.getClass().getName(), e.getMessage());
                }
                return super.visitFile(file, attrs);
            }
        });
    }


}
