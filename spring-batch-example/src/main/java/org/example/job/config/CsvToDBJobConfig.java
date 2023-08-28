package org.example.job.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.example.domain.Employee;
import org.example.job.listener.CsvToDBJobListener;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.io.File;

@Configuration
public class CsvToDBJobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Value("${job.data.path}")
    private String path;

    //多线程读文件
    @Bean
    public FlatFileItemReader<Employee> csvToDBItemReader() {
        return new FlatFileItemReaderBuilder<Employee>()
                .name("csvToDBItemReader")
                .saveState(false)
                .resource(new PathResource(new File(path, "employee.csv").getAbsolutePath()))
                .delimited()
                .names("id", "name", "age", "sex")
                .targetType(Employee.class)
                .build();
    }

    //数据库写
    @Bean
    public MyBatisBatchItemWriter<Employee> csvToDBItemWriter() {
        MyBatisBatchItemWriter<Employee> itemWriter = new MyBatisBatchItemWriter<>();
        itemWriter.setSqlSessionFactory(sqlSessionFactory);
        itemWriter.setStatementId("org.example.mapper.EmployeeMapper.saveTemp");
        return itemWriter;
    }

    @Bean
    public Step csvToDBStep() {
        return stepBuilderFactory.get("csvToDBStep")
                .<Employee, Employee>chunk(10000)
                .reader(csvToDBItemReader())
                .writer(csvToDBItemWriter())
                //多线程读写
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public CsvToDBJobListener csvToDBJobListener() {
        return new CsvToDBJobListener();
    }

    @Bean
    public Job csvToDBJob() {
        return jobBuilderFactory.get("csvToDBJob")
                .start(csvToDBStep())
                .incrementer(new RunIdIncrementer())
                .listener(csvToDBJobListener())
                .build();
    }

}
