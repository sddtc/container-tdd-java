package container;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContainerTest {

  interface Component {

  }

  @Nested
  public class ComponentConstruction {

    @Test
    void should_bind_type_to_a_specific_instance() {
      Context context = new Context();
      Component instance = new Component() {
      };

      context.bind(Component.class, instance);

      assertSame(instance, context.get(Component.class));
    }
  }

  @Nested
  public class ConstructorInjection {

    @Test
    void should_bind_type_to_a_class_with_default_constructor() {
      Context context = new Context();

      context.bind(Component.class, ComponentWithDefaultConstructor.class);
      Component instance = context.get(Component.class);

      assertNotNull(instance);
      assertTrue(instance instanceof ComponentWithDefaultConstructor);
    }

  }

  @Nested
  public class DependencySelection {

  }

  @Nested
  public class LifecycleManagement {

  }
}

class ComponentWithDefaultConstructor implements ContainerTest.Component {

  public ComponentWithDefaultConstructor() {

  }

}
