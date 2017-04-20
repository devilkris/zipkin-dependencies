/**
 * Copyright 2016-2017 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin.dependencies.elasticsearch;

import java.util.List;
import org.apache.spark.api.java.function.Function;
import scala.Serializable;
import scala.Tuple2;
import zipkin.DependencyLink;
import zipkin.Span;
import zipkin.internal.DependencyLinker;
import zipkin.internal.Nullable;

final class TraceIdAndJsonToDependencyLinks implements Serializable,
    Function<Iterable<Tuple2<String, String>>, Iterable<DependencyLink>> {
  private static final long serialVersionUID = 0L;

  final TraceIdAndJsonToTrace getTraces;

  TraceIdAndJsonToDependencyLinks(@Nullable Runnable logInitializer) {
    this.getTraces = new TraceIdAndJsonToTrace(logInitializer);
  }

  @Override public Iterable<DependencyLink> call(Iterable<Tuple2<String, String>> traceIdJson) {
    DependencyLinker linker = new DependencyLinker();
    for (List<Span> trace : getTraces.call(traceIdJson)) {
      linker.putTrace(trace);
    }
    return linker.link();
  }
}
