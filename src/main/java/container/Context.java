package container;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Context {

  private final Map<Class<?>, Object> components = new HashMap<>();
  private final Map<Class<?>, Class<?>> componentsWithImplementations = new HashMap<>();

  public <C> void bind(Class<C> type, C instance) {
    components.put(type, instance);
  }

  public <C> C get(Class<C> type) {
    if (components.containsKey(type)) {
      return (C) components.get(type);
    }
    try {
      return (C) componentsWithImplementations.get(type).getConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  public <C, I extends C> void bind(Class<C> type, Class<I> implementation) {
    componentsWithImplementations.put(type, implementation);
  }
}
