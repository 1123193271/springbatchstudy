package org.example._17_flat_writer;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;

@SpringBootApplication
@EnableBatchProcessing
public class FlatWriteJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<User> itemReader() {
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")
                .resource(new ClassPathResource("user.txt"))
                .delimited().delimiter("#")
                .names("id", "name", "age")
                .targetType(User.class)
                .build();
    }

    @Bean
    public FlatFileItemWriter<User> itemWriter() {
        return new FlatFileItemWriterBuilder<User>()
                .name("flatfileItemwriter")
                .resource(new PathResource("c:/outUser.txt"))
                .formatted().format("id: %s,姓名: %s, 年龄： %s")
                .names("id", "name", "age")
                .build();
    }

    public Step step() {
        return stepBuilderFactory.get("steps")
                .<User, User>chunk(1)
                .reader(itemReader())
                .writer(itemWriter())
                .build();

    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("flat_writer_job")
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(FlatWriteJob.class, args);
    }

}
