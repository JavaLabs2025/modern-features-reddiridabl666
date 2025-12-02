package org.lab.entities;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class BugReport extends ProjectEntity {
    private Status status;

    public BugReport(UUID id, UUID projectId, Status status) {
        super(id, projectId);
        this.status = status;
    }

    public static enum Status {
        New,
        Fixed,
        Tested,
        Closed
    }
}
