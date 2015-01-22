package com.iorga.ivif.tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;

public abstract class Generator<C extends GeneratorContext<C>> {
    private final static Logger LOG = LoggerFactory.getLogger(Generator.class);

    public abstract C createGeneratorContext();

    public abstract SourceFileHandler createSourceFileHandler(Path file, C context);

    public void parseAndGenerate(Path sourceDirectory, Path targetDirectory) throws Exception {
        final C context = createGeneratorContext();

        context.setSourcePath(sourceDirectory);
        context.setTargetPath(targetDirectory);

        parseAndGenerate(context);
    }

    public void parseAndGenerate(C context) throws Exception {
        // First, we will parse XML files to DOM and find their associated FileHandlers
        discoverSourceFiles(context);

        // Now we render all prepared target files (that should be all files)
        renderPreparedTargetFiles(context);

        // At last we check that nothing is still waiting for other thing
        checkNoWaiterLeft(context);
    }


    protected void checkNoWaiterLeft(C context) {
        boolean waitersStillExist = false;

        final Collection<EventWaiter<? extends Event<?>, ?>> allEventWaiters = context.getAllEventWaiters();
        for (EventWaiter<? extends Event<?>, ?> waiter : allEventWaiters) {
            LOG.error("{} is still waiting {}:{}", waiter.getWaiterSource(), waiter.getEventClass(), waiter.getEventId());
        }

        if (waitersStillExist) {
            throw new IllegalStateException("Waiters still exists.");
        }
    }

    public void discoverSourceFiles(final C context) throws IOException {
        Files.walkFileTree(context.getSourcePath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    processNewFile(file, context);
                } catch (Exception e) {
                    LOG.error("Error while handling " + file, e);
                }
                return super.visitFile(file, attrs);
            }
        });
    }

    public void processNewFile(Path file, C context) throws Exception {
        SourceFileHandler<C> sourceFileHandler = createSourceFileHandler(file, context);
        if (sourceFileHandler != null) {
            // Parse source file
            sourceFileHandler.parse(context);

            // Then create TargetFiles for prepared sourceFileHandlers
            sourceFileHandler.declareTargets(context);

            // Now iterates on all new targets to prepare them
            prepareTargets(context);

            // Finally, render prepared target files
            // TODO renderPreparedTargetFiles(context); here, but there is a problem : how to handle Targets which will change other already prepared Targets ? Potentially, those one will be already rendered

            // TODO processPendingNewFiles(context);
        } else {
            LOG.warn("Ignoring {} as no SourceFileHandler was created for it", file);
        }
    }

    public void prepareTargets(C context) throws Exception {
        boolean atLeastOneTargetPrepared = true;

        while (atLeastOneTargetPrepared) {
            atLeastOneTargetPrepared = false;
            for (Target<?, C> target : context.iterateOnNewTargetsToPrepareThem()) {
                atLeastOneTargetPrepared = true;
                target.prepare(context);
                context.declarePreparedCalled(target);
            }
        }
    }

    public void renderPreparedTargetFiles(C context) throws Exception {
        for (TargetFile<?, C> targetFile : context.iterateOnPreparedTargetFilesToRenderThem()) {
            targetFile.render(context);
        }
    }

    public void processPendingNewFiles(C context) throws Exception {
        // TODO processPendingNewFiles
        /*
        for (Path newFile : context.iterateOnNewFilesToProcessThem()) {
            processNewFile(newFile, context);
            context.declareFileProcessed(newFile);
        }
        */
    }
}
