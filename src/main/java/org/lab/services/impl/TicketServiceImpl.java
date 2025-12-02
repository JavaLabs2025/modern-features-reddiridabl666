package org.lab.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.lab.entities.Ticket;
import org.lab.entities.Ticket.Status;
import org.lab.services.TicketService;

public class TicketServiceImpl implements TicketService {
    private final Map<UUID, Ticket> tickets = new HashMap<>();

    @Override
    public List<Ticket> listAssignedTickets(UUID userId) {
        return tickets.values().stream()
                .filter(ticket -> ticket.getAssigned().contains(userId))
                .toList();
    }

    @Override
    public void create(Ticket ticket) {
        tickets.put(ticket.getId(), ticket);
    }

    @Override
    public void assignTicket(UUID ticketId, UUID developerId) {
        var ticket = tickets.get(ticketId);
        if (ticket == null) {
            throw new RuntimeException("not found");
        }

        ticket.assign(developerId);
    }

    @Override
    public Status getTicketStatus(UUID ticketId) {
        var ticket = tickets.get(ticketId);
        if (ticket == null) {
            throw new RuntimeException("not found");
        }

        return ticket.getStatus();
    }

    @Override
    public void setTicketStatus(UUID ticketId, Status status) {
        var ticket = tickets.get(ticketId);
        if (ticket == null) {
            throw new RuntimeException("not found");
        }

        ticket.setStatus(status);
    }
}
