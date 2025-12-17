package org.lab.services.impl;

import static org.lab.entities.Action.CREATE;
import static org.lab.entities.Action.UPDATE_STATUS;
import static org.lab.entities.EntityType.MILESTONE;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.lab.entities.Action;
import org.lab.entities.Milestone;
import org.lab.entities.Ticket;
import org.lab.entities.Milestone.Status;
import org.lab.entities.User;
import org.lab.exceptions.IllegalStatusException;
import org.lab.exceptions.NoAccessException;
import org.lab.exceptions.NotFoundException;
import org.lab.services.AccessControlService;
import org.lab.services.MilestoneService;
import org.lab.services.TicketService;

public class MilestoneServiceImpl implements MilestoneService {
    private static final Map<Status, Status> ALLOWED_STATUS_CHANGE_MAP = Map.of(
            Status.Open, Status.Active,
            Status.Active, Status.Closed,
            Status.Closed, Status.Open);

    private final Map<UUID, Milestone> milestones = new HashMap<>();

    private final AccessControlService accessControlService;

    private final TicketService ticketService;

    public MilestoneServiceImpl(AccessControlService accessControlService, TicketService ticketService) {
        this.accessControlService = accessControlService;
        this.ticketService = ticketService;
    }

    @Override
    public Milestone create(User user, String name, UUID projectId) {
        checkAccess(user.getId(), projectId, CREATE);

        Milestone milestone = new Milestone(UUID.randomUUID(), projectId, name, Status.Open);

        milestones.put(milestone.getId(), milestone);

        return milestone;
    }

    @Override
    public void setMilestoneStatus(User user, UUID milestoneId, Status status) {
        var milestone = milestones.get(milestoneId);
        if (milestone == null) {
            throw new NotFoundException();
        }

        checkAccess(user.getId(), milestone.getProjectId(), UPDATE_STATUS);

        Status allowedStatus = ALLOWED_STATUS_CHANGE_MAP.get(milestone.getStatus());
        if (!allowedStatus.equals(status)) {
            throw new IllegalStatusException(MILESTONE, allowedStatus, milestone.getStatus());
        }

        if (status == Status.Active) {
            boolean activeMilestoneExists = milestones.values()
                    .stream()
                    .filter(item -> item.getProjectId().equals(milestone.getProjectId())
                            && item.getStatus() == Status.Active)
                    .findFirst()
                    .isPresent();

            if (activeMilestoneExists) {
                throw new IllegalArgumentException("Milestone in active status already exists");
            }
        }

        if (status == Status.Closed) {
            var openedTicketCount = ticketService.listTicketsByMilestone(milestoneId)
                    .stream()
                    .filter(ticket -> ticket.getStatus() != Ticket.Status.Done)
                    .count();

            if (openedTicketCount > 0) {
                throw new IllegalArgumentException("Can't close a milestone if there are open tickets");
            }
        }

        milestone.setStatus(status);
    }

    private void checkAccess(UUID userId, UUID projectID, Action action) {
        if (!accessControlService.hasAccess(projectID, userId, MILESTONE, action)) {
            throw new NoAccessException(MILESTONE, action, projectID);
        }
    }
}
