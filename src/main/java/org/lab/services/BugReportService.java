package org.lab.services;

import java.util.List;
import java.util.UUID;

import org.lab.entities.BugReport;

public interface BugReportService {
    List<BugReport> listByUser(UUID userId);

    void create(BugReport report);

    BugReport.Status getBugReportStatus(UUID reportId);

    void setBugReportStatus(UUID reportId, BugReport.Status status);
}
