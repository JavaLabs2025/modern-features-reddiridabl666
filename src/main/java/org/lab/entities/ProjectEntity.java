package org.lab.entities;

import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public abstract sealed class ProjectEntity permits Milestone, Ticket, BugReport {
    private final UUID id;

    private final UUID projectId;
}
