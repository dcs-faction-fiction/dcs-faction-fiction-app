/*
 * Copyright 2019 Raffaele Ragni.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package base.config;

import com.github.apilab.core.ApplicationLifecycleItem;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import java.util.Optional;
import javax.inject.Inject;

/**
 *
 * @author Raffaele Ragni
 */
public class MyLifecycleItem implements ApplicationLifecycleItem {

  @Inject Javalin javalin;

  @Inject
  public MyLifecycleItem() {
    ///
  }

  @Override
  public void start() {
    // A specific feature of this api is to publish an external
    // folder for the UI, solves the CORS problem and can be
    // hosted without proxies, in case it's running bare on windows server.
    String uiFolder = Optional.ofNullable(System.getProperty("API_STATIC_FOLDER")).orElse("").trim();
    if (!uiFolder.isBlank()) {
      javalin.config.addStaticFiles(uiFolder, Location.EXTERNAL);
    }
  }

  @Override
  public void stop() {
    // Nothing to stop
  }

}
