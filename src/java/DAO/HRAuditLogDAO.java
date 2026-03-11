package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HRAuditLogDAO {

    public void insert(Connection conn,
                       int employeeId,
                       String action,
                       int performedBy) throws SQLException {

        String sql = """
            INSERT INTO HRAuditLogs
            (EmployeeID, Action, PerformedBy)
            VALUES (?, ?, ?);
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setString(2, action);
            ps.setInt(3, performedBy);
            ps.executeUpdate();
        }
    }
}