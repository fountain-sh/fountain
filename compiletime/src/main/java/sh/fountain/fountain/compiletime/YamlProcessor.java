package sh.fountain.fountain.compiletime;

import sh.fountain.fountain.api.command.Command;
import sh.fountain.fountain.api.command.CompositeCommand;
import sh.fountain.fountain.api.plugin.Authors;
import sh.fountain.fountain.api.plugin.Dependencies;
import sh.fountain.fountain.api.plugin.Permissions;
import sh.fountain.fountain.api.plugin.Plugin;
import sh.fountain.fountain.compiletime.yaml_model.PluginYamlModelBuilder;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.StandardLocation;

@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class YamlProcessor extends AbstractProcessor {
    private Filer filer;
    private final PluginYamlModelBuilder builder = new PluginYamlModelBuilder();

    @Override
    public synchronized void init(ProcessingEnvironment environment) {
        filer = environment.getFiler();

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment) {

        environment.getElementsAnnotatedWith(CompositeCommand.class)
                .stream()
                .filter(this::isRootCompositeCommand)
                .map(e -> e.getAnnotation(CompositeCommand.class))
                .forEach(builder::addCommand);

        environment.getElementsAnnotatedWith(Command.class)
                .stream()
                .filter(e -> !isCompositeCommand(e.getEnclosingElement()))
                .map(e -> e.getAnnotation(Command.class))
                .forEach(builder::addCommand);

        final var pluginClasses = environment.getElementsAnnotatedWith(Plugin.class).stream().map(e -> (TypeElement) e)
                .toList();
        if (pluginClasses.size() > 1) {
            throw new IllegalArgumentException(
                    "Multiple classes have been decalered as a plugin entrypoint:  %s".formatted(
                            pluginClasses.stream().map(e -> e.getQualifiedName().toString())));

        }

        pluginClasses.stream().forEach(e -> {
            validatePluginClass(e);

            builder.addPluginMetaData(e.getAnnotation(Plugin.class));

            Optional.ofNullable(e.getAnnotation(Authors.class))
                    .map(Authors::value)
                    .map(Arrays::stream)
                    .ifPresent(as -> as.forEach(builder::addAuthor));

            Optional.ofNullable(e.getAnnotation(Dependencies.class))
                    .map(Dependencies::value)
                    .map(Arrays::stream)
                    .ifPresent(ds -> ds.forEach(builder::addPluginDependency));

            Optional.ofNullable(e.getAnnotation(Permissions.class))
                    .map(Permissions::value)
                    .map(Arrays::stream)
                    .ifPresent(ps -> ps.forEach(builder::addPermission));

            builder.addPluginClass(e.getQualifiedName().toString());
        });

        if (environment.processingOver()) {
            try (final var stream = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "plugin.yml")
                    .openOutputStream()) {

                final var mapper = new YAMLMapper();
                mapper.registerModule(new Jdk8Module());

                mapper.writeValue(stream, builder.build());

            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(CompositeCommand.class.getCanonicalName(), Command.class.getCanonicalName(),
                Plugin.class.getCanonicalName());
    }

    private boolean isRootCompositeCommand(Element element) {
        return isCompositeCommand(element) && !isCompositeCommand(element.getEnclosingElement());

    }

    private boolean isCompositeCommand(Element element) {
        return element.getKind().equals(ElementKind.CLASS) && element.getAnnotation(CompositeCommand.class) != null;
    }

    private void validatePluginClass(Element element) {
        if (element instanceof TypeElement type) {
            final var name = type.getQualifiedName().toString();
            if (!type.getKind().equals(ElementKind.CLASS)) {
                throw new IllegalArgumentException("Plugin entrypoint must be a class");
            }
            if (type.getModifiers().contains(Modifier.ABSTRACT)) {
                throw new IllegalArgumentException("Plugin entrypoint %s may not be abstract".formatted(name));
            }
            if (!type.getEnclosingElement().getKind().equals(ElementKind.PACKAGE)) {
                throw new IllegalArgumentException("Plugin entrypoint %s must be a top level class".formatted(name));
            }
            if (ElementFilter.constructorsIn(type.getEnclosedElements()).stream().map(e -> (ExecutableElement) e)
                    .anyMatch(c -> c.getParameters().size() > 0)) {
                throw new IllegalArgumentException("Plugin entrypoint %s must have a 0 argument constructor");
            }
        } else {
            // as the plugin annotation can only be applied to classes this should never happen
            throw new IllegalStateException();
        }
    }

}
