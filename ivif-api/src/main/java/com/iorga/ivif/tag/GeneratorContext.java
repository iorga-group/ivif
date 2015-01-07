package com.iorga.ivif.tag;

import com.google.common.collect.*;
import org.w3c.dom.Document;

import javax.xml.stream.XMLStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public abstract class GeneratorContext<C extends GeneratorContext<C>> {

    protected String basePackage = ""; // TODO parse from config
    protected Deque<DocumentToProcess> documentsToProcess = new LinkedList<>();

    protected Map<Class<?>, Map<Object, TargetFile>> targetFiles = Maps.newHashMap();
    private Path sourcePath = Paths.get("");
    private Path targetPath;
    private List<SourceTagHandler<C>> sourceTagHandlers = new ArrayList<>();


    public <I, T extends TargetFile<C, I>> T getOrCreateTargetFile(Class<T> targetFileType, I id) throws Exception {
        Map<Object, TargetFile> targetFilesForThatType = targetFiles.get(targetFileType);
        if (targetFilesForThatType == null) {
            targetFilesForThatType = Maps.newHashMap();
            targetFiles.put(targetFileType, targetFilesForThatType);
        }
        T targetFile = (T) targetFilesForThatType.get(id);
        if (targetFile == null) {
            // create target file
            targetFile = createTargetFile(targetFileType, id, this);
            targetFilesForThatType.put(id, targetFile);
        }
        return targetFile;
    }

    protected <T extends TargetFile<C, I>, I> T createTargetFile(Class<T> targetFileType, I id, GeneratorContext<C> generatorContext) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<T> constructor = targetFileType.getConstructor(id.getClass(), generatorContext.getClass());
        return constructor.newInstance(id, generatorContext);
    }

    public void addDocumentToProcess(Path path, XMLStreamReader document) {
        addDocumentToProcess(new DocumentToProcess(path, document));
    }

    public void addDocumentToProcess(DocumentToProcess documentToProcess) {
        documentsToProcess.add(documentToProcess);
    }

    public Iterable<DocumentToProcess> processDocument() {
        return new Iterable<DocumentToProcess>() {
            @Override
            public Iterator<DocumentToProcess> iterator() {
                return new Iterator<DocumentToProcess>() {
                    @Override
                    public boolean hasNext() {
                        return !documentsToProcess.isEmpty();
                    }

                    @Override
                    public DocumentToProcess next() {
                        return documentsToProcess.remove();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public Collection<TargetFile<C, ?>> getTargetFiles() {
        List<TargetFile<C, ?>> finalList = new ArrayList<>();
        for (Map<Object, TargetFile> targetFileMap : targetFiles.values()) {
            for (TargetFile targetFile : targetFileMap.values()) {
                finalList.add(targetFile);
            }
        }
        return finalList;
    }

    public void registerSourceTagHandler(SourceTagHandler<C> sourceTagHandler, DocumentToProcess documentToProcess) {
        sourceTagHandlers.add(sourceTagHandler);
    }

    /// Getters & Setters

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setSourcePath(Path sourcePath) {
        this.sourcePath = sourcePath;
    }

    public Path getSourcePath() {
        return sourcePath;
    }

    public void setTargetPath(Path targetPath) {
        this.targetPath = targetPath;
    }

    public Path getTargetPath() {
        return targetPath;
    }

    public List<SourceTagHandler<C>> getSourceTagHandlers() {
        return sourceTagHandlers;
    }
}
