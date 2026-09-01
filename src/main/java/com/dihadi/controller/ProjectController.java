package com.dihadi.controller;

import java.util.List;
import com.dihadi.dao.ProjectDao;
import com.dihadi.model.Project;

public class ProjectController {
    private ProjectDao dao = new ProjectDao();

    public void addProject(Project project) {
        dao.saveProject(project);
    }

    public Project getProject(String projectId) {
        return dao.getProject(projectId);
    }

    public List<Project> getAllProjects() {
        return dao.getAllProjects();
    }

    public boolean deleteProject(String projectId) {
        return dao.deleteProject(projectId);
    }
}
