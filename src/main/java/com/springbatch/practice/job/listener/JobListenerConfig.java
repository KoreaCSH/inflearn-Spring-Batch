package com.springbatch.practice.job.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * desc : 배치 실행 시 파라미터로 '파일' 받기 및 파라미터 검증
 * run : --spring.batch.job.names=jobListenerJob
 */

// 배치 실행 전, 후 작업을 추가할 수 있는 listener

@Configuration
@RequiredArgsConstructor
public class JobListenerConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jobListenerJob(Step jobListenerStep) {
        return jobBuilderFactory.get("jobListenerJob") // get 은 Job 의 이름을 명시해 주는 것
                .incrementer(new RunIdIncrementer())
                .listener(new JobLoggerListener())
                .start(jobListenerStep) // Job 의 step 설정
                .build();

    }

    @JobScope
    @Bean
    public Step jobListenerStep(Tasklet jobListenerTasklet) {
        return stepBuilderFactory.get("jobListenerStep")
                .tasklet(jobListenerTasklet)
                .build();
    }

    // step 의 하위에는 tasklet (간단한 작업), item reader, item writer 가 있다.
    @StepScope
    @Bean
    public Tasklet jobListenerTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("jobListenerTasklet");
                return RepeatStatus.FINISHED;
                //throw new Exception();
            }
        };
    }

}
