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

package org.springframework.boot.availability;

/**
 * Tagging interface used on {@link ApplicationAvailability} states. This interface is
 * usually implemented on an {@code enum} type.
 *
 * @author Phillip Webb
 * @since 2.3.0
 * @see LivenessState
 * @see ReadinessState
 */
public interface AvailabilityState {

}
