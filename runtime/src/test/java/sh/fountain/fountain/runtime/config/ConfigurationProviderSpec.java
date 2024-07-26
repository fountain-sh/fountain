package sh.fountain.fountain.runtime.config;

import sh.fountain.fountain.api.config.Config;
import sh.fountain.fountain.api.config.ConfigurationDeserializationException;
import sh.fountain.fountain.api.config.ConfigurationProvider;

import org.bukkit.util.Vector;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConfigurationProviderSpec {
    @Nested
    public class AConfigurationProvider {
        final FileResolver fileResolver = mock(FileResolver.class);
        final ConfigurationProvider provider = new BukkitConfigurationProvider(fileResolver);

        public record UnannotatedModel(String value) {

        }

        @Config(path = "config.yml")
        public record AnnotatedConfigModel(String myString, int myInt, boolean myBoolean, double myDouble) {

        }

        @Test
        public void requiresAnnotatedModels() throws Exception {
            when(fileResolver.resolve(any()))
                    .thenReturn(new File(
                            getClass().getClassLoader().getResource("config.yml").toURI()));

            assertThrowsExactly(ConfigurationModelException.class, () -> provider.load(UnannotatedModel.class));

            assertEquals(new AnnotatedConfigModel("some string", 42, true, 99.9),
                    provider.load(AnnotatedConfigModel.class));
        }

        @Test
        public void rejectsFilesWithMissingNonOptionalProperties() throws Exception {
            when(fileResolver.resolve(any()))
                    .thenReturn(new File(getClass().getClassLoader()
                            .getResource("configMissingKey.yml").toURI()));

            assertThrowsExactly(ConfigurationDeserializationException.class,
                    () -> provider.load(AnnotatedConfigModel.class),
                    "Non optional key myBoolean of type java.lang.boolean is missing");
        }

        @Config(path = "config.yml")
        public record OptionalConfigModel(Optional<String> myString) {
        }

        @Test
        public void initializesMissingOptionalPropertiesToAnEmptyOptional() throws Exception {
            when(fileResolver.resolve(any()))
                    .thenReturn(new File(getClass().getClassLoader()
                            .getResource("emptyOptionalConfig.yml").toURI()));

            assertEquals(new OptionalConfigModel(Optional.empty()),
                    provider.load(OptionalConfigModel.class));
        }

        @Test
        public void initializesPresentOptionalPropertiesToANonEmptyOptional() throws Exception {
            when(fileResolver.resolve(any()))
                    .thenReturn(new File(getClass().getClassLoader()
                            .getResource("optionalConfig.yml").toURI()));

            assertEquals(new OptionalConfigModel(Optional.of("some value")),
                    provider.load(OptionalConfigModel.class));
        }

        @Config(path = "nestedConfig.yml")
        public record NestedConfigModel(NestedConfigModel.Nested model) {
            public record Nested(int value) {
            }
        }

        @Test
        public void loadsNestedModels() throws Exception {
            when(fileResolver.resolve(any()))
                    .thenReturn(new File(getClass().getClassLoader().getResource("nestedConfig.yml")
                            .toURI()));

            assertEquals(new NestedConfigModel(new NestedConfigModel.Nested(-20)),
                    provider.load(NestedConfigModel.class));
        }

        @Config(path = "listConfig.yml")
        public record ListConfigModel(List<Integer> myInts) {
        }

        @Test
        public void loadsPrimitiveLists() throws Exception {
            when(fileResolver.resolve(any()))
                    .thenReturn(new File(getClass().getClassLoader().getResource("listConfig.yml")
                            .toURI()));

            assertEquals(new ListConfigModel(List.of(0, 20, -69)), provider.load(ListConfigModel.class));
        }

        @Test
        public void loadsAMissingListPropertyAsTheEmptyList() throws Exception {
            when(fileResolver.resolve(any()))
                    .thenReturn(new File(getClass().getClassLoader().getResource("emptyConfig.yml")
                            .toURI()));

            assertEquals(new ListConfigModel(List.of()), provider.load(ListConfigModel.class));
        }

        @Config(path = "mapConfig.yml")
        public record MapConfigModel(Map<String, NestedConfigModel> map) {
        }

        @Config(path = "mapConfig.yml")
        public record IntKeyMapConfigModel(Map<Integer, String> map) {
        }

        @Test
        public void loadsMapsWithStringKeys() throws Exception {
            when(fileResolver.resolve(any()))
                    .thenReturn(new File(getClass().getClassLoader().getResource("mapConfig.yml")
                            .toURI()));

            assertEquals(new MapConfigModel(
                    Map.of("myKey", new NestedConfigModel(new NestedConfigModel.Nested(42)))),
                    provider.load(MapConfigModel.class));

            assertThrowsExactly(ConfigurationModelException.class,
                    () -> provider.load(IntKeyMapConfigModel.class));
        }

        @Test
        public void loadsAMissingMapPropertyAsTheEmptyMap() throws Exception {
            when(fileResolver.resolve(any()))
                    .thenReturn(new File(getClass().getClassLoader().getResource("emptyConfig.yml")
                            .toURI()));

            assertEquals(new MapConfigModel(Map.of()), provider.load(MapConfigModel.class));
        }

        @Test
        public void rejectsFilesWithDifferingPropertyTypes() throws Exception {
            when(fileResolver.resolve(any())).thenReturn(
                    new File(getClass().getClassLoader().getResource("badPrimitiveTypeConfig.yml")
                            .toURI()));

            assertThrows(ConfigurationDeserializationException.class, () -> provider.load(AnnotatedConfigModel.class));

            when(fileResolver.resolve(any()))
                    .thenReturn(new File(getClass().getClassLoader()
                            .getResource("badObjectTypeConfig.yml").toURI()));

            assertThrows(ConfigurationDeserializationException.class, () -> provider.load(NestedConfigModel.class));

            when(fileResolver.resolve(any()))
                    .thenReturn(new File(getClass().getClassLoader()
                            .getResource("badListTypeConfig.yml").toURI()));

            assertThrows(ConfigurationDeserializationException.class, () -> provider.load(ListConfigModel.class));

            when(fileResolver.resolve(any()))
                    .thenReturn(new File(getClass().getClassLoader()
                            .getResource("badMapTypeConfig.yml").toURI()));

            assertThrows(ConfigurationDeserializationException.class, () -> provider.load(MapConfigModel.class));
        }

        @Config(path = "objectListConfig.yml")
        public record ObjectListConfigModel(List<NestedConfigModel> models) {
        }

        @Test
        public void loadsListsOfObjects() throws Exception {
            when(fileResolver.resolve(any())).thenReturn(
                    new File(getClass().getClassLoader().getResource("objectListConfig.yml").toURI()));

            final var expected = new ObjectListConfigModel(
                    List.of(
                            new NestedConfigModel(new NestedConfigModel.Nested(10)),
                            new NestedConfigModel(new NestedConfigModel.Nested(20))));

            assertEquals(expected, provider.load(ObjectListConfigModel.class));
        }

        @Config(path = "configSerializable.yml")
        public record BukkitSerializableConfigModel(List<Vector> vectors) {
        }

        @Test
        public void loadsConfigSerializableObjects() throws Exception {
            when(fileResolver.resolve(any())).thenReturn(
                    new File(getClass().getClassLoader().getResource("vectorsConfig.yml").toURI()));

            final var expected = new BukkitSerializableConfigModel(
                    List.of(
                            new Vector(0, 0, 0),
                            new Vector(1, 2, 3)));

            assertEquals(expected, provider.load(BukkitSerializableConfigModel.class));
        }

    }

}
