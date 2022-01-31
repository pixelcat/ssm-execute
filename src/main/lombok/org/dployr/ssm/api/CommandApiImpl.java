package org.dployr.ssm.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.CommandInvocationStatus;
import software.amazon.awssdk.services.ssm.model.GetCommandInvocationResponse;
import software.amazon.awssdk.services.ssm.model.SendCommandResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Copyright 2022 - Kenzi Stewart
 * Date: 1/28/22, 9:51 AM
 */
@AllArgsConstructor
public class CommandApiImpl
{
    private static final Log log = LogFactory.getLog(CommandApiImpl.class);
    private static final String DOCUMENT_NAME = "AWS-RunShellScript";

    @NonNull
    @Getter
    @Setter
    SsmClient ssmClient;

    public String sendCommand(String command, Collection<String> instanceIds)
    {
        System.out.printf("Running command '%s' across %d instances%n", command, instanceIds.size());
        final SendCommandResponse commandResponse = ssmClient.sendCommand(b -> b.instanceIds(instanceIds).parameters(Map.of("commands", List.of(command))).documentName(DOCUMENT_NAME));
        return commandResponse.command().commandId();
    }

    public void waitForResults(String commandId, Collection<String> instanceIds)
    {
        System.out.println("Waiting for command execution to start...");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            log.error("", e);
        }
        System.out.printf("Waiting for results...%n");
        final List<CompletableFuture<CommandResponseWrapper>> resultFutures = new ArrayList<>();
        instanceIds.forEach(instanceId -> resultFutures.add(waitForResult(commandId, instanceId)));

        final String[] columnNames = {"instanceId", "Output Type", "Output"};

        List<List<String>> rows = new ArrayList<>();

        for (CompletableFuture<CommandResponseWrapper> resultFuture : resultFutures) {
            try {
                CommandResponseWrapper c = resultFuture.get();
                System.out.println("Instance ID: " + c.getInstanceId());
                System.out.println("======= STDOUT =======");
                System.out.println(c.getStandardOutput());
                System.out.println("======= END STDOUT =======");
                System.out.println("======= STDERR =======");
                System.out.println(c.getStandardError());
                System.out.println("======= END STDERR =======");
            } catch (ExecutionException | InterruptedException e) {
                log.error("", e);
            }
        }
    }

    @Async
    public CompletableFuture<CommandResponseWrapper> waitForResult(String commandId, String instanceId)
    {
        CompletableFuture<CommandResponseWrapper> c = new CompletableFuture<>();
        GetCommandInvocationResponse commandInvocationResponse = null;
        while (commandInvocationResponse == null || commandInvocationResponse.status() == CommandInvocationStatus.IN_PROGRESS) {
            commandInvocationResponse = ssmClient.getCommandInvocation(b -> b.commandId(commandId).instanceId(instanceId));
            if (commandInvocationResponse.status() == CommandInvocationStatus.IN_PROGRESS) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // NOOP
                }
            }
        }
        c.complete(new CommandResponseWrapper(commandInvocationResponse.standardOutputContent(), commandInvocationResponse.standardErrorContent(), instanceId));
        return c;
    }
}
