package com.amir;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@EnableBatchProcessing
@SpringBootApplication
public class BatchDemoApplication {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	private boolean IS_OK=false;

	@Bean
	public Step step() {
	return this.stepBuilderFactory.get("step1")
	.tasklet(helloWorldTasklet())
	.build();
	}
	
	@Bean 
	public Step step2() {
		return this.stepBuilderFactory.get("step2")
				.tasklet(step2Tasklet())
				.build();
		
	}

	@Bean
	public Tasklet step2Tasklet() {
		Tasklet ret = (contri,chunks)-> {
			System.out.println("This is step2");
			if (IS_OK) {
				throw new RuntimeException("IS_NOTOK");
			}
			return RepeatStatus.FINISHED;
		};
		return ret;
	}

	@Bean
	public Tasklet helloWorldTasklet() {
		return (contribution, chunkContext) -> {
			String name = (String) chunkContext.getStepContext()
			.getJobParameters()
			.get("name");
			System.out.println(String.format("Hello, %s!", name));
			return RepeatStatus.FINISHED;
			};
			


	}
	
	
	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")
				.start(step())
				.next(step2())
				.build();
	}

	
	
	public static void main(String[] args) {
		SpringApplication.run(BatchDemoApplication.class, args);
	}

}
