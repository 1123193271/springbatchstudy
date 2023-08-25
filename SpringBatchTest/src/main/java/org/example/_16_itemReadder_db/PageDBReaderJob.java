package org.example._16_itemReadder_db;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;

@SpringBootApplication
@EnableBatchProcessing
public class PageDBReaderJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;
//    @Bean
    public UserRowMapper userRowMapper() {
        return new UserRowMapper();
    }
//    @Bean
    public PagingQueryProvider pagingQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean providerFactoryBean = new SqlPagingQueryProviderFactoryBean();
        providerFactoryBean.setDataSource(dataSource);
        providerFactoryBean.setSelectClause("select *");
        providerFactoryBean.setFromClause("from user");
//        providerFactoryBean.setWhereClause("where age > :age");
        providerFactoryBean.setSortKey("id");
        return  providerFactoryBean.getObject();
    }


    @Bean
    public JdbcPagingItemReader<User> jdbcPagingItemReader() throws Exception {
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("age", 16);
        return new JdbcPagingItemReaderBuilder<User>()
                .name("UserItemReader")
                .dataSource(dataSource)
                .queryProvider(pagingQueryProvider())
//                .parameterValues(param)
                .pageSize(10)
                .rowMapper(userRowMapper())
                .build();
    }

//    @Bean
    public ItemWriter<User> itemWriter(){
        return new ItemWriter<User>() {
            @Override
            public void write(List<? extends User> items) throws Exception {
                items.forEach(System.err::println);
//                System.err.println(dataSource);
            }
        };
    }

//    @Bean
    public Step step() throws Exception {
        return stepBuilderFactory.get("step33")
                .<User, User>chunk(1)
                .reader(jdbcPagingItemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job() throws Exception {
        return jobBuilderFactory.get("page_db_reader3")
                .start(step())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(PageDBReaderJob.class, args);
    }
}
