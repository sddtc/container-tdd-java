package container;

import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContainerTest {

  Context context;

  @BeforeEach
  public void setup() {
    context = new Context();
  }

  @Nested
  public class ComponentConstruction {

    @Test
    void should_bind_type_to_a_specific_instance() {
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
      context.bind(Component.class, ComponentWithDefaultConstructor.class);
      Component instance = context.get(Component.class);

      assertNotNull(instance);
      assertTrue(instance instanceof ComponentWithDefaultConstructor);
    }

    @Test
    void should_bind_type_to_a_class_with_inject_constructor() {
      Dependency dependency = new Dependency() {
      };
      context.bind(Dependency.class, dependency);
      context.bind(Component.class, ComponentWithInjectConstructor.class);

      assertNotNull(context.get(Component.class));
      assertSame(dependency, ((ComponentWithInjectConstructor) context.get(Component.class)).getDependency());
    }

    @Test
    void should_bind_type_to_a_class_with_transitive_constructor() {
      context.bind(Component.class, ComponentWithInjectConstructor.class);
      context.bind(Dependency.class, DependencyWithInjectConstructor.class);
      context.bind(String.class, "indirect dependency");

      Dependency dependency = ((ComponentWithInjectConstructor) context.get(Component.class)).getDependency();
      assertNotNull(dependency);
      assertSame("indirect dependency", ((DependencyWithInjectConstructor) dependency).getDependency());
    }
  }

  @Nested
  public class DependencySelection {

  }

  @Nested
  public class LifecycleManagement {

  }
}

interface Component {

}

interface Dependency {

}

class DependencyWithInjectConstructor implements Dependency {

  private String dependency;

  @Inject
  public DependencyWithInjectConstructor(String dependency) {
    this.dependency = dependency;
  }

  public String getDependency() {
    return dependency;
  }
}

class ComponentWithDefaultConstructor implements Component {

  public ComponentWithDefaultConstructor() {

  }
}

class ComponentWithInjectConstructor implements Component {

  Dependency dependency;

  @Inject
  public ComponentWithInjectConstructor(Dependency dependency) {
    this.dependency = dependency;
  }

  public Dependency getDependency() {
    return dependency;
  }
}
