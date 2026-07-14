/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.system;

import java.util.function.Supplier;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.opentelemetry.api.trace.Span;

/** Records low-cardinality product signals without collecting customer data. */
public final class ProductTelemetry {

	private ProductTelemetry() {
	}

	public static void record(String name, Runnable action) {
		Timer.builder(name + ".duration").register(Metrics.globalRegistry).record(action);
		complete(name);
	}

	public static <T> T record(String name, String tagName, String tagValue, Supplier<T> action) {
		T result = Timer.builder(name + ".duration")
			.tag(tagName, tagValue)
			.register(Metrics.globalRegistry)
			.record(action);
		Metrics.counter(name, tagName, tagValue).increment();
		Span.current().addEvent(name);
		return result;
	}

	private static void complete(String name) {
		Metrics.counter(name).increment();
		Span.current().addEvent(name);
	}

}
