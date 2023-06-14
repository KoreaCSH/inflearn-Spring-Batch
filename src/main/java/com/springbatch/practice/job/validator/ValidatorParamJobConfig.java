package com.springbatch.practice.job.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;


/*
 * desc : 배치 실행 시 파라미터로 '파일' 받기 및 파라미터 검증
 * run : --spring.batch.job.names=validatorParamJob -fileName=test.csv
 */

@Configuration
@RequiredArgsConstructor
public class ValidatorParamJobConfig {

    // job 과 step 은 '1 대 다' 관계이다.
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job validatorParamJob(Step validatorParamStep) {
        return jobBuilderFactory.get("validatorParamJob") // get 은 Job 의 이름을 명시해 주는 것
                .incrementer(new RunIdIncrementer())
                //.validator(new FileParamValidator())
                .validator(jobParametersValidator())
                .start(validatorParamStep) // Job 의 step 설정
                .build();

    }

    // CompositeJobParametersValidator 를 활용하여 하나의 job 에 여러가지 validator 를 설정할 수 있다.
    private CompositeJobParametersValidator jobParametersValidator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
        validator.setValidators(Arrays.asList(new FileParamValidator()));
        return validator;
    }

    @JobScope
    @Bean
    public Step validatorParamStep(Tasklet validatorParamTasklet) {
        return stepBuilderFactory.get("validatorParamStep")
                .tasklet(validatorParamTasklet)
                .build();
    }

    // step 의 하위에는 tasklet (간단한 작업), item reader, item writer 가 있다.
    @StepScope
    @Bean
    public Tasklet validatorParamTasklet(@Value("#{jobParameters['fileName']}") String fileName) {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                // System.out.println(fileName);
                // tasklet 까지 가지 않고 Job 에서 validator 사용
                System.out.println("validator Param");
                return RepeatStatus.FINISHED;
            }
        };
    }

}
