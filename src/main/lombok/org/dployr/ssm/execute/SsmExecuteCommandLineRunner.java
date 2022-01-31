package org.dployr.ssm.execute;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dployr.ssm.api.CommandApiImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Copyright 2022 - Kenzi Stewart
 * Date: 1/28/22, 9:50 AM
 */
@Component
@RequiredArgsConstructor
public class SsmExecuteCommandLineRunner implements CommandLineRunner
{
    private static final Log log = LogFactory.getLog(SsmExecuteCommandLineRunner.class);

    @NonNull
    private CommandApiImpl commandApi;

    @Override public void run(String... args)
    {
        if (args.length < 2) {
            usage();
        }
        final Optional<String> instanceIds = Arrays.stream(args).findFirst();
        final List<String> instanceIdList = Arrays.asList(instanceIds.orElse("").split(","));

        final String command = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        final String commandId = commandApi.sendCommand(command, instanceIdList);

        commandApi.waitForResults(commandId, instanceIdList);
    }

    private void usage()
    {
        System.err.println("At least two arguments are required:" + System.lineSeparator() +
                           "  <instanceIds> - A comma separated list of AWS EC2 instance IDs" + System.lineSeparator() +
                           "  <command> - The command to execute" + System.lineSeparator()
        );
        System.exit(254);
    }
}
