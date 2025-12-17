package org.lab.services;

import java.util.List;
import java.util.UUID;

import org.lab.entities.BugReport;
import org.lab.entities.User;

public interface BugReportService {
    List<BugReport> listByUser(UUID userId);

    void create(User user, BugReport report);

    BugReport.Status getBugReportStatus(User user, UUID reportId);

    void setBugReportStatus(User user, UUID reportId, BugReport.Status status);
}
