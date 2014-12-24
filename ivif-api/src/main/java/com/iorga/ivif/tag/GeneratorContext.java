package com.iorga.ivif.tag;

import com.google.common.collect.*;
import org.w3c.dom.Document;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.util.Map.*;

public abstract class GeneratorContext<C extends GeneratorContext<C>> {

    protected String basePackage = ""; // TODO parse from config
    protected Path basePath = Paths.get("");
    protected Deque<DocumentToProcess> documentsToProcess = new LinkedList<>();

    protected Set<Dependency> rootDependencies = Sets.newHashSet();
    protected Multimap<TargetFile<C, ?>, Dependency> unresolvedDependencies = LinkedListMultimap.create();
    protected Map<TargetFile<C, ?>, Dependency> dependenciesByCreatedTargetFile = Maps.newHashMap();
    protected Map<SourceFileHandler<C, ?>, Dependency> dependenciesBySourceFileHandler = Maps.newHashMap();

    protected Map<SourceFileHandler<C, ?>, SourceFile> sourceFilesByHandler = Maps.newHashMap();

    protected Map<Class<?>, Map<Object, TargetFile>> targetFiles = Maps.newHashMap();

    protected class Dependency {
        protected SourceFileHandler<C, ?> sourceFileHandler;
        protected Set<TargetFile<C, ?>> requiredTargetFiles = Sets.newHashSet();
        protected Set<TargetFile<C, ?>> createdTargetFiles = Sets.newHashSet();
        protected Set<Dependency> resolvedParentDependencies = Sets.newHashSet();
        protected Set<Dependency> resolvedChildrenDependencies = Sets.newHashSet();
    }

    public class SourceFileAndHandler {
        protected final SourceFile sourceFile;
        protected final SourceFileHandler sourceFileHandler;

        public SourceFileAndHandler(SourceFile sourceFile, SourceFileHandler sourceFileHandler) {
            this.sourceFile = sourceFile;
            this.sourceFileHandler = sourceFileHandler;
        }

        public SourceFile getSourceFile() {
            return sourceFile;
        }

        public SourceFileHandler getSourceFileHandler() {
            return sourceFileHandler;
        }
    }

    public <SF extends SourceFile> void declareCreatedSourceFile(SF sourceFile, SourceFileHandler<C, SF> sourceFileCreator) {
        SourceFile previousSourceFile = sourceFilesByHandler.get(sourceFileCreator);
        if (previousSourceFile != null) {
            throw new IllegalStateException("A SourceFileHandler can create only one SourceFile per GeneratorContext. Can't create "+sourceFile.getPath()+" as "+previousSourceFile.getPath()+" is already created for "+sourceFileCreator);
        } else {
            sourceFilesByHandler.put(sourceFileCreator, sourceFile);
        }
    }

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

    public void addDocumentToProcess(Path path, Document document) {
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

    public <T extends TargetFile<C, ?>> void declareRequiredTargetFile(SourceFileHandler<C, ?> sourceFileHandler, T targetFile) {
        Dependency dependency = getOrCreateDependency(sourceFileHandler);
        dependency.requiredTargetFiles.add(targetFile);
        // Try to resolve dependency searching its parent dependencies
        Dependency parentDependency = dependenciesByCreatedTargetFile.get(targetFile);
        if (parentDependency != null) {
            markAsResolved(dependency, parentDependency);
        } else {
            unresolvedDependencies.put(targetFile, dependency);
        }
    }

    private void markAsResolved(Dependency dependency, Dependency parent) {
        // link parent & child
        dependency.resolvedParentDependencies.add(parent);
        parent.resolvedChildrenDependencies.add(dependency);
        // this is no more a root dependency
        rootDependencies.remove(dependency);
    }

    public <T extends TargetFile<C, ?>> void declareCreatedTargetFile(SourceFileHandler<C, ?> sourceFileHandler, T targetFile) {
        Dependency dependency = getOrCreateDependency(sourceFileHandler);
        dependency.createdTargetFiles.add(targetFile);
        dependenciesByCreatedTargetFile.put(targetFile, dependency);
        // Try to resolve dependency searching its children dependencies
        Collection<Dependency> children = unresolvedDependencies.get(targetFile);
        if (children != null) {
            for (Dependency child : children) {
                // link parent & child
                markAsResolved(child, dependency);
            }
        }
    }

    private Dependency getOrCreateDependency(SourceFileHandler<C, ?> sourceFileHandler) {
        Dependency dependency = dependenciesBySourceFileHandler.get(sourceFileHandler);
        if (dependency == null) {
            // create dependency
            dependency = new Dependency();
            dependency.sourceFileHandler = sourceFileHandler;
            rootDependencies.add(dependency);
            dependenciesBySourceFileHandler.put(sourceFileHandler, dependency);
        }
        return dependency;
    }

    /**
     * Iterate on SourceFileHandler and its SourceFile taking into account
     * the dependency tree between handlers
     */
    public Iterable<SourceFileAndHandler> iterateOnSourceFileHandlers() {
        return new Iterable<SourceFileAndHandler>() {
            @Override
            public Iterator<SourceFileAndHandler> iterator() {
                // Compute resolved Dependency list
                Set<Dependency> alreadyIteratedOnDependency = Sets.newHashSet();
                List<SourceFileAndHandler> flattenedDependencies = Lists.newLinkedList();
                visit(rootDependencies, alreadyIteratedOnDependency, flattenedDependencies);
                return flattenedDependencies.iterator();
            }

            private void visit(Collection<Dependency> dependencies, Set<Dependency> alreadyIteratedOnDependency, List<SourceFileAndHandler> flattenedDependencies) {
                for (Dependency dependency : dependencies) {
                    if (!alreadyIteratedOnDependency.contains(dependency)) {
                        flattenedDependencies.add(new SourceFileAndHandler(sourceFilesByHandler.get(dependency.sourceFileHandler), dependency.sourceFileHandler));
                        alreadyIteratedOnDependency.add(dependency);
                        // now handle children
                        visit(dependency.resolvedChildrenDependencies, alreadyIteratedOnDependency, flattenedDependencies);
                    }
                }
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

    /// Getters & Setters

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public Path getBasePath() {
        return basePath;
    }

    public void setBasePath(Path basePath) {
        this.basePath = basePath;
    }
}
