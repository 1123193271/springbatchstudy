//package org.example._14_web_batch;
//
//import org.springframework.batch.core.*;
//import org.springframework.batch.core.explore.JobExplorer;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
//import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
//import org.springframework.batch.core.repository.JobRestartException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class HelloController {
//    @Autowired
//    private JobLauncher jobLauncher;
//    @Autowired
//    private Job job;
//    @Autowired
//    private JobExplorer jobExplorer;
//
//    @GetMapping("/job/start")
//    public ExitStatus start(String name) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
//        JobParameters jp = new JobParametersBuilder(jobExplorer)
//                .getNextJobParameters(job)
//                .addString("name", name)
//                .toJobParameters();
//        JobExecution jobExecution = jobLauncher.run(job, jp);
//        return jobExecution.getExitStatus();
//    }
//
//
//}
