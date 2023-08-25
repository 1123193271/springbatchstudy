package org.example._04_default_validator_parameter;

import org.example._03_validator_parameter.NameParamValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@SpringBootApplication
@EnableBatchProcessing
public class DefaultValidateJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Tasklet tasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                Map<String, Object> parameters = chunkContext.getStepContext().getJobParameters();
                System.err.println("params---name:" + parameters.get("name"));
                System.err.println("params---age:" + parameters.get("age"));
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get("defaultvalidatestep")
                .tasklet(tasklet())
                .build();
    }


    @Bean
    public DefaultJobParametersValidator defaultJobParametersValidator() {
        DefaultJobParametersValidator defaultJobParametersValidator = new DefaultJobParametersValidator();
        defaultJobParametersValidator.setRequiredKeys(new String[]{"name"});
        defaultJobParametersValidator.setOptionalKeys(new String[]{"age"});
        return defaultJobParametersValidator;
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("defaultvalidatejob")
                .start(step())
                .validator(defaultJobParametersValidator())
                .build();
    }


    public static void main(String[] args) {
        SpringApplication.run(DefaultValidateJob.class, args);
    }
}
