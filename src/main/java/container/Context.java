package container;

import java.util.HashMap;
import java.util.Map;

public class Context {

  private final Map<Class<?>, Object> components = new HashMap<Class<?>, Object>();

  public <C> void bind(Class<C> type, C instance) {
    components.put(type, instance);
  }

  public <C> C get(Class<C> type) {
    return (C) components.get(type);
  }
}
