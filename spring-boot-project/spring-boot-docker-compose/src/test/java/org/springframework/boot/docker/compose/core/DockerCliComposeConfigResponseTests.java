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

package org.springframework.boot.docker.compose.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.boot.docker.compose.core.DockerCliComposeConfigResponse.Service;
import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DockerCliComposeConfigResponse}.
 *
 * @author Moritz Halbritter
 * @author Andy Wilkinson
 * @author Phillip Webb
 */
class DockerCliComposeConfigResponseTests {

	@Test
	void deserializeJson() throws IOException {
		String json = new ClassPathResource("docker-compose-config.json", getClass())
			.getContentAsString(StandardCharsets.UTF_8);
		DockerCliComposeConfigResponse response = DockerJson.deserialize(json, DockerCliComposeConfigResponse.class);
		DockerCliComposeConfigResponse expected = new DockerCliComposeConfigResponse("redis-docker",
				Map.of("redis", new Service("redis:7.0")));
		assertThat(response).isEqualTo(expected);
	}

}
