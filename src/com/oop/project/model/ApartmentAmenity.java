package com.oop.project.model;

/**
 * Plain Old Java Object for the 'apartmentAmenities' junction table.
 * Represents the many-to-many relationship between apartments and amenities.
 * Does not implement POJO because of the composite primary key.
 */
public class ApartmentAmenity {
    private final int apartmentId;
    private final int amenityId;

    public ApartmentAmenity(int apartmentId, int amenityId) {
        this.apartmentId = apartmentId;
        this.amenityId = amenityId;
    }

    public int getApartmentId() {
        return apartmentId;
    }

    public int getAmenityId() {
        return amenityId;
    }

    @Override
    public String toString() {
        return "ApartmentAmenity(apartment=" + apartmentId + ", amenity=" + amenityId + ")";
    }
}