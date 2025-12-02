package org.lab.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.lab.entities.BugReport;
import org.lab.entities.Role;
import org.lab.entities.BugReport.Status;
import org.lab.services.BugReportService;
import org.lab.services.ProjectService;

public class BugReportServiceImpl implements BugReportService {
    private final Map<UUID, BugReport> bugReports = new HashMap<>();

    private final ProjectService projectService;

    BugReportServiceImpl(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public List<BugReport> listByUser(UUID userId) {
        var projectUsers = projectService.listProjectsByUser(userId);

        var projectIds = projectUsers.stream()
                .filter(item -> item.getRole() == Role.Developer)
                .map(item -> item.getProjectId())
                .toList();

        return bugReports.values().stream()
                .filter(report -> projectIds.contains(report.getProjectId()))
                .toList();
    }

    @Override
    public void create(BugReport report) {
        bugReports.put(report.getId(), report);
    }

    @Override
    public Status getBugReportStatus(UUID reportId) {
        var bugReport = bugReports.get(reportId);
        if (bugReport == null) {
            throw new RuntimeException("not found");
        }

        return bugReport.getStatus();
    }

    @Override
    public void setBugReportStatus(UUID reportId, Status status) {
        var bugReport = bugReports.get(reportId);
        if (bugReport == null) {
            throw new RuntimeException("not found");
        }

        bugReport.setStatus(status);
    }
}
