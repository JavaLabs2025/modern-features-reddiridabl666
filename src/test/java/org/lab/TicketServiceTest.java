package org.lab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.lab.entities.Ticket;
import org.lab.entities.Ticket.Status;
import org.lab.exceptions.NoAccessException;
import org.lab.entities.Milestone;
import org.lab.entities.Project;
import org.lab.entities.Role;
import org.lab.entities.User;
import org.lab.services.AccessControlService;
import org.lab.services.MilestoneService;
import org.lab.services.ProjectService;
import org.lab.services.TicketService;
import org.lab.services.UserService;
import org.lab.services.impl.AccessControlServiceImpl;
import org.lab.services.impl.MilestoneServiceImpl;
import org.lab.services.impl.ProjectServiceImpl;
import org.lab.services.impl.TicketServiceImpl;
import org.lab.services.impl.UserServiceImpl;

public class TicketServiceTest {
    static UserService userService;

    static User requester;
    static User otherUser;

    AccessControlService accessControlService;

    TicketService ticketService;

    ProjectService projectService;

    MilestoneService milestoneService;

    Project project;
    Milestone milestone;

    @BeforeAll
    static void beforeAll() {
        userService = new UserServiceImpl();
        requester = userService.register("test user");
        otherUser = userService.register("other user");
    }

    @BeforeEach
    void beforeEach() {
        projectService = new ProjectServiceImpl(userService);

        project = projectService.create(requester, "test project");

        accessControlService = new AccessControlServiceImpl(projectService);

        ticketService = new TicketServiceImpl(accessControlService);

        milestoneService = new MilestoneServiceImpl(accessControlService, ticketService);

        milestone = milestoneService.create(requester, "Test milestone", project.getId());
    }

    @Test
    void shouldCreateTicketForManager() {
        var ticket = ticketService.create(requester, project.getId(), milestone.getId(), "test ticket");

        assertThat(ticket).extracting(Ticket::getName).isEqualTo("test ticket");
    }

    @Test
    void shouldCreateTicketForTeamlead() {
        projectService.addUser(requester, project.getId(), otherUser.id(), Role.Teamlead);

        var ticket = ticketService.create(otherUser, project.getId(), milestone.getId(), "test ticket");

        assertThat(ticket).extracting(Ticket::getName).isEqualTo("test ticket");
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = { "Developer", "QA" })
    void shouldNotAllowCreateTicketForOthers(Role role) {
        projectService.addUser(requester, project.getId(), otherUser.id(), role);

        assertThrows(NoAccessException.class, () -> {
            ticketService.create(otherUser, project.getId(), milestone.getId(), "test ticket");
        });
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = { "Developer", "Teamlead" })
    void shouldUpdateTicketStatus(Role role) {
        var ticket = ticketService.create(requester, project.getId(), milestone.getId(), "test ticket");

        projectService.addUser(requester, project.getId(), otherUser.id(), role);

        ticketService.setTicketStatus(otherUser, ticket.getId(), Status.ToDo);
        ticketService.setTicketStatus(otherUser, ticket.getId(), Status.InProgress);
        ticketService.setTicketStatus(otherUser, ticket.getId(), Status.Done);
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = { "QA" })
    void shouldNotAllowUpdateTicketStatus(Role role) {
        var ticket = ticketService.create(requester, project.getId(), milestone.getId(), "test ticket");

        projectService.addUser(requester, project.getId(), otherUser.id(), role);

        assertThrows(NoAccessException.class, () -> {
            ticketService.setTicketStatus(otherUser, ticket.getId(), Status.ToDo);
        });
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = { "Developer", "Teamlead" })
    void shouldListTickets(Role role) {
        var ticket = ticketService.create(requester, project.getId(), milestone.getId(), "test ticket");

        projectService.addUser(requester, project.getId(), otherUser.id(), role);

        ticketService.assignTicket(requester, ticket.getId(), otherUser.id());

        var tickets = ticketService.listAssignedTickets(otherUser.id());

        assertThat(tickets).hasSize(1);
        assertThat(tickets).first().extracting(Ticket::getName).isEqualTo(ticket.getName());
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = { "Developer", "QA", "Teamlead" })
    void shouldGetTicketStatus(Role role) {
        var ticket = ticketService.create(requester, project.getId(), milestone.getId(), "test bugreport");

        projectService.addUser(requester, project.getId(), otherUser.id(), role);

        Status status = ticketService.getTicketStatus(otherUser, ticket.getId());

        assertThat(status).isEqualTo(Status.New);
    }
}
