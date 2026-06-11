package com.gabriel.plannerapi.task;

public record TaskPatchRequest(
        String title,
        String description,
        Boolean completed
) {}
