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

package org.springframework.boot.testcontainers.properties;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.testcontainers.lifecycle.TestcontainersLifecycleApplicationContextInitializer;
import org.springframework.boot.testsupport.container.DisabledIfDockerUnavailable;
import org.springframework.boot.testsupport.container.TestImage;
import org.springframework.boot.testsupport.system.CapturedOutput;
import org.springframework.boot.testsupport.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.springframework.test.context.DynamicPropertyRegistry;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TestcontainersPropertySourceAutoConfiguration}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
@DisabledIfDockerUnavailable
@ExtendWith(OutputCaptureExtension.class)
class TestcontainersPropertySourceAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withInitializer(new TestcontainersLifecycleApplicationContextInitializer())
		.withConfiguration(AutoConfigurations.of(TestcontainersPropertySourceAutoConfiguration.class));

	@Test
	void dynamicPropertyRegistrarBeanContributesProperties(CapturedOutput output) {
		this.contextRunner.withUserConfiguration(ContainerAndPropertyRegistrarConfiguration.class).run((context) -> {
			TestBean testBean = context.getBean(TestBean.class);
			RedisContainer redisContainer = context.getBean(RedisContainer.class);
			assertThat(testBean.getUsingPort()).isEqualTo(redisContainer.getFirstMappedPort());
		});
	}

	@Configuration(proxyBeanMethods = false)
	@EnableConfigurationProperties(ContainerProperties.class)
	@Import(TestBean.class)
	static class ContainerAndPropertiesConfiguration {

		@Bean
		RedisContainer redisContainer(DynamicPropertyRegistry properties) {
			RedisContainer container = TestImage.container(RedisContainer.class);
			properties.add("container.port", container::getFirstMappedPort);
			return container;
		}

	}

	@Configuration(proxyBeanMethods = false)
	@EnableConfigurationProperties(ContainerProperties.class)
	@Import(TestBean.class)
	static class ContainerAndPropertyRegistrarConfiguration {

		@Bean
		RedisContainer redisContainer() {
			return TestImage.container(RedisContainer.class);
		}

		@Bean
		DynamicPropertyRegistrar redisProperties(RedisContainer container) {
			return (registry) -> registry.add("container.port", container::getFirstMappedPort);
		}

	}

	@ConfigurationProperties("container")
	record ContainerProperties(int port) {
	}

	static class TestBean {

		private int usingPort;

		TestBean(ContainerProperties containerProperties) {
			this.usingPort = containerProperties.port();
		}

		int getUsingPort() {
			return this.usingPort;
		}

	}

}
