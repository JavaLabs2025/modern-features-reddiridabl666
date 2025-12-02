package org.lab.entities;

import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Ticket extends ProjectEntity {
    private final UUID milestoneId;

    private final Set<UUID> assigned;

    private Status status;

    public Ticket(UUID id, UUID projectId, UUID milestoneId, Set<UUID> assigned, Status status) {
        super(id, projectId);
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

    public void assign(UUID id) {
        assigned.add(id);
    }
}
