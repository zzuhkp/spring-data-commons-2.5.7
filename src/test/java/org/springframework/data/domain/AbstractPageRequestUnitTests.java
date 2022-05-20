/*
 * Copyright 2013-2021 the original author or authors.
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
package org.springframework.data.domain;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.data.domain.UnitTestUtils.*;

import org.junit.jupiter.api.Test;

/**
 * @author Thomas Darimont
 * @author Alex Bondarev
 */
public abstract class AbstractPageRequestUnitTests {

	public abstract AbstractPageRequest newPageRequest(int page, int size);

	@Test // DATACMNS-402
	void preventsNegativePage() {
		assertThatIllegalArgumentException().isThrownBy(() -> newPageRequest(-1, 10));
	}

	@Test // DATACMNS-402
	void preventsNegativeSize() {
		assertThatIllegalArgumentException().isThrownBy(() -> newPageRequest(0, -1));
	}

	@Test // DATACMNS-402
	void navigatesPageablesCorrectly() {

		Pageable request = newPageRequest(1, 10);

		assertThat(request.hasPrevious()).isTrue();
		assertThat(request.next()).isEqualTo((Pageable) newPageRequest(2, 10));

		Pageable first = request.previousOrFirst();

		assertThat(first.hasPrevious()).isFalse();
		assertThat(first).isEqualTo(newPageRequest(0, 10));
		assertThat(first).isEqualTo(request.first());
		assertThat(first.previousOrFirst()).isEqualTo(first);
	}

	@Test // DATACMNS-402
	void equalsHonoursPageAndSize() {

		AbstractPageRequest request = newPageRequest(0, 10);

		// Equals itself
		assertEqualsAndHashcode(request, request);

		// Equals same setup
		assertEqualsAndHashcode(request, newPageRequest(0, 10));

		// Does not equal on different page
		assertNotEqualsAndHashcode(request, newPageRequest(1, 10));

		// Does not equal on different size
		assertNotEqualsAndHashcode(request, newPageRequest(0, 11));
	}

	@Test // DATACMNS-377
	void preventsPageSizeLessThanOne() {
		assertThatIllegalArgumentException().isThrownBy(() -> newPageRequest(0, 0));
	}

	@Test // DATACMNS-1327
	void getOffsetShouldNotCauseOverflow() {

		AbstractPageRequest request = newPageRequest(Integer.MAX_VALUE, Integer.MAX_VALUE);

		assertThat(request.getOffset()).isGreaterThan(Integer.MAX_VALUE);
	}
}
