package com.gabriel.plannerapi.task;

import jakarta.validation.constraints.Size;

public record TaskPatchRequest(
        @Size(min = 1, max = 200) String title,
        String description,
        Boolean completed
) {}
