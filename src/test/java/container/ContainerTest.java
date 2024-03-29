package container;

import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

      assertSame(instance, context.get(Component.class).get());
    }

    @Test
    void should_return_empty_if_component_not_defined() {
      Optional<Component> component = context.get(Component.class);
      assertTrue(component.isEmpty());
    }
  }

  @Nested
  public class ConstructorInjection {

    @Test
    void should_bind_type_to_a_class_with_default_constructor() {
      context.bind(Component.class, ComponentWithDefaultConstructor.class);
      Component instance = context.get(Component.class).get();

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
      assertSame(dependency, ((ComponentWithInjectConstructor) context.get(Component.class).get()).getDependency());
    }

    @Test
    void should_bind_type_to_a_class_with_transitive_constructor() {
      context.bind(Component.class, ComponentWithInjectConstructor.class);
      context.bind(Dependency.class, DependencyWithInjectConstructor.class);
      context.bind(String.class, "indirect dependency");

      Dependency dependency = ((ComponentWithInjectConstructor) context.get(Component.class).get()).getDependency();
      assertNotNull(dependency);
      assertSame("indirect dependency", ((DependencyWithInjectConstructor) dependency).getDependency());
    }

    @Test
    void should_throw_exception_when_multi_inject_constructor_provided() {
      assertThrows(IllegalComponentException.class, () -> {
        context.bind(Component.class, ComponentWithMultiInjectConstructor.class);
      });
    }

    @Test
    void should_throw_exception_if_no_inject_nor_constructors_provided() {
      assertThrows(IllegalComponentException.class, () -> {
        context.bind(Component.class, ComponentWithNoInjectNorConstructorProvided.class);
      });
    }

    @Test
    void should_throw_exception_if_no_inject_dependency_found() {
      assertThrows(DependencyNotFoundException.class, () -> {
        context.bind(Component.class, ComponentWithInjectConstructor.class);
        context.get(Component.class);
      });
    }

    @Test
    void should_throw_exception_if_circle_dependencies_found() {
      assertThrows(CircleDependencyFoundException.class, () -> {
        context.bind(Component.class, ComponentWithInjectConstructor.class);
        context.bind(Dependency.class, DependencyWithComponentDependency.class);
        context.get(Component.class);
      });
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

class DependencyWithComponentDependency implements Dependency {

  private Component component;

  @Inject
  public DependencyWithComponentDependency(Component component) {
    this.component = component;
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


class ComponentWithMultiInjectConstructor implements Component {

  @Inject
  public ComponentWithMultiInjectConstructor() {
  }

  @Inject
  public ComponentWithMultiInjectConstructor(String name) {
  }
}

class ComponentWithNoInjectNorConstructorProvided implements Component {

  public ComponentWithNoInjectNorConstructorProvided(String name) {
  }
}
