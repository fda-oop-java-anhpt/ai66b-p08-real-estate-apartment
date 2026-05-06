package com.oop.project.model;

public class CategoryProportion implements DTO {
    private final String category;
    private final int count;

    public CategoryProportion(String category, int count) {
        this.category = category;
        this.count = count;
    }

    public String getCategory() { return category; }
    public int getCount() { return count; }
}