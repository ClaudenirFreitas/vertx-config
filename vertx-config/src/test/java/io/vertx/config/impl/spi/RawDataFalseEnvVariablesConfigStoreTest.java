/*
 * Copyright (c) 2014 Red Hat, Inc. and others
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 */

package io.vertx.config.impl.spi;

import static org.assertj.core.api.Assertions.assertThat;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.HashMap;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
@PrepareForTest(EnvVariablesConfigStore.class)
public class RawDataFalseEnvVariablesConfigStoreTest extends ConfigStoreTestBase {
  private static final Map<String, String> ENV = new HashMap<>();
  private static final String KEY_1 = "SOME_NUMBER";
  private static final String VAL_1 = "1234567890";
  private static final String KEY_2 = "SOME_BOOLEAN";
  private static final String VAL_2 = "true";

  static {
    ENV.put(KEY_1, VAL_1);
    ENV.put(KEY_2, VAL_2);
  }

  @Before
  public void init() {
    PowerMockito.mockStatic(System.class);
    PowerMockito.when(System.getenv()).thenReturn(ENV);
    factory = new EnvVariablesConfigStore();
    store = factory.create(vertx, new JsonObject().put("raw-data", false));
  }

  @Test
  public void testName() {
    assertThat(factory.name()).isNotNull().isEqualTo("env");
  }

  @Test
  public void testLoadingFromEnvironmentVariables(TestContext context) {
    Async async = context.async();
    getJsonConfiguration(vertx, store, ar -> {
      assertThat(ar.succeeded()).isTrue();
      assertThat(ar.result().getInteger(KEY_1)).isEqualTo(Integer.parseInt(VAL_1));
      assertThat(ar.result().getBoolean(KEY_2)).isEqualTo(Boolean.parseBoolean(VAL_2));
      async.complete();
    });
  }
}
