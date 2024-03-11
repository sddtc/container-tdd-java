package container;

import jakarta.inject.Provider;

import java.lang.reflect.InvocationTargetException;
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
        return implementation.getConstructor().newInstance();
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public <C> C get(Class<C> type) {
    return (C) providers.get(type).get();
  }
}
