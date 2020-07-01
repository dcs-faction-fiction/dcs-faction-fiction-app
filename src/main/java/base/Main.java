package base;

import base.config.DaggerApplicationComponent;
import com.github.apilab.core.ApplicationLifecycle;

public class Main {

  static Main main = new Main();
  ApplicationLifecycle app = DaggerApplicationComponent
    .create().instance();

  public static void main(String[] args) {
    main.startInstance();
  }

  public static void stop() {
    main.stopInstance();
  }

  public void startInstance() {
    app.start();
  }

  public void stopInstance() {
    app.stop();
  }

}
