package org.example.job.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class CsvToDBJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        long begin = System.currentTimeMillis();
        jobExecution.getExecutionContext().putLong("begin",begin);
        System.err.println("start_time of CsvToDBJob-------------->" + begin + "<-----------------");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long begin = jobExecution.getExecutionContext().getLong("begin");
        long end = System.currentTimeMillis();
        System.err.println("end_time of CsvToDBJob-------------->" + end + "<-----------------");
        System.err.println("duration_time of CsvToDBJob-------------->" + (end - begin) + "<-----------------");

    }
}
