package org.lab.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public abstract sealed class ProjectEntity permits Milestone, Ticket, BugReport {
    private final UUID id;

    private final UUID projectId;

    private OffsetDateTime openedAt;

    private OffsetDateTime closedAt;
}
