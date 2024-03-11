package container;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

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
  public class DependencySelection {

  }

  @Nested
  public class LifecycleManagement {

  }
}
