package com.iorga.ivif.tag;

import com.google.common.collect.*;

import javax.xml.stream.XMLStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public abstract class GeneratorContext<C extends GeneratorContext<C>> {

    protected String basePackage = ""; // TODO parse from config
    protected Deque<DocumentToProcess> documentsToProcess = new LinkedList<>();

    //protected Map<Class<?>, Map<Object, TargetFile>> targetFiles = Maps.newHashMap();
    private Path sourcePath = Paths.get("");
    private Path targetPath;
    private List<SourceTagHandler<C>> sourceTagHandlers = new ArrayList<>();

    protected Map<Class<? extends TargetFile<C, ?>>, Map<Object, TargetFileContext>> targetFileContexts = Maps.newHashMap();

    protected class TargetFileContext<I, T extends TargetFile<C, I>> {
        protected boolean targetFilePrepared = false;
        protected T targetFile;
        protected Deque<TargetFileWaiter<T, I, C>> waiters = new LinkedList<>();
    }

    protected <I, T extends TargetFile<C, I>> TargetFileContext<I, T> getOrCreateTargetFileContext(Class<T> targetFileClass, I id) {
        Map<Object, TargetFileContext> targetFileContextForThatClass = targetFileContexts.get(targetFileClass);
        if (targetFileContextForThatClass == null) {
            targetFileContextForThatClass = Maps.newHashMap();
            targetFileContexts.put(targetFileClass, targetFileContextForThatClass);
        }
        TargetFileContext<I, T> targetFileContext = targetFileContextForThatClass.get(id);
        if (targetFileContext == null) {
            targetFileContext = new TargetFileContext<>();
            targetFileContextForThatClass.put(id, targetFileContext);
        }
        return targetFileContext;
    }

    public <I, T extends TargetFile<C, I>> void waitForTargetFileToBePrepared(TargetFileWaiter<T, I, C> waiter) throws Exception {
        TargetFileContext<I, T> targetFileContext = getOrCreateTargetFileContext(waiter.getTargetClass(), waiter.getTargetId());
        if (!targetFileContext.targetFilePrepared) {
            // this targetFile is not yet prepared, must register the waiter
            targetFileContext.waiters.add(waiter);
        } else {
            // this targetFile is prepared, we just have to call the waiter on it
            waiter.onPrepared(targetFileContext.targetFile);
        }
    }

    public <I, T extends TargetFile<C, I>> void declareTargetFilePrepared(T targetFile) throws Exception {
        TargetFileContext<I, T> targetFileContext = getOrCreateTargetFileContext((Class<T>)(Class)targetFile.getClass(), targetFile.getId());
        targetFileContext.targetFile = targetFile;
        targetFileContext.targetFilePrepared = true;
        // now call each waiter
        Deque<TargetFileWaiter<T, I, C>> waiters = targetFileContext.waiters;
        while (!waiters.isEmpty()) {
            TargetFileWaiter<T, I, C> waiter = waiters.remove();
            waiter.onPrepared(targetFile);
        }
    }

    public <I, T extends TargetFile<C, I>> T getOrCreateTargetFile(Class<T> targetFileType, I id) throws Exception {
        TargetFileContext<I, T> targetFileContext = getOrCreateTargetFileContext(targetFileType, id);
        T targetFile = targetFileContext.targetFile;
        if (targetFile == null) {
            // create target file
            targetFile = createTargetFile(targetFileType, id, this);
            targetFileContext.targetFile = targetFile;
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
        for (Map<Object, TargetFileContext> targetFileContextMap : targetFileContexts.values()) {
            for (TargetFileContext targetFileContext : targetFileContextMap.values()) {
                if (targetFileContext.targetFile != null) { // TODO why it would be null here ?
                    finalList.add(targetFileContext.targetFile);
                }
            }
        }
        return finalList;
    }

    public Collection<TargetFileWaiter<? extends TargetFile<C, ?>, ?, C>> getTargetFileWaiters() {
        List<TargetFileWaiter<? extends TargetFile<C, ?>, ?, C>> finalList = new ArrayList<>();
        for (Map<Object, TargetFileContext> targetFileContextMap : targetFileContexts.values()) {
            for (TargetFileContext targetFileContext : targetFileContextMap.values()) {
                finalList.addAll(targetFileContext.waiters);
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
