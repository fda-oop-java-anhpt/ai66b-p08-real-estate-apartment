package com.oop.project.service;

import com.oop.project.model.Apartment;
import com.oop.project.repository.FavouriteRepository;
import com.oop.project.util.SessionManager;

import java.sql.SQLException;
import java.util.List;

public class FavouriteService {
    private final FavouriteRepository repository = new FavouriteRepository();

    private void requireLogin() throws SecurityException {
        if (!SessionManager.isLoggedIn()) {
            throw new SecurityException("You must be logged in.");
        }
    }

    public List<Integer> getMyFavoriteIds() throws SQLException, SecurityException {
        requireLogin();
        String username = SessionManager.getCurrentUsername();
        return repository.getFavoriteApartmentIdsForUser(username);
    }

    public boolean isFavorited(int apartmentId) throws SQLException, SecurityException {
        requireLogin();
        return repository.isFavorited(apartmentId);
    }

    // Returns true if now favorited, false if now unfavorited
    public boolean toggle(int apartmentId) throws SQLException, SecurityException {
        requireLogin();
        return repository.toggle(apartmentId);
    }

    public List<Apartment> getMyFavorites() throws SQLException, SecurityException {
        requireLogin();
        return repository.getMyFavorites();
    }
}
