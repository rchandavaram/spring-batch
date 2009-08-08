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

package org.springframework.batch.core.step.tasklet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.JobRepositorySupport;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.batch.repeat.support.TaskExecutorRepeatTemplate;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.StringUtils;

public class AsyncTaskletStepTests {

	private List<String> processed = new CopyOnWriteArrayList<String>();

	private TaskletStep step;

	private int throttleLimit = 20;

	ItemWriter<String> itemWriter = new ItemWriter<String>() {
		public void write(List<? extends String> data) throws Exception {
			// Thread.sleep(100L);
			processed.addAll(data);
		}
	};

	private JobRepository jobRepository;

	@Before
	public void setUp() throws Exception {

		step = new TaskletStep("stepName");

		ResourcelessTransactionManager transactionManager = new ResourcelessTransactionManager();
		step.setTransactionManager(transactionManager);

		List<String> items = Arrays.asList(StringUtils
				.commaDelimitedListToStringArray("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25"));
		RepeatTemplate chunkTemplate = new RepeatTemplate();
		chunkTemplate.setCompletionPolicy(new SimpleCompletionPolicy(2));
		step.setTasklet(new TestingChunkOrientedTasklet<String>(new ListItemReader<String>(items), itemWriter,
				chunkTemplate));

		jobRepository = new JobRepositorySupport();
		step.setJobRepository(jobRepository);

		TaskExecutorRepeatTemplate template = new TaskExecutorRepeatTemplate();
		template.setThrottleLimit(throttleLimit);
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		taskExecutor.setConcurrencyLimit(300);
		template.setTaskExecutor(taskExecutor);
		step.setStepOperations(template);

		step.registerStream(new ItemStreamSupport() {
			private int count = 0;

			@Override
			public void update(ExecutionContext executionContext) {
				executionContext.putInt("counter", count++);
			}
		});

	}

	/**
	 * StepExecution should be updated after every chunk commit.
	 */
	@Test
	public void testStepExecutionUpdates() throws Exception {

		JobExecution jobExecution = jobRepository.createJobExecution("JOB", new JobParameters());
		StepExecution stepExecution = jobExecution.createStepExecution(step.getName());

		step.execute(stepExecution);

		assertEquals(BatchStatus.COMPLETED, stepExecution.getStatus());
		assertEquals(25, stepExecution.getReadCount());
		assertEquals(25, processed.size());

		// System.err.println(stepExecution.getCommitCount());
		// System.err.println(processed);
		// Check commit count didn't spin out of control waiting for other
		// threads to finish...
		assertTrue("Not enough commits: " + stepExecution.getCommitCount(), stepExecution.getCommitCount() > processed
				.size() / 2);
		assertTrue("Too many commits: " + stepExecution.getCommitCount(), stepExecution.getCommitCount() <= processed
				.size()
				/ 2 + throttleLimit + 1);

	}

}
