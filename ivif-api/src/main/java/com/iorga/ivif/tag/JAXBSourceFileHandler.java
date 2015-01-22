package com.iorga.ivif.tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JAXBSourceFileHandler<C extends GeneratorContext<C>> implements SourceFileHandler<C> {
    private final static Logger LOG = LoggerFactory.getLogger(JAXBSourceFileHandler.class);

    private final JAXBSourceFileHandlerFactory<C> jaxbSourceFileHandlerFactory;

    protected final Path file;
    protected List<SourceTagHandler<C>> sourceTagHandlers;

    public JAXBSourceFileHandler(Path file, JAXBSourceFileHandlerFactory<C> jaxbSourceFileHandlerFactory) {
        this.file = file;
        this.jaxbSourceFileHandlerFactory = jaxbSourceFileHandlerFactory;
    }

    @Override
    public void parse(C context) {
        try {
            sourceTagHandlers = new ArrayList<>();
            //TODO change to xml stream here & cut SourceFileHandlers to SourceTagHandlers and make JAXB the default impl
            XMLStreamReader reader = jaxbSourceFileHandlerFactory.xmlInputFactory.createXMLStreamReader(file.toString(), new FileInputStream(file.toFile()));
            DocumentToProcess documentToProcess = new DocumentToProcess(file, reader);
            // Inspect file until a tag handler is found
            int eventType;
            int nbSourceTagHandlers = 0;
            while (reader.hasNext()) {
                eventType = reader.next();
                if (eventType == XMLStreamReader.START_ELEMENT) {
                    // check if we have a tag handler for this
                    SourceTagHandler<C> sourceTagHandler = jaxbSourceFileHandlerFactory.createSourceTagHandler(reader.getName(), context);
                    if (sourceTagHandler != null) {
                        boolean parsed = sourceTagHandler.parse(documentToProcess, context);
                        if (parsed) {
                            nbSourceTagHandlers++;
                            sourceTagHandlers.add(sourceTagHandler);
                        }
                    }
                }
            }
            if (nbSourceTagHandlers == 0) {
                LOG.warn("Found no tag handlers for file {}", documentToProcess.getPath());
            }
        } catch (Exception e) {
            LOG.warn("Ignoring {} as it must not be a parseable source file (Problem: [{}] {})", file, e.getClass().getName(), e.getMessage());
        }
    }

    @Override
    public void declareTargets(C context) throws Exception {
        for (SourceTagHandler<C> sourceTagHandler : sourceTagHandlers) {
            sourceTagHandler.declareTargets(context);
        }
    }
}
