package com.iorga.ivif.maven;

import com.iorga.ivif.tag.Generator;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mojo(
        name = "process",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        threadSafe = true
)
public class ProcessMojo extends AbstractMojo {

    @Parameter(name = "generatorClass", required = true)
    protected String generatorClass;

    /*
    @Parameter(defaultValue = "${project}")
    private MavenProject project;
*/
    @Component
    protected MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            // create new class loader thanks to http://stackoverflow.com/a/16263482/535203
            Set<URL> urls = new HashSet<>();
            List<String> elements = project.getCompileClasspathElements();
            for (String element : elements) {
                urls.add(new File(element).toURI().toURL());
            }

            ClassLoader contextClassLoader = URLClassLoader.newInstance(
                    urls.toArray(new URL[0]),
                    Thread.currentThread().getContextClassLoader());

            Class<? extends Generator> generatorClass = (Class<? extends Generator>) Class.forName(this.generatorClass, true, contextClassLoader);
            Generator generator = generatorClass.newInstance();
            Build build = project.getBuild();
            Path sourcePath = Paths.get(build.getSourceDirectory()).getParent().resolve("ivif");
            Path targetPath = Paths.get(build.getOutputDirectory()).getParent();
            getLog().info("Reading ivif sources from '" + sourcePath+"' and generating into '"+targetPath+"'");
            generator.parseAndGenerate(sourcePath, targetPath);
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Couldn't find generator class '"+this.generatorClass+"'", e);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new MojoExecutionException("Problem while creating generator '"+this.generatorClass+"'", e);
        } catch (MalformedURLException | DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Problem while preparing the generator ClassLoader", e);
        } catch (Exception e) {
            throw new MojoFailureException("Problem while generation", e);
        }
    }
}
