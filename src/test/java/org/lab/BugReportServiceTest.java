package org.lab;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.lab.entities.BugReport;
import org.lab.entities.Project;
import org.lab.entities.Role;
import org.lab.entities.User;
import org.lab.entities.BugReport.Status;
import org.lab.services.AccessControlService;
import org.lab.services.BugReportService;
import org.lab.services.ProjectService;
import org.lab.services.UserService;
import org.lab.services.impl.AccessControlServiceImpl;
import org.lab.services.impl.BugReportServiceImpl;
import org.lab.services.impl.ProjectServiceImpl;
import org.lab.services.impl.UserServiceImpl;

public class BugReportServiceTest {
    static UserService userService;

    static User requester;
    static User qaUser;
    static User otherUser;

    AccessControlService accessControlService;

    BugReportService bugreportService;

    ProjectService projectService;

    Project project;

    @BeforeAll
    static void beforeAll() {
        userService = new UserServiceImpl();
        requester = userService.register("test user");
        otherUser = userService.register("other user");
        qaUser = userService.register("other user");
    }

    @BeforeEach
    void beforeEach() {
        projectService = new ProjectServiceImpl(userService);

        project = projectService.create(requester, "test project");

        projectService.addUser(requester, project.getId(), qaUser.id(), Role.QA);

        accessControlService = new AccessControlServiceImpl(projectService);

        bugreportService = new BugReportServiceImpl(projectService, accessControlService);
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = { "Developer", "QA", "Teamlead" })
    void shouldCreateBugreport(Role role) {
        projectService.addUser(requester, project.getId(), otherUser.id(), role);

        var bugReport = bugreportService.create(otherUser, project.getId(), "test bugreport");

        assertThat(bugReport).extracting(BugReport::getName).isEqualTo("test bugreport");
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = { "Developer", "QA", "Teamlead" })
    void shouldUpdateBugreportStatus(Role role) {
        var bugReport = bugreportService.create(qaUser, project.getId(), "test bugreport");

        projectService.addUser(requester, project.getId(), otherUser.id(), role);

        bugreportService.setBugReportStatus(otherUser, bugReport.getId(), Status.Fixed);

        bugreportService.setBugReportStatus(otherUser, bugReport.getId(), Status.Tested);

        bugreportService.setBugReportStatus(otherUser, bugReport.getId(), Status.Closed);
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = { "Developer", "QA", "Teamlead" })
    void shouldListBugreports(Role role) {
        var bugReport = bugreportService.create(qaUser, project.getId(), "test bugreport");

        projectService.addUser(requester, project.getId(), otherUser.id(), role);

        var bugReports = bugreportService.listByUser(otherUser.id());

        assertThat(bugReports).hasSize(1);
        assertThat(bugReports).first().extracting(BugReport::getName).isEqualTo(bugReport.getName());
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = { "Developer", "QA", "Teamlead" })
    void shouldGetBugreportStatus(Role role) {
        var bugReport = bugreportService.create(qaUser, project.getId(), "test bugreport");

        projectService.addUser(requester, project.getId(), otherUser.id(), role);

        Status status = bugreportService.getBugReportStatus(otherUser, bugReport.getId());

        assertThat(status).isEqualTo(Status.New);
    }
}
