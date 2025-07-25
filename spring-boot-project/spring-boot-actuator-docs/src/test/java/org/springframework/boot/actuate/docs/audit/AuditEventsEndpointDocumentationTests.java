/*
 * Copyright 2012-present the original author or authors.
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

package org.springframework.boot.actuate.docs.audit;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.AuditEventsEndpoint;
import org.springframework.boot.actuate.docs.MockMvcEndpointDocumentationTests;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

/**
 * Tests for generating documentation describing {@link AuditEventsEndpoint}.
 *
 * @author Andy Wilkinson
 */
class AuditEventsEndpointDocumentationTests extends MockMvcEndpointDocumentationTests {

	@MockitoBean
	private AuditEventRepository repository;

	@Test
	void allAuditEvents() {
		String queryTimestamp = "2017-11-07T09:37Z";
		given(this.repository.find(any(), any(), any()))
			.willReturn(List.of(new AuditEvent("alice", "logout", Collections.emptyMap())));
		assertThat(this.mvc.get().uri("/actuator/auditevents").param("after", queryTimestamp)).hasStatusOk()
			.apply(document("auditevents/all",
					responseFields(fieldWithPath("events").description("An array of audit events."),
							fieldWithPath("events.[].timestamp")
								.description("The timestamp of when the event occurred."),
							fieldWithPath("events.[].principal").description("The principal that triggered the event."),
							fieldWithPath("events.[].type").description("The type of the event."))));
	}

	@Test
	void filteredAuditEvents() {
		String queryTimestamp = "2017-11-07T09:37Z";
		Instant instant = Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(queryTimestamp));
		given(this.repository.find("alice", instant, "logout"))
			.willReturn(List.of(new AuditEvent(instant.plusSeconds(73), "alice", "logout", Collections.emptyMap())));
		assertThat(this.mvc.get()
			.uri("/actuator/auditevents")
			.param("principal", "alice")
			.param("after", queryTimestamp)
			.param("type", "logout"))
			.hasStatusOk()
			.apply(document("auditevents/filtered",
					queryParameters(
							parameterWithName("after").description(
									"Restricts the events to those that occurred after the given time. Optional."),
							parameterWithName("principal")
								.description("Restricts the events to those with the given principal. Optional."),
							parameterWithName("type")
								.description("Restricts the events to those with the given type. Optional."))));
		then(this.repository).should().find("alice", instant, "logout");
	}

	@Configuration(proxyBeanMethods = false)
	static class TestConfiguration {

		@Bean
		AuditEventsEndpoint auditEventsEndpoint(AuditEventRepository repository) {
			return new AuditEventsEndpoint(repository);
		}

	}

}
