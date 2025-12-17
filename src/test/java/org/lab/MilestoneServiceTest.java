package org.lab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.lab.entities.Milestone;
import org.lab.entities.Project;
import org.lab.entities.Role;
import org.lab.entities.User;
import org.lab.entities.Milestone.Status;
import org.lab.exceptions.NoAccessException;
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

public class MilestoneServiceTest {
    static UserService userService;

    static User requester;
    static User otherUser;

    AccessControlService accessControlService;

    MilestoneService milestoneService;

    ProjectService projectService;

    TicketService ticketService;

    Project project;

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
    }

    @Test
    void shouldCreateMilestone() {
        Milestone milestone = milestoneService.create(requester, "test milestone", project.getId());

        assertThat(milestone)
                .extracting(Milestone::getName)
                .isEqualTo("test milestone");
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = { "Developer", "QA", "Teamlead" })
    void shouldNotAllowMilestoneCreation(Role role) {
        projectService.addUser(requester, project.getId(), otherUser.getId(), role);

        assertThrows(NoAccessException.class, () -> {
            milestoneService.create(otherUser, "test milestone", project.getId());
        });
    }

    @Test
    void shouldChangeMilestoneStatus() {
        Milestone milestone = milestoneService.create(requester, "test milestone", project.getId());

        milestoneService.setMilestoneStatus(requester, milestone.getId(), Status.Active);

        milestoneService.setMilestoneStatus(requester, milestone.getId(), Status.Closed);
    }

    @Test
    void shouldNotAllowTwoActiveMilestones() {
        Milestone milestone = milestoneService.create(requester, "test milestone", project.getId());

        milestoneService.setMilestoneStatus(requester, milestone.getId(), Status.Active);

        Milestone secondMilestone = milestoneService.create(requester, "test milestone 2", project.getId());

        assertThrows(IllegalArgumentException.class, () -> {
            milestoneService.setMilestoneStatus(requester, secondMilestone.getId(), Status.Active);
        });
    }

    @Test
    void shouldNotAllowToCloseMilestoneWithOpenTickets() {
        Milestone milestone = milestoneService.create(requester, "test milestone", project.getId());

        milestoneService.setMilestoneStatus(requester, milestone.getId(), Status.Active);

        ticketService.create(requester, project.getId(), milestone.getId(), "test ticket");

        assertThrows(IllegalArgumentException.class, () -> {
            milestoneService.setMilestoneStatus(requester, milestone.getId(), Status.Closed);
        });
    }
}
