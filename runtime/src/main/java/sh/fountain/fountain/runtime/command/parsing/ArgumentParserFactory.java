package sh.fountain.fountain.runtime.command.parsing;

import sh.fountain.fountain.api.command.SenderBinding;
import sh.fountain.fountain.runtime.command.parsing.builtin_parsers.CollectionParser;
import sh.fountain.fountain.runtime.command.parsing.builtin_parsers.EnumParser;
import sh.fountain.fountain.runtime.command.parsing.builtin_parsers.OptionalParser;
import sh.fountain.fountain.runtime.dependency_injection.DependencyInjector;
import sh.fountain.fountain.runtime.dependency_injection.InjectionToken;

import com.google.common.reflect.TypeParameter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public class ArgumentParserFactory {
    private final DependencyInjector injector;

    public ArgumentParserFactory(DependencyInjector injector) {
        this.injector = injector;
    }

    public ArgumentParser<?> fromParameter(Parameter param) {
        final var type = Optional.ofNullable(param.getParameterizedType()).orElse(param.getType());
        return deriveParser(com.google.common.reflect.TypeToken.of(type));
    }

    public List<? extends ArgumentParser<?>> fromMethod(Method method) {
        return Arrays.stream(method.getParameters())
                .filter(p -> p.getAnnotation(SenderBinding.class) == null)
                .map(this::fromParameter)
                .toList();
    }

    @SuppressWarnings({"unchecked", "rawtypes"}) //We do reflection, so suppress those warnings
    private ArgumentParser<?> deriveParser(com.google.common.reflect.TypeToken<?> type) {
        final var normalizedType = type.wrap();

        final var innerType = normalizedType.getType() instanceof ParameterizedType p
                ? com.google.common.reflect.TypeToken.of(p.getActualTypeArguments()[0])
                : null;

        if (normalizedType.getRawType().equals(Optional.class)) {
            assert innerType != null; //As normalizedType has one type argument
            return new OptionalParser<>(deriveParser(innerType));

        } else if (normalizedType.getRawType().equals(List.class)) {
            assert innerType != null; //As normalizedType has one type argument
            return new CollectionParser<>(ArrayList::new, deriveParser(innerType));

        } else if (normalizedType.getRawType().equals(Set.class)) {
            assert innerType != null; //As normalizedType has one type argument
            return new CollectionParser<>(HashSet::new, deriveParser(innerType));

        } else if (normalizedType.getRawType().equals(Deque.class)) {
            assert innerType != null; //As normalizedType has one type argument
            return new CollectionParser<>(ArrayDeque::new, deriveParser(innerType));

        } else if (normalizedType.getRawType().equals(Queue.class)) {
            assert innerType != null; //As normalizedType has one type argument
            return new CollectionParser<>(LinkedList::new, deriveParser(innerType));

        } else if (normalizedType.getType() instanceof Class<?> c && c.isEnum()) {
            return new EnumParser(c);

        } else {
            final var tokenImpl = optionalToken(parserToken(normalizedType));
            final var token = InjectionToken.from(tokenImpl);
            return injector
                    .valueFor(token)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Illegal command parameter type. No parser could be found for "
                                    + normalizedType));
        }

    }

    private <T> com.google.common.reflect.TypeToken<ArgumentParser<T>> parserToken(
            com.google.common.reflect.TypeToken<T> parsedType) {
        return new com.google.common.reflect.TypeToken<ArgumentParser<T>>() {}
                .where(new TypeParameter<T>() {}, parsedType);
    }

    private <T> com.google.common.reflect.TypeToken<Optional<T>> optionalToken(
            com.google.common.reflect.TypeToken<T> wrapped) {
        return new com.google.common.reflect.TypeToken<Optional<T>>() {}
                .where(new TypeParameter<>() {}, wrapped);
    }
}
