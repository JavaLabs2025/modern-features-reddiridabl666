package org.lab.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.lab.entities.Action;
import org.lab.entities.Ticket;
import org.lab.entities.Ticket.Status;
import org.lab.entities.User;
import org.lab.exceptions.IllegalStatusException;
import org.lab.exceptions.NoAccessException;
import org.lab.services.AccessControlService;
import org.lab.services.TicketService;

import static org.lab.entities.Action.CREATE;
import static org.lab.entities.Action.READ;
import static org.lab.entities.Action.UPDATE;
import static org.lab.entities.Action.UPDATE_STATUS;
import static org.lab.entities.EntityType.TICKET;

public class TicketServiceImpl implements TicketService {
    private static final Map<Status, Status> ALLOWED_STATUS_CHANGE_MAP = Map.of(
            Status.New, Status.ToDo,
            Status.ToDo, Status.InProgress,
            Status.InProgress, Status.Done,
            Status.Done, Status.ToDo);

    private final Map<UUID, Ticket> tickets = new HashMap<>();

    private final AccessControlService accessControlService;

    TicketServiceImpl(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @Override
    public List<Ticket> listAssignedTickets(UUID userId) {
        return tickets.values().stream()
                .filter(ticket -> ticket.getAssigned().contains(userId))
                .toList();
    }

    @Override
    public void create(User user, Ticket ticket) {
        checkAccess(user.getId(), ticket.getProjectId(), CREATE);

        tickets.put(ticket.getId(), ticket);
    }

    @Override
    public void assignTicket(User user, UUID ticketId, UUID developerId) {
        var ticket = tickets.get(ticketId);
        if (ticket == null) {
            throw new RuntimeException("not found");
        }

        checkAccess(user.getId(), ticket.getProjectId(), UPDATE);

        ticket.assign(developerId);
    }

    @Override
    public Status getTicketStatus(User user, UUID ticketId) {
        var ticket = tickets.get(ticketId);
        if (ticket == null) {
            throw new RuntimeException("not found");
        }

        checkAccess(user.getId(), ticket.getProjectId(), READ);

        return ticket.getStatus();
    }

    @Override
    public void setTicketStatus(User user, UUID ticketId, Status status) {
        var ticket = tickets.get(ticketId);
        if (ticket == null) {
            throw new RuntimeException("not found");
        }

        checkAccess(user.getId(), ticket.getProjectId(), UPDATE_STATUS);

        Status allowedStatus = ALLOWED_STATUS_CHANGE_MAP.get(ticket.getStatus());
        if (!allowedStatus.equals(status)) {
            throw new IllegalStatusException(TICKET, allowedStatus, ticket.getStatus());
        }

        ticket.setStatus(status);
    }

    private void checkAccess(UUID userId, UUID projectID, Action action) {
        if (!accessControlService.hasAccess(projectID, userId, TICKET, action)) {
            throw new NoAccessException(TICKET, action, projectID);
        }
    }

    @Override
    public List<Ticket> listTicketsByMilestone(UUID milestoneId) {
        return tickets.values().stream()
                .filter(ticket -> ticket.getMilestoneId().equals(milestoneId))
                .toList();
    }
}
