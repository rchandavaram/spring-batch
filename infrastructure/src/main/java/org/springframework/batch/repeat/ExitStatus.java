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

/**
 * Value object used to carry information about the status of a
 * {@link RepeatOperations}.
 * 
 * @author Dave Syer
 * 
 */
public class ExitStatus {

	/**
	 * Convenient constant value representing unfinished processing.
	 */
	public static ExitStatus CONTINUABLE = new ExitStatus(true);

	/**
	 * Convenient constant value representing finished processing.
	 */
	public static ExitStatus FINISHED = new ExitStatus(false);

	/**
	 * Convenient constant value representing finished processing with an error.
	 */
	public static ExitStatus FAILED = new ExitStatus(false, -1);

	private final boolean continuable;

	private final int exitCode;

	public ExitStatus(boolean continuable) {
		this(continuable, 0);
	}

	public ExitStatus(boolean continuable, int exitCode) {
		super();
		this.continuable = continuable;
		this.exitCode = exitCode;
	}

	/**
	 * Flag to signal that processing can continue. This is distinct from any
	 * flag that might indicate that a batch is complete, or terminated, since a
	 * batch might be only a small part of a larger whole, which is still not
	 * finished.
	 * 
	 * @return true if processing can continue.
	 */
	public boolean isContinuable() {
		return continuable;
	}

	/**
	 * Getter for the exit code (defaults to 0).
	 * @return the exit code.
	 */
	public int getExitCode() {
		return exitCode;
	}

	/**
	 * Create a new {@link ExitStatus} with a logical combination of the
	 * continuable flag.
	 * @param continuable true if the caller thinks it is safe to continue.
	 * @return a new {@link ExitStatus} with {@link #isContinuable()} the
	 * logical and of the current value and the argument provided.
	 */
	public ExitStatus and(boolean continuable) {
		return and(new ExitStatus(continuable));
	}

	/**
	 * Create a new {@link ExitStatus} with a logical combination of the
	 * continuable flag and adding the other exit code.
	 * @param other an other {@link ExitStatus}.
	 * @return a new {@link ExitStatus} with {@link #isContinuable()} the
	 * logical and of the current value and the other's.
	 */
	public ExitStatus and(ExitStatus other) {
		return new ExitStatus(this.continuable && other.isContinuable(), exitCode).addExitCode(other.getExitCode());
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "continuable=" + continuable + ";exitCode=" + exitCode;
	}

	/**
	 * Duplicates the existing status except that a new exit code is added as
	 * per the argument. If both codes (the current and the new one provided)
	 * are negative then the lower of the two is returned, otherwise the higher
	 * or the two.
	 * 
	 * @param exitCode the new value of the exitCode
	 * @return a new {@link ExitStatus} instance
	 */
	public ExitStatus addExitCode(int exitCode) {
		if (this.exitCode < 0 || exitCode < 0) {
			exitCode = Math.min(this.exitCode, exitCode);
		}
		else {
			exitCode = Math.max(this.exitCode, exitCode);
		}
		return new ExitStatus(this.continuable, exitCode);
	}

}
