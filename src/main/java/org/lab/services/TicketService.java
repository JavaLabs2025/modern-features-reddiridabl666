package org.lab.services;

import java.util.List;
import java.util.UUID;

import org.lab.entities.Ticket;
import org.lab.entities.User;

public interface TicketService {
    List<Ticket> listAssignedTickets(UUID userId);

    List<Ticket> listTicketsByMilestone(UUID milestoneId);

    Ticket create(User user, UUID projectId, UUID milestoneId, String name);

    void assignTicket(User user, UUID ticketId, UUID developerId);

    Ticket.Status getTicketStatus(User user, UUID ticketId);

    void setTicketStatus(User user, UUID ticketId, Ticket.Status status);
}
