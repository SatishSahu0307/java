package com.bugtracker.dao;

import com.bugtracker.model.Project;
import java.util.List;

public interface ProjectDAO {
    boolean addProject(Project project);
    Project getProjectByID(int projectID);
    List<Project> getAllProjects();
    boolean updateProject(Project project);
    boolean deleteProject(int projectID);
}
