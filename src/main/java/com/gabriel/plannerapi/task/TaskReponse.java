package com.gabriel.plannerapi.task;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskReponse(
        UUID id,
        String title,
        String description,
        boolean completed,
        LocalDateTime createdAt
) { }
