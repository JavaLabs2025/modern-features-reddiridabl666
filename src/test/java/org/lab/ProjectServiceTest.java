package org.lab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lab.entities.Project;
import org.lab.entities.ProjectUser;
import org.lab.entities.Role;
import org.lab.entities.User;
import org.lab.exceptions.NoAccessException;
import org.lab.services.ProjectService;
import org.lab.services.UserService;
import org.lab.services.impl.ProjectServiceImpl;
import org.lab.services.impl.UserServiceImpl;

public class ProjectServiceTest {
    ProjectService projectService;
    UserService userService;

    User requester;
    User otherUser;

    Project createdProject;

    @BeforeEach
    void setup() {
        userService = new UserServiceImpl();
        projectService = new ProjectServiceImpl(userService);

        requester = userService.register("test user");

        createdProject = projectService.create(requester, "test project");

        userService.listUsers().get(0);

        otherUser = userService.register("second user");
    }

    @Test
    void shouldCreateProject() {
        var projectUser = new ProjectUser(createdProject.getId(), requester.id(), Role.Manager);

        var projectsByUser = projectService.listProjectsByUser(requester.id());

        assertThat(projectsByUser).hasSize(1);
        assertThat(projectsByUser).first().isEqualTo(projectUser);

        var usersByProject = projectService.listUsersByProject(createdProject.getId());

        assertThat(usersByProject).hasSize(1);
        assertThat(usersByProject).first().isEqualTo(projectUser);
    }

    @Test
    void shouldAddUser() {
        projectService.addUser(requester, createdProject.getId(), otherUser.id(), Role.Developer);

        var projectUsers = projectService.listUsersByProject(createdProject.getId());

        assertThat(projectUsers).hasSize(2);
        assertThat(projectUsers).contains(new ProjectUser(createdProject.getId(), otherUser.id(), Role.Developer));
    }

    @Test
    void shouldNotAddUserForNonManagers() {
        assertThrows(NoAccessException.class, () -> {
            projectService.addUser(otherUser, createdProject.getId(), otherUser.id(), Role.Developer);
        });
    }

    @Test
    void shouldNotAddManager() {
        assertThrows(IllegalArgumentException.class, () -> {
            projectService.addUser(requester, createdProject.getId(), otherUser.id(), Role.Manager);
        });
    }

    @Test
    void shouldNotAddTeamleadTwice() {
        projectService.addUser(requester, createdProject.getId(), otherUser.id(), Role.Teamlead);

        User thirdUser = userService.register("second user");

        assertThrows(IllegalArgumentException.class, () -> {
            projectService.addUser(requester, createdProject.getId(), thirdUser.id(), Role.Teamlead);
        });
    }
}
