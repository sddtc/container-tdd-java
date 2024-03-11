package container;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Context {

  private final Map<Class<?>, Provider<?>> providers = new HashMap<>();

  public <C> void bind(Class<C> type, C instance) {
    providers.put(type, () -> instance);
  }

  public <C, I extends C> void bind(Class<C> type, Class<I> implementation) {
    providers.put(type, () -> {
      try {
        Constructor<I> injectConstructor = (Constructor<I>) Arrays.stream(implementation.getConstructors())
            .filter(c -> c.isAnnotationPresent(Inject.class))
            .findFirst()
            .orElseGet(() -> {
              try {
                return implementation.getConstructor();
              } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
              }
            });
        Object[] dependencies = Arrays.stream(injectConstructor.getParameters())
            .map(p -> get(p.getType()))
            .toArray(Object[]::new);
        return injectConstructor.newInstance(dependencies);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public <C> C get(Class<C> type) {
    return (C) providers.get(type).get();
  }
}
