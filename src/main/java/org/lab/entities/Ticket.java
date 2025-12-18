package org.lab.entities;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Ticket extends ProjectEntity {
    private final UUID milestoneId;

    private final Set<UUID> assigned = new HashSet<>();

    private Status status;

    public Ticket(UUID id, UUID projectId, String name, UUID milestoneId, Status status) {
        super(id, projectId, name);
        this.status = status;
        this.milestoneId = milestoneId;
    }

    public static enum Status {
        New,
        ToDo,
        InProgress,
        Done
    }

    public void assign(UUID id) {
        assigned.add(id);
    }
}
