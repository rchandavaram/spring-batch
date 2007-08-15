/*
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.repeat;

import junit.framework.TestCase;

/**
 * @author Dave Syer
 *
 */
public class ExitStatusTests extends TestCase {

	/**
	 * Test method for {@link org.springframework.batch.repeat.ExitStatus#ExitStatus(boolean, int)}.
	 */
	public void testExitStatusBooleanInt() {
		ExitStatus status = new ExitStatus(true, 10);
		assertTrue(status.isContinuable());
		assertEquals(10, status.getExitCode());
	}

	/**
	 * Test method for {@link org.springframework.batch.repeat.ExitStatus#ExitStatus(boolean, int)}.
	 */
	public void testExitStatusConstantsContinuable() {
		ExitStatus status = ExitStatus.CONTINUABLE;
		assertTrue(status.isContinuable());
		assertEquals(0, status.getExitCode());
	}

	/**
	 * Test method for {@link org.springframework.batch.repeat.ExitStatus#ExitStatus(boolean, int)}.
	 */
	public void testExitStatusConstantsFinished() {
		ExitStatus status = ExitStatus.FINISHED;
		assertFalse(status.isContinuable());
		assertEquals(0, status.getExitCode());
	}

	/**
	 * Test method for {@link org.springframework.batch.repeat.ExitStatus#and(boolean)}.
	 */
	public void testAndBoolean() {
		assertTrue(ExitStatus.CONTINUABLE.and(true).isContinuable());
		assertFalse(ExitStatus.CONTINUABLE.and(false).isContinuable());
	}

	/**
	 * Test method for {@link org.springframework.batch.repeat.ExitStatus#and(org.springframework.batch.repeat.ExitStatus)}.
	 */
	public void testAndExitStatus() {
		assertTrue(ExitStatus.CONTINUABLE.and(ExitStatus.CONTINUABLE).isContinuable());
		assertFalse(ExitStatus.CONTINUABLE.and(ExitStatus.FINISHED).isContinuable());
	}

	/**
	 * Test method for {@link org.springframework.batch.repeat.ExitStatus#addExitCode(int)}.
	 */
	public void testAddExitCode() {
		ExitStatus status = ExitStatus.FINISHED.addExitCode(10);
		assertEquals(10, status.getExitCode());
		assertFalse(status.isContinuable());
	}

	/**
	 * Test method for {@link org.springframework.batch.repeat.ExitStatus#addExitCode(int)}.
	 */
	public void testAddExitCodeBothNegative() {
		ExitStatus status = ExitStatus.FINISHED.addExitCode(-2);
		assertEquals(-2, status.getExitCode());
		assertEquals(-3, status.addExitCode(-3).getExitCode());
	}

	/**
	 * Test method for {@link org.springframework.batch.repeat.ExitStatus#addExitCode(int)}.
	 */
	public void testAddExitCodeBothPositive() {
		ExitStatus status = ExitStatus.FINISHED.addExitCode(2);
		assertEquals(2, status.getExitCode());
		assertEquals(3, status.addExitCode(3).getExitCode());
	}

	/**
	 * Test method for {@link org.springframework.batch.repeat.ExitStatus#addExitCode(int)}.
	 */
	public void testAddExitCodeNewNegative() {
		ExitStatus status = ExitStatus.FINISHED.addExitCode(2);
		assertEquals(2, status.getExitCode());
		assertEquals(-3, status.addExitCode(-3).getExitCode());
	}
}
