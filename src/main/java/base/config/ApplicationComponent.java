package base.config;

import com.github.apilab.core.ApplicationLifecycle;
import com.github.apilab.core.GSONModule;
import com.github.apilab.jdbi.JdbiModule;
import com.github.apilab.rest.JavalinModule;
import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = {
  // Our own app module providing custom instances
  ComponentsModule.class,
  // API-LAB modules
  GSONModule.class,
  JavalinModule.class,
  JdbiModule.class})
public interface ApplicationComponent {
  ApplicationLifecycle instance();
}
