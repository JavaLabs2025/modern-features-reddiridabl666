package org.lab.entities;

import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract sealed class ProjectEntity permits Milestone, Ticket, BugReport {
    private final UUID id;

    private final UUID projectId;

    private final String name;
}
