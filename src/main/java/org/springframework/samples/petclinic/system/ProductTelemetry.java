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

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.trace.Span;

import org.springframework.stereotype.Component;

@Component
public class ProductTelemetry {

	private static final String INSTRUMENTATION_SCOPE = "org.springframework.samples.petclinic.product";

	private static final AttributeKey<String> VET_LOOKUP_FORMAT = AttributeKey.stringKey("petclinic.vet.lookup.format");

	private final LongCounter ownersCreated;

	private final LongCounter petsRegistered;

	private final LongCounter visitsBooked;

	private final LongCounter vetLookups;

	public ProductTelemetry(OpenTelemetry openTelemetry) {
		var meter = openTelemetry.getMeter(INSTRUMENTATION_SCOPE);
		this.ownersCreated = meter.counterBuilder("petclinic.owner.created")
			.setDescription("Number of owners successfully created")
			.setUnit("{owner}")
			.build();
		this.petsRegistered = meter.counterBuilder("petclinic.pet.registered")
			.setDescription("Number of pets successfully registered")
			.setUnit("{pet}")
			.build();
		this.visitsBooked = meter.counterBuilder("petclinic.visit.booked")
			.setDescription("Number of visits successfully booked")
			.setUnit("{visit}")
			.build();
		this.vetLookups = meter.counterBuilder("petclinic.vet.lookup")
			.setDescription("Number of successful veterinarian lookups")
			.setUnit("{lookup}")
			.build();
	}

	public void ownerCreated() {
		record(this.ownersCreated, "petclinic.owner.created", Attributes.empty());
	}

	public void petRegistered() {
		record(this.petsRegistered, "petclinic.pet.registered", Attributes.empty());
	}

	public void visitBooked() {
		record(this.visitsBooked, "petclinic.visit.booked", Attributes.empty());
	}

	public void vetsLookedUp(String format) {
		Attributes attributes = Attributes.of(VET_LOOKUP_FORMAT, format);
		record(this.vetLookups, "petclinic.vet.lookup", attributes);
	}

	private void record(LongCounter counter, String eventName, Attributes attributes) {
		counter.add(1, attributes);
		Span.current().addEvent(eventName, attributes);
	}

}
