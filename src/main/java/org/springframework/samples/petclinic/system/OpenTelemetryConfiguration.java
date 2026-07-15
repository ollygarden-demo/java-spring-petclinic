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

import java.util.Collection;
import java.util.List;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.EventData;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.data.StatusData;
import io.opentelemetry.sdk.trace.export.SpanExporter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class OpenTelemetryConfiguration {

	private static final AttributeKey<String> URL_FULL = AttributeKey.stringKey("url.full");

	private static final AttributeKey<String> URL_QUERY = AttributeKey.stringKey("url.query");

	@Bean
	AutoConfigurationCustomizerProvider telemetryPrivacyCustomizer() {
		return customizer -> customizer
			.addSpanExporterCustomizer((exporter, properties) -> new SanitizingSpanExporter(exporter));
	}

	private static final class SanitizingSpanExporter implements SpanExporter {

		private final SpanExporter delegate;

		private SanitizingSpanExporter(SpanExporter delegate) {
			this.delegate = delegate;
		}

		@Override
		public CompletableResultCode export(Collection<SpanData> spans) {
			List<SpanData> sanitized = spans.stream()
				.filter(span -> span.getKind() != SpanKind.CLIENT || span.getParentSpanContext().isValid())
				.map(SanitizedSpanData::new)
				.map(SpanData.class::cast)
				.toList();
			return sanitized.isEmpty() ? CompletableResultCode.ofSuccess() : this.delegate.export(sanitized);
		}

		@Override
		public CompletableResultCode flush() {
			return this.delegate.flush();
		}

		@Override
		public CompletableResultCode shutdown() {
			return this.delegate.shutdown();
		}

	}

	private static final class SanitizedSpanData implements SpanData {

		private final SpanData delegate;

		private SanitizedSpanData(SpanData delegate) {
			this.delegate = delegate;
		}

		@Override
		public String getName() {
			return this.delegate.getName();
		}

		@Override
		public SpanKind getKind() {
			return this.delegate.getKind();
		}

		@Override
		public io.opentelemetry.api.trace.SpanContext getSpanContext() {
			return this.delegate.getSpanContext();
		}

		@Override
		public io.opentelemetry.api.trace.SpanContext getParentSpanContext() {
			return this.delegate.getParentSpanContext();
		}

		@Override
		public StatusData getStatus() {
			return this.delegate.getStatus();
		}

		@Override
		public long getStartEpochNanos() {
			return this.delegate.getStartEpochNanos();
		}

		@Override
		public Attributes getAttributes() {
			Attributes attributes = this.delegate.getAttributes();
			String url = attributes.get(URL_FULL);
			var sanitized = attributes.toBuilder().remove(URL_QUERY);
			if (url != null) {
				int queryStart = url.indexOf('?');
				if (queryStart >= 0) {
					int fragmentStart = url.indexOf('#', queryStart);
					sanitized.put(URL_FULL,
							url.substring(0, queryStart) + (fragmentStart >= 0 ? url.substring(fragmentStart) : ""));
				}
			}
			return sanitized.build();
		}

		@Override
		public List<EventData> getEvents() {
			return this.delegate.getEvents();
		}

		@Override
		public List<LinkData> getLinks() {
			return this.delegate.getLinks();
		}

		@Override
		public long getEndEpochNanos() {
			return this.delegate.getEndEpochNanos();
		}

		@Override
		public boolean hasEnded() {
			return this.delegate.hasEnded();
		}

		@Override
		public int getTotalRecordedEvents() {
			return this.delegate.getTotalRecordedEvents();
		}

		@Override
		public int getTotalRecordedLinks() {
			return this.delegate.getTotalRecordedLinks();
		}

		@Override
		public int getTotalAttributeCount() {
			return getAttributes().size();
		}

		@Override
		@SuppressWarnings("deprecation")
		public io.opentelemetry.sdk.common.InstrumentationLibraryInfo getInstrumentationLibraryInfo() {
			return this.delegate.getInstrumentationLibraryInfo();
		}

		@Override
		public io.opentelemetry.sdk.common.InstrumentationScopeInfo getInstrumentationScopeInfo() {
			return this.delegate.getInstrumentationScopeInfo();
		}

		@Override
		public io.opentelemetry.sdk.resources.Resource getResource() {
			return this.delegate.getResource();
		}

	}

}
