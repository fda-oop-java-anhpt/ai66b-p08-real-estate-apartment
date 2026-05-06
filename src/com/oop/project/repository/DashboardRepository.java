package com.oop.project.repository;

import com.oop.project.db.DBConnection;
import com.oop.project.model.CityStats;
import com.oop.project.model.CategoryProportion;
import com.oop.project.model.OverallStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DashboardRepository implements DAO {

    public List<CityStats> getCityStats() throws SQLException {
        String sql = "SELECT city, COUNT(*) AS cnt, AVG(price) AS avg_price, AVG(size) AS avg_size " +
                     "FROM apartment GROUP BY city ORDER BY city";
        List<CityStats> list = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new CityStats(
                    rs.getString("city"),
                    rs.getInt("cnt"),
                    rs.getDouble("avg_price"),
                    rs.getDouble("avg_size")
                ));
            }
        }
        return list;
    }

    public List<CategoryProportion> getGlobalCategoryProportions() throws SQLException {
        String sql = "SELECT category, COUNT(*) AS cnt FROM apartment GROUP BY category ORDER BY category";
        List<CategoryProportion> list = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new CategoryProportion(rs.getString("category"), rs.getInt("cnt")));
            }
        }
        return list;
    }

    public List<CategoryProportion> getCityCategoryProportions(String city) throws SQLException {
        String sql = "SELECT category, COUNT(*) AS cnt FROM apartment WHERE city = ? GROUP BY category ORDER BY category";
        List<CategoryProportion> list = new ArrayList<>();
        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, city);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new CategoryProportion(rs.getString("category"), rs.getInt("cnt")));
                }
            }
        }
        return list;
    }
    
    public OverallStats getOverallStats() throws SQLException {
        String sql = "SELECT " +
                     "COUNT(*) AS total, " +
                     "SUM(CASE WHEN status = 'empty' THEN 1 ELSE 0 END) AS empty_count, " +
                     "SUM(CASE WHEN status = 'rented' THEN 1 ELSE 0 END) AS rented_count, " +
                     "AVG(price) AS avg_price, " +
                     "AVG(size) AS avg_size, " +
                     "AVG(CASE WHEN category = 'luxury' THEN price END) AS avg_lux_price, " +
                     "AVG(CASE WHEN category = 'standard' THEN price END) AS avg_std_price, " +
                     "AVG(CASE WHEN category = 'budget' THEN price END) AS avg_bud_price, " +
                     "AVG(CASE WHEN category = 'luxury' THEN size END) AS avg_lux_size, " +
                     "AVG(CASE WHEN category = 'standard' THEN size END) AS avg_std_size, " +
                     "AVG(CASE WHEN category = 'budget' THEN size END) AS avg_bud_size " +
                     "FROM apartment";

        try (Connection conn = new DBConnection().establish();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return new OverallStats(
                    rs.getInt("total"),
                    rs.getInt("empty_count"),
                    rs.getInt("rented_count"),
                    rs.getDouble("avg_price"),
                    rs.getDouble("avg_size"),
                    rs.getDouble("avg_lux_price"),
                    rs.getDouble("avg_std_price"),
                    rs.getDouble("avg_bud_price"),
                    rs.getDouble("avg_lux_size"),
                    rs.getDouble("avg_std_size"),
                    rs.getDouble("avg_bud_size")
                );
            }
        }
        return new OverallStats(0,0,0,0,0,0,0,0,0,0,0);
    }
}