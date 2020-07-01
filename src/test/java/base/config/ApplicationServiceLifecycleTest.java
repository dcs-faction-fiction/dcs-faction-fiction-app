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

import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import static io.javalin.http.staticfiles.Location.EXTERNAL;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 *
 * @author Raffaele Ragni
 */
public class ApplicationServiceLifecycleTest {
  @Test
  public void test() {
    var cycle = new MyLifecycleItem();
    var javalin = mock(Javalin.class);
    var config = mock(JavalinConfig.class);
    cycle.javalin = javalin;
    cycle.javalin.config = config;

    cycle.start();
    verify(config, times(0)).addStaticFiles(any(), any());

    System.setProperty("API_STATIC_FOLDER", "folder");
    cycle.start();
    verify(config).addStaticFiles("folder", EXTERNAL);

    System.clearProperty("API_STATIC_FOLDER");
  }
}
