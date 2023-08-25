package org.example._05_composite_validate_parameters;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Map;

@SpringBootApplication
@EnableBatchProcessing
public class CompositeParamterJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Tasklet tasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
                System.err.println("params---name:" + jobParameters.get("name"));
                System.err.println("params---age:" + jobParameters.get("age"));
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get("compositeParameterStep")
                .tasklet(tasklet())
                .build();
    }

//    @Bean
//    public NameParamValidator nameParamValidator() {
//        return new NameParamValidator();
//    }
//
//    @Bean
//    public DefaultJobParametersValidator defaultJobParametersValidator() {
//        DefaultJobParametersValidator defaultJobParametersValidator = new DefaultJobParametersValidator();
//        defaultJobParametersValidator.setOptionalKeys(new String[]{"age"});
//        defaultJobParametersValidator.setRequiredKeys(new String[]{"name"});
//        return defaultJobParametersValidator;
//    }

    @Bean
    public CompositeJobParametersValidator compositeJobParametersValidator() {
        DefaultJobParametersValidator defaultJobParametersValidator = new DefaultJobParametersValidator();
        defaultJobParametersValidator.setRequiredKeys(new String[]{"name"});
        defaultJobParametersValidator.setOptionalKeys(new String[]{"age"});

        NameParamValidator nameParamValidator = new NameParamValidator();
        CompositeJobParametersValidator compositeJobParametersValidator = new CompositeJobParametersValidator();
        compositeJobParametersValidator.setValidators(Arrays.asList(defaultJobParametersValidator, nameParamValidator));
        try {
            compositeJobParametersValidator.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return compositeJobParametersValidator;
    }

    @Bean
    public Job job() {
        return  jobBuilderFactory.get("compositeParameterJob")
                .start(step())
                .validator(compositeJobParametersValidator())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(CompositeParamterJob.class, args);
    }
}
