package org.dployr.ssm.execute;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dployr.ssm.api.CommandApiImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import software.amazon.awssdk.services.ssm.SsmClient;

/**
 * Copyright 2022 - Kenzi Stewart
 * Date: 1/28/22, 9:45 AM
 */
@SpringBootConfiguration
@EnableAsync
@ComponentScan
public class SsmExecuteApplication
{
    private static final Log log = LogFactory.getLog(SsmExecuteApplication.class);

    @Bean
    public SsmClient ssmClient()
    {
        return SsmClient.builder().build();
    }

    @Bean
    public CommandApiImpl commandApi()
    {
        return new CommandApiImpl(ssmClient());
    }

    public static void main(String[] args)
    {
        SpringApplication.run(SsmExecuteApplication.class, args).close();
    }
}
