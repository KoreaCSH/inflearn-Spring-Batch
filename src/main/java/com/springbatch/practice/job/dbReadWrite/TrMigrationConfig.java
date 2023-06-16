package com.springbatch.practice.job.dbReadWrite;

import com.springbatch.practice.job.domain.accounts.Accounts;
import com.springbatch.practice.job.domain.accounts.AccountsRepository;
import com.springbatch.practice.job.domain.orders.Orders;
import com.springbatch.practice.job.domain.orders.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;

/*
 * desc : 주문 테이블 -> 정산 테이블로 데이터 이관
 * run : --spring.batch.job.names=trMigrationJob
 */

@Configuration
@RequiredArgsConstructor
public class TrMigrationConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final OrdersRepository ordersRepository;
    private final AccountsRepository accountsRepository;

    @Bean
    public Job trMigrationJob(Step trMigrationStep) {
        return jobBuilderFactory.get("trMigrationJob") // get 은 Job 의 이름을 명시해 주는 것
                .incrementer(new RunIdIncrementer())
                .start(trMigrationStep) // Job 의 step 설정
                .build();

    }

    @JobScope
    @Bean
    public Step trMigrationStep(ItemReader trOrdersReader, ItemProcessor toOrderProcessor, ItemWriter trAccountsWriter) {
        return stepBuilderFactory.get("trMigrationStep")
                .<Orders, Accounts>chunk(5)
                .reader(trOrdersReader)
//                .writer(new ItemWriter() {
//                    @Override
//                    public void write(List items) throws Exception {
//                        items.forEach(System.out::println);
//                    }
//                })
                .processor(toOrderProcessor)
                .writer(trAccountsWriter)
                .build();
    }

    // JdbcCursorItemReader 를 사용하여 JPA 가 아닌 sql 문으로도 Batch 작업 명령을 할 수 있다.

    @StepScope
    @Bean
    public RepositoryItemReader<Orders> trOrdersReader() {
        return new RepositoryItemReaderBuilder<Orders>()
                .name("trOrdersReader")
                .repository(ordersRepository)
                .methodName("findAll")
                // 보통 chunk size 와 동일하게
                .pageSize(5)
                .arguments(Arrays.asList())
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    // Orders 객체를 Accounts 객체로
    @StepScope
    @Bean
    public ItemProcessor<Orders, Accounts> toOrderProcessor() {
        return new ItemProcessor<Orders, Accounts>() {
            @Override
            public Accounts process(Orders item) throws Exception {
                return new Accounts(item);
            }
        };
    }

    @StepScope
    @Bean
    public RepositoryItemWriter<Accounts> trAccountsWriter() {
        return new RepositoryItemWriterBuilder<Accounts>()
                .methodName("trAccountsWriter")
                .repository(accountsRepository)
                .methodName("save")
                .build();
    }

}
