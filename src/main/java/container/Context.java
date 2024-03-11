package container;

import java.util.HashMap;
import java.util.Map;

public class Context {

  private final Map<Class<?>, Object> context = new HashMap<Class<?>, Object>();

  public <C> void bind(Class<C> type, C instance) {
    context.put(type, instance);
  }

  public <C> C get(Class<C> type) {
    return (C) context.get(type);
  }
}
