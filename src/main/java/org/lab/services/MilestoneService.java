package org.lab.services;

import java.util.UUID;

import org.lab.entities.Milestone;
import org.lab.entities.User;

public interface MilestoneService {
    void create(User user, Milestone milestone);

    void setMilestoneStatus(User user, UUID milestoneId, Milestone.Status status);
}
