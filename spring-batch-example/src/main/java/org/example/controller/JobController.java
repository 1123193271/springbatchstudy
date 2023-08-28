package org.example.controller;

import org.example.service.EmployeeService;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class JobController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    @Qualifier("csvToDBJob")
    private Job csvToDBJob;

    @Autowired
    @Qualifier("dbToDBJob")
    private Job dbToDBJob;

    @GetMapping("/csvToDB")
    public String csvToDB() throws Exception {
        employeeService.truncateTemp();

        JobParameters jobParameters = new JobParametersBuilder(new JobParameters(), jobExplorer)
                .addLong("time", new Date().getTime())
                .getNextJobParameters(csvToDBJob).toJobParameters();
        //
        JobExecution run = jobLauncher.run(csvToDBJob, jobParameters);
        return run.getId().toString();
    }

    @GetMapping("/dbToDB")
    public String dbToDB() throws Exception {
        employeeService.truncateAll();
        JobParameters jobParameters = new JobParametersBuilder(new JobParameters(), jobExplorer)
                .addLong("time", new Date().getTime()).getNextJobParameters(dbToDBJob).toJobParameters();

        JobExecution run = jobLauncher.run(dbToDBJob, jobParameters);
        return run.getId().toString();
    }

}
