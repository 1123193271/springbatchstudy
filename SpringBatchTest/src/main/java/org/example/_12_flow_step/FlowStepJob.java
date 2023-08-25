package org.example._12_flow_step;

import javafx.concurrent.Task;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableBatchProcessing
public class FlowStepJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    public Tasklet taskletA() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("------------stepA--taskletA---------------");
                return RepeatStatus.FINISHED;
            }
        };
    }

    public Tasklet taskletB1() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("------------stepB1--taskletB1---------------");
                return RepeatStatus.FINISHED;
            }
        };
    }

    public Tasklet taskletB2() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("------------stepB2--taskletB2---------------");
                return RepeatStatus.FINISHED;
            }
        };
    }

    public Tasklet taskletB3() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("------------stepB3--taskletB3---------------");
                return RepeatStatus.FINISHED;
            }
        };
    }

    public Tasklet taskletC() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("------------stepC--taskletC---------------");
                return RepeatStatus.FINISHED;
            }
        };
    }

    public Step stepA() {
        return stepBuilderFactory.get("StepA")
                .tasklet(taskletA())
                .build();
    }

    public Step stepB1() {
        return stepBuilderFactory.get("StepB1")
                .tasklet(taskletB1())
                .build();
    }

    public Step stepB2() {
        return stepBuilderFactory.get("StepB2")
                .tasklet(taskletB2())
                .build();
    }

    public Step stepB3() {
        return stepBuilderFactory.get("StepB3")
                .tasklet(taskletB3())
                .build();
    }

    public Flow flowB() {
        return new FlowBuilder<Flow>("FlowB")
                .start(stepB1())
                .next(stepB2())
                .next(stepB3())
                .build();
    }

    public Step stepB(){
        return stepBuilderFactory.get("StepB")
                .flow(flowB())
                .build();
    }

    public Step stepC() {
        return stepBuilderFactory.get("step3")
                .tasklet(taskletC())
                .build();
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("FlowStepJob")
                .start(stepA())
                .next(stepB())
                .next(stepC())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(FlowStepJob.class, args);
    }
}
