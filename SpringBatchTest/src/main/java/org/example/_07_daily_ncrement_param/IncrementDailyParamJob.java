package org.example._07_daily_ncrement_param;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.sql.SQLOutput;
import java.util.Map;

@SpringBootApplication
@EnableBatchProcessing
public class IncrementDailyParamJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

//    @Bean
    public Tasklet tasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                System.err.println("params---daily:" + jobParameters.get("daily"));
                return RepeatStatus.FINISHED;
            }
        };
    }

//    @Bean
    public DailyTimestampParamIncrementer dailyTimestampParamIncrementer() {
        return new DailyTimestampParamIncrementer();
    }

//    @Bean
    public Step step() {
        return stepBuilderFactory.get("dailystep")
                .tasklet(tasklet())
                .build();
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("dailyjob")
                .start(step())
                .incrementer(dailyTimestampParamIncrementer())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(IncrementDailyParamJob.class, args);
    }
}
