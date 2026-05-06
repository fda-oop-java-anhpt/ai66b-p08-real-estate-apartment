package com.oop.project.service;

import com.oop.project.model.CategoryProportion;
import com.oop.project.model.CityStats;
import com.oop.project.model.OverallStats;
import com.oop.project.repository.DashboardRepository;
import com.oop.project.util.SessionManager;

import java.sql.SQLException;
import java.util.List;

public class DashboardService {
    private final DashboardRepository repository = new DashboardRepository();

    private void requireLogin() throws SecurityException {
        if (!SessionManager.isLoggedIn()) {
            throw new SecurityException("You must be logged in.");
        }
    }

    public List<CityStats> getCityStats() throws SQLException, SecurityException {
        requireLogin();
        return repository.getCityStats();
    }

    public List<CategoryProportion> getGlobalCategoryProportions() throws SQLException, SecurityException {
        requireLogin();
        return repository.getGlobalCategoryProportions();
    }

    public List<CategoryProportion> getCityCategoryProportions(String city) throws SQLException, SecurityException {
        requireLogin();
        return repository.getCityCategoryProportions(city);
    }
    
    public OverallStats getOverallStats() throws SQLException, SecurityException {
        requireLogin();
        return repository.getOverallStats();
    }
}