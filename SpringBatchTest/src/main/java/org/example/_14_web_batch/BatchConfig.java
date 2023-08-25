package org.example._14_web_batch;

import javafx.concurrent.Task;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.Repeat;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    public Tasklet tasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.err.println("SpringBatch Hello world????/.....");
                return RepeatStatus.FINISHED;
            }
        };
    }

    public Step step() {
        return stepBuilderFactory.get("Step")
                .tasklet(tasklet())
                .build();
    }

    public Step step1() {
        return stepBuilderFactory.get("Step")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.err.println("SpringbATCHjOB2222222");
                        return RepeatStatus.FINISHED;
                    };
                }).build();
    }


    @Bean
    public Job job1() {
        return jobBuilderFactory.get("hello_restFul_job1")
                .start(step1())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("hello-restful-job")
                .start(step())
                .incrementer(new RunIdIncrementer())
                .build();
    }
}
