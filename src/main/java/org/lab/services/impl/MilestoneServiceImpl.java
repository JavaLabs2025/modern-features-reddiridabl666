package org.lab.services.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.lab.entities.Milestone;
import org.lab.entities.Milestone.Status;
import org.lab.services.MilestoneService;

public class MilestoneServiceImpl implements MilestoneService {
    private final Map<UUID, Milestone> milestones = new HashMap<>();

    @Override
    public void create(Milestone milestone) {
        milestones.put(milestone.getId(), milestone);
    }

    @Override
    public void setMilestoneStatus(UUID milestoneId, Status status) {
        var project = milestones.get(milestoneId);
        if (project == null) {
            throw new RuntimeException("not found");
        }

        project.setStatus(status);
    }
}
