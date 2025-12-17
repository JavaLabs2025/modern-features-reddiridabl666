package org.lab.services;

import java.util.UUID;

import org.lab.entities.Milestone;
import org.lab.entities.User;

public interface MilestoneService {
    Milestone create(User user, String name, UUID projectId);

    void setMilestoneStatus(User user, UUID milestoneId, Milestone.Status status);
}
