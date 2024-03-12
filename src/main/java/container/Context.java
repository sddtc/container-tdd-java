package container;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Context {

  private final Map<Class<?>, Provider<?>> providers = new HashMap<>();

  public <C> void bind(Class<C> type, C instance) {
    providers.put(type, () -> instance);
  }

  public <C, I extends C> void bind(Class<C> type, Class<I> implementation) {
    Constructor<I> injectConstructor = getInjectConstructor(implementation);

    providers.put(type, () -> {
      try {
        Object[] dependencies = Arrays.stream(injectConstructor.getParameters())
            .map(p -> get(p.getType()).orElseThrow(DependencyNotFoundException::new))
            .toArray(Object[]::new);
        return injectConstructor.newInstance(dependencies);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private static <C, I extends C> Constructor<I> getInjectConstructor(Class<I> implementation) {
    List<Constructor<?>> injectConstructors = Arrays.stream(implementation.getConstructors())
        .filter(c -> c.isAnnotationPresent(Inject.class)).toList();
    if (injectConstructors.size() > 1) {
      throw new IllegalComponentException();
    }
    return (Constructor<I>) injectConstructors
        .stream()
        .findFirst()
        .orElseGet(() -> {
          try {
            return implementation.getConstructor();
          } catch (NoSuchMethodException e) {
            throw new IllegalComponentException();
          }
        });
  }

  public <C> Optional<C> get(Class<C> type) {
    return Optional.ofNullable(providers.get(type)).map(p -> (C) p.get());
  }
}
