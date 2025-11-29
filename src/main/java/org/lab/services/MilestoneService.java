package org.lab.services;

import java.util.UUID;

import org.lab.entities.Milestone;

public interface MilestoneService {
    void create(Milestone milestone);

    void setMilestoneStatus(UUID milestoneId, Milestone.Status status);
}
