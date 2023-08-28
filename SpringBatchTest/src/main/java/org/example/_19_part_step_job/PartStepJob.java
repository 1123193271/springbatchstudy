package org.example._19_part_step_job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.List;

@SpringBootApplication
@EnableBatchProcessing
public class PartStepJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public FlatFileItemReader<User> flatFileItemReader(@Value("#{stepExecutionContext['file']}")
                                                       Resource resource) {
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemreader")
                .resource(resource)
                .delimited().delimiter("#")
                .names("id", "name", "age")
                .targetType(User.class)
                .build();

    }

    public ItemWriter<User> itemWriter() {
        return new ItemWriter<User>() {
            @Override
            public void write(List<? extends User> items) throws Exception {
                items.forEach(System.err::println);
            }
        };
    }

    //分区器
    public UserPartitioner userPartitioner() {
        return new UserPartitioner();
    }

    //分区处理器
    public PartitionHandler userPartitionHandler() {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setGridSize(5);
        handler.setTaskExecutor(new SimpleAsyncTaskExecutor());
        handler.setStep(workStep());
        try {
            handler.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return handler;

    }

    //从分区
    @Bean
    public Step workStep() {
        return stepBuilderFactory.get("workStep")
                .<User, User>chunk(10)
                .reader(flatFileItemReader(null))
                .writer(itemWriter())
                .build();
    }

    //主分区
    @Bean
    public Step masterStep() {
        return stepBuilderFactory.get("masterStep")
                .partitioner(workStep().getName(), userPartitioner())
                .partitionHandler(userPartitionHandler())
                .build();
    }



    @Bean
    public Job partJob(){
        return jobBuilderFactory.get("part-step-job")
                .start(masterStep())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(PartStepJob.class, args);
    }

}
