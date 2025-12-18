package org.lab.services;

import java.util.List;
import java.util.UUID;

import org.lab.entities.BugReport;
import org.lab.entities.User;

public interface BugReportService {
    List<BugReport> listByUser(UUID userId);

    BugReport create(User user, UUID projectId, String name);

    BugReport.Status getBugReportStatus(User user, UUID reportId);

    void setBugReportStatus(User user, UUID reportId, BugReport.Status status);
}
