// Note.java
package com.oop.project.model;

import java.sql.Timestamp;

/**
 * Plain Old Java Object for the 'notes' table.
 */
public class Note implements POJO {
    private final int noteId;
    private final String username;
    private final int apartmentId;
    private final String content;
    private final Timestamp createdAt;
    private final Timestamp updatedAt;

    public Note(int noteId, String username, int apartmentId, String content,
                Timestamp createdAt, Timestamp updatedAt) {
        this.noteId = noteId;
        this.username = username;
        this.apartmentId = apartmentId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public int getId() {
        return noteId;
    }

    public int getNoteId() {
        return noteId;
    }

    public String getUsername() {
        return username;
    }

    public int getApartmentId() {
        return apartmentId;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Note(" + noteId + ", " + username + ", apt " + apartmentId + ")";
    }
}