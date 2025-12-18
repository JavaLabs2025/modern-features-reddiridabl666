package org.lab.services.impl;

import static org.lab.entities.Action.CREATE;
import static org.lab.entities.Action.READ;
import static org.lab.entities.Action.UPDATE_STATUS;
import static org.lab.entities.EntityType.BUG_REPORT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.lab.entities.Action;
import org.lab.entities.BugReport;
import org.lab.entities.User;
import org.lab.exceptions.IllegalStatusException;
import org.lab.exceptions.NoAccessException;
import org.lab.exceptions.NotFoundException;
import org.lab.entities.BugReport.Status;
import org.lab.services.AccessControlService;
import org.lab.services.BugReportService;
import org.lab.services.ProjectService;

public class BugReportServiceImpl implements BugReportService {
    private static final Map<Status, Status> ALLOWED_STATUS_CHANGE_MAP = Map.of(
            Status.New, Status.Fixed,
            Status.Fixed, Status.Tested,
            Status.Tested, Status.Closed,
            Status.Closed, Status.New);

    private final Map<UUID, BugReport> bugReports = new HashMap<>();

    private final ProjectService projectService;

    private final AccessControlService accessControlService;

    public BugReportServiceImpl(ProjectService projectService, AccessControlService accessControlService) {
        this.projectService = projectService;
        this.accessControlService = accessControlService;
    }

    @Override
    public List<BugReport> listByUser(UUID userId) {
        var projectUsers = projectService.listProjectsByUser(userId);

        var projectIds = projectUsers.stream()
                .filter(item -> item.userId().equals(userId))
                .map(item -> item.projectId())
                .toList();

        return bugReports.values().stream()
                .filter(report -> projectIds.contains(report.getProjectId()))
                .toList();
    }

    @Override
    public BugReport create(User user, UUID projectId, String name) {
        checkAccess(user.getId(), projectId, CREATE);

        var bugReport = new BugReport(UUID.randomUUID(), projectId, name, Status.New);

        bugReports.put(bugReport.getId(), bugReport);

        return bugReport;
    }

    @Override
    public Status getBugReportStatus(User user, UUID reportId) {
        var bugReport = bugReports.get(reportId);
        if (bugReport == null) {
            throw new NotFoundException();
        }

        checkAccess(user.getId(), bugReport.getProjectId(), READ);

        return bugReport.getStatus();
    }

    @Override
    public void setBugReportStatus(User user, UUID reportId, Status status) {
        var bugReport = bugReports.get(reportId);
        if (bugReport == null) {
            throw new NotFoundException();
        }

        checkAccess(user.getId(), bugReport.getProjectId(), UPDATE_STATUS);

        Status allowedStatus = ALLOWED_STATUS_CHANGE_MAP.get(bugReport.getStatus());
        if (!allowedStatus.equals(status)) {
            throw new IllegalStatusException(BUG_REPORT, allowedStatus, bugReport.getStatus());
        }

        bugReport.setStatus(status);
    }

    private void checkAccess(UUID userId, UUID projectID, Action action) {
        if (!accessControlService.hasAccess(projectID, userId, BUG_REPORT, action)) {
            throw new NoAccessException(BUG_REPORT, action, projectID);
        }
    }
}
