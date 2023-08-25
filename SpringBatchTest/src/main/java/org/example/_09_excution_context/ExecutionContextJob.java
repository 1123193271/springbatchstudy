package org.example._09_excution_context;

import javafx.concurrent.Task;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.platform.commons.util.ExceptionUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.Repeat;

@SpringBootApplication
@EnableBatchProcessing
public class ExecutionContextJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    public Tasklet tasklet1() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                ExecutionContext stepEC = chunkContext.getStepContext().getStepExecution().getExecutionContext();
                stepEC.put("key-step1-step", "value-step1-step");
                System.err.println("-----------1-----------");
                ExecutionContext jobEC = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
                jobEC.put("key-step1-job", "value-step1-job");

                return RepeatStatus.FINISHED;
            }
        };
    }

    public Tasklet tasklet2() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                ExecutionContext stepEC = chunkContext.getStepContext().getStepExecution().getExecutionContext();
                System.err.println("-----------2-----------");
                System.err.println(stepEC.get("key-step1-step"));

                ExecutionContext jobEC = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
                System.err.println(jobEC.get("key-step1-job"));

                return RepeatStatus.FINISHED;
            }
        };
    }

    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet(tasklet1())
                .build();
    }

    public Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet(tasklet2())
                .build();
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("execution-context-job")
                .start(step1())
                .next(step2())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ExecutionContextJob.class, args);
    }
}
