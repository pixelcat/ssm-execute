package org.dployr.ssm.api;

import lombok.Data;
import org.springframework.lang.NonNull;

/**
 * Copyright 2022 - Kenzi Stewart
 * Date: 1/28/22, 11:35 AM
 */
@Data
public class CommandResponseWrapper
{
    @NonNull private String standardOutput;
    @NonNull private String standardError;
    @NonNull private String instanceId;
}
