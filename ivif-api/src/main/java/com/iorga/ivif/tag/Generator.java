package com.iorga.ivif.tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public abstract class Generator<C extends GeneratorContext<C>> {
    private final static Logger LOG = LoggerFactory.getLogger(Generator.class);

    protected XMLInputFactory xmlInputFactory;

    {
        xmlInputFactory = XMLInputFactory.newInstance();
    }

    public abstract C createGeneratorContext();

    protected abstract SourceTagHandler<C> createSourceTagHandler(QName name, C context) throws JAXBException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException;

    public void parseAndGenerate(Path sourceDirectory, Path targetDirectory) throws Exception {
        final C context = createGeneratorContext();

        context.setSourcePath(sourceDirectory);
        context.setTargetPath(targetDirectory);

        parseAndGenerate(context);
    }

    public void parseAndGenerate(C context) throws Exception {
        // First, we will parse XML files to DOM and find their associated FileHandlers
        addDocumentsToProcess(context);

        // For each sources files, create a new file handler and init it
        createSourceTagHandlersThenParseAndInit(context);

        // Now, we "prepareTargetFiles", that is to say, every fileHandler will create target files in the generator context using getOrCreateTargetFile method, and add modifications to it using addFileModification method
        makeSourceTagHandlersPrepareTargetFiles(context);

        prepareTargetFiles(context);

        //TODO FIXME check if there are no target files waiting for others to be prepared

        // End finally render the target files
        renderTargetFiles(context);
    }

    public void prepareTargetFiles(C context) throws Exception {
        for (TargetFile targetFile : context.getTargetFiles()) {
            targetFile.prepare(context);
            context.declareTargetFilePrepared(targetFile);
        }
    }

    public void renderTargetFiles(C context) {
        for (TargetFile targetFile : context.getTargetFiles()) {
            try {
                targetFile.render(context);
            } catch (Exception e) {
                LOG.error("Problem while rendering " + targetFile.getPath(context), e);
            }
        }
    }

    public void makeSourceTagHandlersPrepareTargetFiles(C context) throws Exception {
        for (SourceTagHandler<C> sourceTagHandler : context.getSourceTagHandlers()) {
            sourceTagHandler.prepareTargetFiles(context);
        }
    }

    public void createSourceTagHandlersThenParseAndInit(C context) throws Exception {
        for (DocumentToProcess documentToProcess : context.processDocument()) {
            XMLStreamReader reader = documentToProcess.getXmlStreamReader();
            // Inspect file until a tag handler is found
            int eventType;
            int nbSourceTagHandlers = 0;
            while (reader.hasNext()) {
                eventType = reader.next();
                if (eventType == XMLStreamReader.START_ELEMENT) {
                    // check if we have a tag handler for this
                    SourceTagHandler<C> sourceTagHandler = createSourceTagHandler(reader.getName(), context);
                    if (sourceTagHandler != null) {
                        boolean parsed = sourceTagHandler.parse(documentToProcess, context);
                        if (parsed) {
                            nbSourceTagHandlers++;
                            context.registerSourceTagHandler(sourceTagHandler, documentToProcess);
                        }
                    }
                }
            }
            if (nbSourceTagHandlers == 0) {
                LOG.warn("Found no tag handlers for file {}", documentToProcess.getPath());
            }
        }
        for (SourceTagHandler sourceTagHandler : context.getSourceTagHandlers()) {
            sourceTagHandler.init(context);
        }
    }

    public void addDocumentsToProcess(final C context) throws IOException {
        Files.walkFileTree(context.getSourcePath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    //TODO change to xml stream here & cut SourceFileHandlers to SourceTagHandlers and make JAXB the default impl
                    XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(file.toString(), new FileInputStream(file.toFile()));
                    context.addDocumentToProcess(file, xmlStreamReader);
                } catch (Exception e) {
                    LOG.warn("Ignoring {} as it must not be a parseable source file (Problem: [{}] {})", file, e.getClass().getName(), e.getMessage());
                }
                return super.visitFile(file, attrs);
            }
        });
    }


}
