package sh.fountain.fountain.runtime.reflection;

import sh.fountain.fountain.api.dependency_injection.Injectable;

import org.reflections.Reflections;

@Injectable(useConstructor = false)
public record ReflectionsProvider(Reflections reflections) {
}
