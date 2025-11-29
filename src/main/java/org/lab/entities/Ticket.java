package org.lab.entities;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Ticket extends ProjectEntity {
    private final UUID milestoneId;

    private Set<UUID> assigned;

    private Status status;

    public Ticket(UUID id, UUID projectId, UUID milestoneId,
            Set<UUID> assigned, Status status, OffsetDateTime openedAt,
            OffsetDateTime closedAt) {
        super(id, projectId, openedAt, closedAt);
        this.status = status;
        this.milestoneId = milestoneId;
        this.assigned = assigned;
    }

    public static enum Status {
        New,
        ToDo,
        InProgress,
        Done
    }
}
