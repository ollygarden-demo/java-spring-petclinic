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
public class ProductUsageMetrics {

	private static final String INSTRUMENTATION_SCOPE = "org.springframework.samples.petclinic";

	private final LongCounter ownersCreated;

	private final LongCounter petsRegistered;

	private final LongCounter visitsBooked;

	private final LongCounter vetsLookedUp;

	public ProductUsageMetrics(OpenTelemetry openTelemetry) {
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
		this.vetsLookedUp = meter.counterBuilder("petclinic.vet.looked_up")
			.setDescription("Number of successful vet list lookups")
			.setUnit("{lookup}")
			.build();
	}

	public void ownerCreated() {
		this.ownersCreated.add(1);
	}

	public void petRegistered() {
		this.petsRegistered.add(1);
	}

	public void visitBooked() {
		this.visitsBooked.add(1);
	}

	public void vetsLookedUp() {
		this.vetsLookedUp.add(1);
	}

}
