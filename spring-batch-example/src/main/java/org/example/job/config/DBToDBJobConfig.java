package org.example.job.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.example.domain.Employee;
import org.example.job.partioner.DBToDBPartitioner;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DBToDBJobConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    public static int PAGESIZE = 1000;
    public static int RANGE = 10000;
    public static int GRIDSIZE = 50;
    @Bean
    @StepScope
    public MyBatisPagingItemReader<Employee> dBToDBJobItemReader(
            @Value("#{stepExecutionContext[from]}") final Integer from,
            @Value("#{stepExecutionContext[to]}") final Integer to,
            @Value("#{stepExecutionContext[range]}") final Integer range
    ) {
        System.out.println("----------MyBatisPagingItemReader开始-----from: " + from + "  -----to:" + to + "  -----每片数量:" + range);
        MyBatisPagingItemReader<Employee> itemReader = new MyBatisPagingItemReader();
        itemReader.setSqlSessionFactory(sqlSessionFactory);
        itemReader.setQueryId("org.example.mapper.EmployeeMapper.selectTempForList");
        itemReader.setPageSize(DBToDBJobConfig.PAGESIZE);
        Map<String, Object> map = new HashMap<>();
        map.put("from", from);
        map.put("to", to);
        itemReader.setParameterValues(map);

        return itemReader;
    }

    @Bean
    public MyBatisBatchItemWriter<Employee> dbToDBItemWriter() {
        MyBatisBatchItemWriter<Employee> itemWriter = new MyBatisBatchItemWriter<>();
        itemWriter.setSqlSessionFactory(sqlSessionFactory);
        itemWriter.setStatementId("org.example.mapper.EmployeeMapper.save");

        return itemWriter;
    }

    @Bean
    public Step workStep() {
        return stepBuilderFactory.get("workStep")
                .<Employee, Employee>chunk(DBToDBJobConfig.PAGESIZE)
                .reader(dBToDBJobItemReader(null,null,null))
                .writer(dbToDBItemWriter())
                .build();
    }

    @Bean
    public PartitionHandler dbToDBPartitionHandler() throws Exception {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setGridSize(DBToDBJobConfig.GRIDSIZE);
        handler.setTaskExecutor(new SimpleAsyncTaskExecutor());
        handler.setStep(workStep());
        handler.afterPropertiesSet();

        return handler;
    }

    @Bean
    public DBToDBPartitioner dbToDBPartitioner() {
        return new DBToDBPartitioner();
    }

    @Bean
    public Step masterStep() throws Exception {
        return stepBuilderFactory.get("masterStep")
                .partitioner(workStep().getName(), dbToDBPartitioner())
                .partitionHandler(dbToDBPartitionHandler())
                .build();
    }

    @Bean
    public Job dbToDBJob() throws Exception {
        return jobBuilderFactory.get("dbToDB-Step-Job")
                .start(masterStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }





}
