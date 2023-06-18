package com.springbatch.practice.job.fileReadWrite;

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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

/*
 * desc : 주문 테이블 -> 정산 테이블로 데이터 이관
 * run : --spring.batch.job.names=fileDataReadWriteJob
 */

@Configuration
@RequiredArgsConstructor
public class FileDataReadWriteConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job fileDataReadWriteJob(Step fileDataReadWriteStep) {
        return jobBuilderFactory.get("fileDataReadWriteJob")
                .incrementer(new RunIdIncrementer())
                .start(fileDataReadWriteStep)
                .build();
    }

    @JobScope
    @Bean
    public Step fileDataReadWriteStep(ItemReader playerFlatFileItemReader, ItemProcessor itemProcessor, ItemWriter playerYearsFlatFileItemReader) {
        return stepBuilderFactory.get("fileDataReadWriteStep")
                .<Player, PlayerYears>chunk(5) // 데이터 처리할 chuck size
                .reader(playerFlatFileItemReader)
                /*.writer(new ItemWriter() {
                    @Override
                    public void write(List items) throws Exception {
                        items.forEach(System.out::println);
                    }
                })*/
                .processor(itemProcessor)
                .writer(playerYearsFlatFileItemReader)
                .build();
    }

    @StepScope
    @Bean
    public FlatFileItemReader<Player> playerFlatFileItemReader() {
        return new FlatFileItemReaderBuilder<Player>()
                .name("playerFlatFileItemReader")
                .resource(new FileSystemResource("players.csv"))
                .lineTokenizer(new DelimitedLineTokenizer())
                .fieldSetMapper(new PlayerFieldSetMapper()) // file 에서 읽은 내용을 class 로 매핑하는 함수
                .linesToSkip(1) // 첫 번째 줄은 제목이기에 skip 하겠다.
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<Player, PlayerYears> itemProcessor() {
        return new ItemProcessor<Player, PlayerYears>() {
            @Override
            public PlayerYears process(Player item) throws Exception {
                return new PlayerYears(item);
            }
        };
    }

    @StepScope
    @Bean
    public FlatFileItemWriter<PlayerYears> playerYearsFlatFileItemReader() {
        BeanWrapperFieldExtractor<PlayerYears> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"ID", "lastName", "position", "yearsExperience"});
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<PlayerYears> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        FileSystemResource outputResource = new FileSystemResource("players_output.txt");

        return new FlatFileItemWriterBuilder<PlayerYears>()
                .name("playerYearsFlatFileItemReader")
                .resource(outputResource)
                .lineAggregator(lineAggregator)
                .build();
    }

}
