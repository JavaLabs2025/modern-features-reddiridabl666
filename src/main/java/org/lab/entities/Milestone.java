package org.lab.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Milestone extends ProjectEntity {
    private Status status;

    public Milestone(UUID id, UUID projectId, Status status, OffsetDateTime openedAt, OffsetDateTime closedAt) {
        super(id, projectId, openedAt, closedAt);
        this.status = status;
    }

    public static enum Status {
        Open,
        Active,
        Closed
    }
}
