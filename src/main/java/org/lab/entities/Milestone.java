package org.lab.entities;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Milestone extends ProjectEntity {
    private Status status;

    private String name;

    public Milestone(UUID id, UUID projectId, String name, Status status) {
        super(id, projectId);

        this.status = status;
        this.name = name;
    }

    public static enum Status {
        Open,
        Active,
        Closed
    }
}
