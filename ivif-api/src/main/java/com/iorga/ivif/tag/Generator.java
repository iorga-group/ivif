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

    public void parseAndGenerate(Path sourceDirectory, Path targetDirectory) throws Exception {
        final C context = createGeneratorContext();

        context.setSourcePath(sourceDirectory);
        context.setTargetPath(targetDirectory);

        parseAndGenerate(context);
    }

    public void parseAndGenerate(C context) throws Exception {
        // First, we will parse XML files to DOM and find their associated FileHandlers
        addDocumentToProcess(context);

        // For each sources files, create a new file handler and init it
        createSourceFileHandlersThenParseAndInit(context);

        // Now, we "prepareTargetFiles", that is to say, every fileHandler will create target files in the generator context using getOrCreateTargetFile method, and add modifications to it using addFileModification method
        makeSourceFileHandlersPrepareTargetFiles(context);

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

    public void makeSourceFileHandlersPrepareTargetFiles(C context) throws Exception {
        for (SourceFileHandler sourceFileHandler : context.getSourceFileHandlers()) {
            sourceFileHandler.prepareTargetFiles(context.getSourceFileForHandler(sourceFileHandler), context);
        }
    }

    public void createSourceFileHandlersThenParseAndInit(C context) throws Exception {
        for (DocumentToProcess documentToProcess : context.processDocument()) {
            Document document = documentToProcess.getDocument();
            SourceFileHandler sourceFileHandler = getSourceFileHandler(document);
            if (sourceFileHandler != null) {
                parseDocumentToProcess(context, documentToProcess, sourceFileHandler);
            } else {
                LOG.warn("Ignoring {} as we couldn't found a FileHandler for it", documentToProcess.getPath());
            }
        }
        for (SourceFileHandler sourceFileHandler : context.getSourceFileHandlers()) {
            sourceFileHandler.init(context.getSourceFileForHandler(sourceFileHandler), context);
        }
    }

    public void parseDocumentToProcess(C context, DocumentToProcess documentToProcess, SourceFileHandler sourceFileHandler) throws Exception {
        SourceFile sourceFile = sourceFileHandler.parse(documentToProcess, context);
        context.registerSourceFile(sourceFile, sourceFileHandler);
    }

    public void addDocumentToProcess(final C context) throws IOException {
        Files.walkFileTree(context.getSourcePath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    Document document = documentBuilder.parse(file.toFile());
                    context.addDocumentToProcess(file, document);
                } catch (Exception e) {
                    LOG.warn("Ignoring {} as it must not be a parseable source file (Problem: [{}] {})", file, e.getClass().getName(), e.getMessage());
                }
                return super.visitFile(file, attrs);
            }
        });
    }


}
