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

package org.springframework.boot.origin;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link OriginTrackedValue}.
 *
 * @author Phillip Webb
 */
class OriginTrackedValueTests {

	@Test
	void getValueShouldReturnValue() {
		Object value = new Object();
		assertThat(OriginTrackedValue.of(value).getValue()).isEqualTo(value);
	}

	@Test
	void getOriginShouldReturnOrigin() {
		Object value = new Object();
		Origin origin = mock(Origin.class);
		assertThat(OriginTrackedValue.of(value, origin).getOrigin()).isEqualTo(origin);
	}

	@Test
	void toStringShouldReturnValueToString() {
		Object value = new Object();
		assertThat(OriginTrackedValue.of(value)).hasToString(value.toString());
	}

	@Test
	void hashCodeAndEqualsShouldIgnoreOrigin() {
		Object value1 = new Object();
		OriginTrackedValue tracked1 = OriginTrackedValue.of(value1);
		OriginTrackedValue tracked2 = OriginTrackedValue.of(value1, mock(Origin.class));
		OriginTrackedValue tracked3 = OriginTrackedValue.of(new Object());
		assertThat(tracked1).hasSameHashCodeAs(tracked2);
		assertThat(tracked1).isEqualTo(tracked1).isEqualTo(tracked2).isNotEqualTo(tracked3);
	}

	@Test
	void ofWhenValueIsNullShouldReturnNull() {
		assertThat(OriginTrackedValue.of(null)).isNull();
		assertThat(OriginTrackedValue.of(null, mock(Origin.class))).isNull();
	}

	@Test
	void ofWhenValueIsCharSequenceShouldReturnCharSequence() {
		String value = "foo";
		OriginTrackedValue tracked = OriginTrackedValue.of(value);
		assertThat(tracked).isInstanceOf(CharSequence.class);
		CharSequence charSequence = (CharSequence) tracked;
		assertThat(charSequence).hasSameSizeAs(value);
		assertThat(charSequence.charAt(0)).isEqualTo(value.charAt(0));
		assertThat(charSequence.subSequence(0, 1)).isEqualTo(value.subSequence(0, 1));
	}

}
