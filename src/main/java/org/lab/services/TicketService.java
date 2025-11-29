package org.lab.services;

import java.util.List;
import java.util.UUID;

import org.lab.entities.Ticket;

public interface TicketService {
    List<Ticket> listAssignedTickets(UUID userId);

    void create(Ticket ticket);

    void assignTicket(UUID ticketId, UUID developerId);

    Ticket.Status getTicketStatus(UUID ticketId);

    void setTicketStatus(UUID ticketId, Ticket.Status status);
}
