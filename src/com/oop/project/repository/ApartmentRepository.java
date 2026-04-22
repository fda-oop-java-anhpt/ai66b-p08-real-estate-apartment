package com.oop.project.repository;

import com.oop.project.db.DBConnection;
import com.oop.project.model.Apartment;
import java.sql.*;

public class ApartmentRepository {

    // Gọi Procedure sp_insert_apartment
    public void insert(Apartment apt) throws SQLException {
        String sql = "{CALL sp_insert_apartment(?, ?, ?, ?, ?)}";
        try (Connection conn = DBConnection.establish();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, apt.getAddress());
            cstmt.setString(2, apt.getCity());
            cstmt.setDouble(3, apt.getPrice());
            cstmt.setInt(4, apt.getBedrooms());
            cstmt.setDouble(5, apt.getSize());
            cstmt.execute();
        }
    }

    // Gọi Procedure sp_update_apartment
    public void update(Apartment apt) throws SQLException {
        String sql = "{CALL sp_update_apartment(?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DBConnection.establish();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, apt.getId());
            cstmt.setString(2, apt.getAddress());
            cstmt.setString(3, apt.getCity());
            cstmt.setDouble(4, apt.getPrice());
            cstmt.setInt(5, apt.getBedrooms());
            cstmt.setDouble(6, apt.getSize());
            cstmt.execute();
        }
    }

    // Gọi Procedure sp_delete_apartment
    public void delete(int id) throws SQLException {
        String sql = "{CALL sp_delete_apartment(?)}";
        try (Connection conn = DBConnection.establish();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, id);
            cstmt.execute();
        }
    }
}