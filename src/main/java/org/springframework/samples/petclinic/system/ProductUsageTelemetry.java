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
import io.opentelemetry.api.metrics.LongCounter;

import org.springframework.stereotype.Component;

@Component
public class ProductUsageTelemetry {

	private static final String INSTRUMENTATION_SCOPE = "org.springframework.samples.petclinic.product-usage";

	private final LongCounter ownerRegistrations;

	private final LongCounter petRegistrations;

	private final LongCounter visitBookings;

	private final LongCounter vetLookups;

	public ProductUsageTelemetry(OpenTelemetry openTelemetry) {
		var meter = openTelemetry.getMeter(INSTRUMENTATION_SCOPE);
		this.ownerRegistrations = meter.counterBuilder("petclinic.owner.registrations")
			.setDescription("Number of owners successfully registered")
			.setUnit("{registration}")
			.build();
		this.petRegistrations = meter.counterBuilder("petclinic.pet.registrations")
			.setDescription("Number of pets successfully registered")
			.setUnit("{registration}")
			.build();
		this.visitBookings = meter.counterBuilder("petclinic.visit.bookings")
			.setDescription("Number of visits successfully booked")
			.setUnit("{booking}")
			.build();
		this.vetLookups = meter.counterBuilder("petclinic.vet.lookups")
			.setDescription("Number of veterinarian lists successfully retrieved")
			.setUnit("{lookup}")
			.build();
	}

	public void ownerRegistered() {
		this.ownerRegistrations.add(1);
	}

	public void petRegistered() {
		this.petRegistrations.add(1);
	}

	public void visitBooked() {
		this.visitBookings.add(1);
	}

	public void vetsLookedUp() {
		this.vetLookups.add(1);
	}

}
