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

package org.springframework.batch.execution.repository.dao;

import java.util.List;

import org.springframework.batch.core.domain.JobInstance;
import org.springframework.batch.core.domain.StepExecution;
import org.springframework.batch.core.domain.StepInstance;

/**
 * Data access object for steps.
 * 
 * TODO: Add java doc.
 * 
 * @author Lucas Ward
 *
 */
public interface StepDao {

	/**
	 * Find a step with the given JobId and Step Name. Return null if none
	 * are found.
	 * 
	 * @param jobId
	 * @param stepName
	 * @return Step
	 */
	public StepInstance findStep(JobInstance job, String stepName);

	/**
	 * Find all steps with the given Job ID.
	 * 
	 * @param jobId
	 * @return
	 */
	public List findSteps(Long jobId);

	/**
	 * Create a step for the given Step Name and Job Id.
	 * @param job
	 * @param stepName
	 * 
	 * @return
	 */
	public StepInstance createStep(JobInstance job, String stepName);

	/**
	 * Update an existing Step.
	 * 
	 * Preconditions: Step must have an ID.
	 * 
	 * @param job
	 */
	public void update(StepInstance step);

	/**
	 * Save the given StepExecution.
	 * 
	 * Preconditions: Id must be null. Postconditions: Id will be set to a
	 * Unique Long.
	 * 
	 * @param stepExecution
	 */
	public void save(StepExecution stepExecution);

	/**
	 * Update the given StepExecution
	 * 
	 * Preconditions: Id must not be null.
	 * 
	 * @param stepExecution
	 */
	public void update(StepExecution stepExecution);

	/**
	 * Return the count of StepExecutions with the given StepId.
	 * 
	 * @param stepId
	 * @return
	 */
	public int getStepExecutionCount(Long stepId);
	
	/**
	 * Return all StepExecutions for the given step. 
	 * 
	 * @param id
	 * @return list of stepExecutions
	 */
	public List findStepExecutions(StepInstance step);
}
