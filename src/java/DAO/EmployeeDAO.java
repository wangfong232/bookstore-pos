package DAO;

import entity.Employee;
import entity.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EmployeeDAO extends DBContext {

    // ==========================
    // HASH PASSWORD USING SHA-256
    // ==========================
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // ==========================
    // INSERT EMPLOYEE

    public boolean insertEmployee(Employee e, int performedBy) {

        String insertSql = """
            INSERT INTO Employees
            (FullName, Email, Phone, RoleID, HireDate, Status)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        String auditSql = """
            INSERT INTO HRAuditLogs
            (EmployeeID, Action, PerformedBy)
            VALUES (?, 'CREATE', ?)
        """;

        try (Connection conn = getConnection()) {

            conn.setAutoCommit(false);

            int newId;

            // 1️⃣ Insert employee
            try (PreparedStatement ps
                    = conn.prepareStatement(insertSql,
                            Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, e.getFullName());
                ps.setString(2, e.getEmail());
                ps.setString(3, e.getPhone());
                ps.setInt(4, e.getRole().getRoleId());

                if (e.getHireDate() != null) {
                    ps.setDate(5, e.getHireDate());
                } else {
                    ps.setNull(5, Types.DATE);
                }

                ps.setString(6, "ACTIVE");

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) {
                    conn.rollback();
                    return false;
                }

                newId = rs.getInt(1);
            }

            // 2️⃣ Insert audit log
            try (PreparedStatement ps = conn.prepareStatement(auditSql)) {
                ps.setInt(1, newId);
                ps.setInt(2, performedBy);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    // ==========================
    // UPDATE EMPLOYEE
    // ==========================
    public boolean updateEmployee(Employee e, int performedBy) {

        String updateSql = """
            UPDATE Employees
            SET FullName=?, Email=?, Phone=?,
                RoleID=?, HireDate=?, Status=?
            WHERE EmployeeID=?
        """;

        String auditSql = """
            INSERT INTO HRAuditLogs
            (EmployeeID, Action, PerformedBy)
            VALUES (?, 'UPDATE', ?)
        """;

        try (Connection conn = getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {

                ps.setString(1, e.getFullName());
                ps.setString(2, e.getEmail());
                ps.setString(3, e.getPhone());
                ps.setInt(4, e.getRole().getRoleId());

                if (e.getHireDate() != null) {
                    ps.setDate(5, e.getHireDate());
                } else {
                    ps.setNull(5, Types.DATE);
                }

                ps.setString(6, e.getStatus());
                ps.setInt(7, e.getEmployeeId());

                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(auditSql)) {
                ps.setInt(1, e.getEmployeeId());
                ps.setInt(2, performedBy);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    // ==========================
    // DEACTIVATE
    // ==========================
    public boolean deactivateEmployee(int id, int performedBy) {

        String sql = """
            UPDATE Employees
            SET Status='INACTIVE'
            WHERE EmployeeID=?
        """;

        String auditSql = """
            INSERT INTO HRAuditLogs
            (EmployeeID, Action, PerformedBy)
            VALUES (?, 'DEACTIVATE', ?)
        """;

        try (Connection conn = getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(auditSql)) {
                ps.setInt(1, id);
                ps.setInt(2, performedBy);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    // ==========================
// DELETE EMPLOYEE (HARD DELETE)
// ==========================
    public boolean deleteEmployee(int id, int performedBy) {

        String deleteAttendanceSql = """
            DELETE a FROM Attendance a
            JOIN EmployeeShiftAssignments esa ON a.AssignmentID = esa.AssignmentID
            WHERE esa.EmployeeID = ?
        """;

        String deleteAssignmentsSql = "DELETE FROM EmployeeShiftAssignments WHERE EmployeeID = ?";
        
        String deleteAuditLogsAsSubjectSql = "DELETE FROM HRAuditLogs WHERE EmployeeID = ?";
        
        String updateAuditLogsAsPerformerSql = "UPDATE HRAuditLogs SET PerformedBy = NULL WHERE PerformedBy = ?";

        String deleteEmployeeSql = "DELETE FROM Employees WHERE EmployeeID = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. Delete Attendance
                try (PreparedStatement ps = conn.prepareStatement(deleteAttendanceSql)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                // 2. Delete Assignments
                try (PreparedStatement ps = conn.prepareStatement(deleteAssignmentsSql)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                // 3. Delete Audit Logs where this employee is the subject
                try (PreparedStatement ps = conn.prepareStatement(deleteAuditLogsAsSubjectSql)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                // 4. Update Audit Logs where this employee is the performer (set to NULL)
                try (PreparedStatement ps = conn.prepareStatement(updateAuditLogsAsPerformerSql)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                // 5. Delete the Employee record
                int rowsDeleted = 0;
                try (PreparedStatement ps = conn.prepareStatement(deleteEmployeeSql)) {
                    ps.setInt(1, id);
                    rowsDeleted = ps.executeUpdate();
                }

                if (rowsDeleted > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }

            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==========================
    // TOGGLE STATUS
    // ==========================
    public boolean toggleStatus(int id, int performedBy) {

        Employee e = getEmployeeByID(id);
        if (e == null) {
            return false;
        }

        String newStatus
                = e.getStatus().equalsIgnoreCase("ACTIVE")
                ? "INACTIVE"
                : "ACTIVE";

        String updateSql = """
            UPDATE Employees
            SET Status=?
            WHERE EmployeeID=?
        """;

        String auditSql = """
            INSERT INTO HRAuditLogs
            (EmployeeID, Action, PerformedBy)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, newStatus);
                ps.setInt(2, id);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(auditSql)) {
                ps.setInt(1, id);
                ps.setString(2,
                        newStatus.equals("ACTIVE")
                        ? "ACTIVATE"
                        : "DEACTIVATE");
                ps.setInt(3, performedBy);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    // ==========================
    // GET BY ID
    // ==========================
    public Employee getEmployeeByID(int id) {

        String sql = """
            SELECT e.*, r.RoleName
            FROM Employees e
            JOIN Roles r ON e.RoleID = r.RoleID
            WHERE e.EmployeeID = ?
        """;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Employee e = new Employee();
                e.setEmployeeId(rs.getInt("EmployeeID"));
                e.setFullName(rs.getString("FullName"));
                e.setEmail(rs.getString("Email"));
                e.setPhone(rs.getString("Phone"));
                e.setHireDate(rs.getDate("HireDate"));
                e.setStatus(rs.getString("Status"));

                Role r = new Role();
                r.setRoleId(rs.getInt("RoleID"));
                r.setRoleName(rs.getString("RoleName"));
                e.setRole(r);

                return e;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    // ==========================
    // LIST WITH FILTER + PAGING
    // ==========================
    public List<Employee> getEmployees(
            String search,
            Integer roleId,
            String status,
            int page,
            int pageSize) {

        List<Employee> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT e.*, r.RoleName
            FROM Employees e
            JOIN Roles r ON e.RoleID = r.RoleID
            WHERE 1=1
        """);

        if (search != null && !search.isEmpty()) {
            sql.append(" AND (e.FullName LIKE ? OR e.Email LIKE ?)");
        }

        if (roleId != null) {
            sql.append(" AND e.RoleID = ?");
        }

        if (status != null && !status.isEmpty()) {
            sql.append(" AND e.Status = ?");
        }

        sql.append(" ORDER BY e.EmployeeID DESC ");
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = getConnection(); PreparedStatement ps
                = conn.prepareStatement(sql.toString())) {

            int index = 1;

            if (search != null && !search.isEmpty()) {
                String keyword = "%" + search + "%";
                ps.setString(index++, keyword);
                ps.setString(index++, keyword);
            }

            if (roleId != null) {
                ps.setInt(index++, roleId);
            }

            if (status != null && !status.trim().isEmpty()) {
                ps.setString(index++, status.trim());
            }

            ps.setInt(index++, (page - 1) * pageSize);
            ps.setInt(index++, pageSize);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Employee e = new Employee();
                e.setEmployeeId(rs.getInt("EmployeeID"));
                e.setFullName(rs.getString("FullName"));
                e.setEmail(rs.getString("Email"));
                e.setPhone(rs.getString("Phone"));
                e.setHireDate(rs.getDate("HireDate"));
                e.setStatus(rs.getString("Status"));

                Role r = new Role();
                r.setRoleId(rs.getInt("RoleID"));
                r.setRoleName(rs.getString("RoleName"));
                e.setRole(r);

                list.add(e);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        System.out.println("SIZE: " + list.size());

        return list;
    }

    // ==========================
    // COUNT FOR PAGINATION
    // ==========================
    public int getTotalEmployees(
            String search,
            Integer roleId,
            String status) {

        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*)
            FROM Employees
            WHERE 1=1
        """);

        if (search != null && !search.isEmpty()) {
            sql.append(" AND (FullName LIKE ? OR Email LIKE ?)");
        }

        if (roleId != null) {
            sql.append(" AND RoleID = ?");
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND Status = ?");
        }

        try (Connection conn = getConnection(); PreparedStatement ps
                = conn.prepareStatement(sql.toString())) {

            int index = 1;

            if (search != null && !search.isEmpty()) {
                String keyword = "%" + search + "%";
                ps.setString(index++, keyword);
                ps.setString(index++, keyword);
            }

            if (roleId != null) {
                ps.setInt(index++, roleId);
            }

            if (status != null && !status.isEmpty()) {
                ps.setString(index++, status);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    // ==========================
    // CHECK EMAIL EXISTS
    // ==========================
    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Employees WHERE Email = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // ==========================
    // REGISTER EMPLOYEE
    // ==========================
    public boolean registerEmployee(Employee e) {
        String sql = """
            INSERT INTO Employees
            (FullName, Email, Phone, RoleID, HireDate, Status, Password, CreatedAt)
            VALUES (?, ?, ?, ?, ?, 'PENDING', ?, GETDATE())
        """;
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getFullName());
            ps.setString(2, e.getEmail());
            ps.setString(3, e.getPhone());
            ps.setInt(4, e.getRole().getRoleId());
            if (e.getHireDate() != null) {
                ps.setDate(5, e.getHireDate());
            } else {
                ps.setNull(5, Types.DATE);
            }
            String hashedPassword = hashPassword(e.getPassword());
            ps.setString(6, hashedPassword);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // ==========================
    // GET BY EMAIL (for account status check)
    // ==========================
    public Employee getEmployeeByEmail(String email) {
        String sql = """
            SELECT e.*, r.RoleName
            FROM Employees e
            JOIN Roles r ON e.RoleID = r.RoleID
            WHERE e.Email = ?
        """;
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Employee e = new Employee();
                e.setEmployeeId(rs.getInt("EmployeeID"));
                e.setFullName(rs.getString("FullName"));
                e.setEmail(rs.getString("Email"));
                e.setPhone(rs.getString("Phone"));
                e.setHireDate(rs.getDate("HireDate"));
                e.setStatus(rs.getString("Status"));
                Role r = new Role();
                r.setRoleId(rs.getInt("RoleID"));
                r.setRoleName(rs.getString("RoleName"));
                e.setRole(r);
                return e;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // ==========================
    // LOGIN
    // ==========================
    public Employee login(String email, String password) {
        String sql = """
            SELECT e.*, r.RoleName
            FROM Employees e
            JOIN Roles r ON e.RoleID = r.RoleID
            WHERE e.Email = ? AND e.Status = 'ACTIVE'
        """;
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("Password");
                String inputHash = hashPassword(password);
                if (hashedPassword.equals(inputHash)) {
                    Employee e = new Employee();
                    e.setEmployeeId(rs.getInt("EmployeeID"));
                    e.setFullName(rs.getString("FullName"));
                    e.setEmail(rs.getString("Email"));
                    e.setPhone(rs.getString("Phone"));
                    e.setHireDate(rs.getDate("HireDate"));
                    e.setStatus(rs.getString("Status"));
                    Role r = new Role();
                    r.setRoleId(rs.getInt("RoleID"));
                    r.setRoleName(rs.getString("RoleName"));
                    e.setRole(r);
                    // Update LastLogin
                    updateLastLogin(e.getEmployeeId());
                    return e;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // ==========================
    // UPDATE LAST LOGIN
    // ==========================
    public boolean updateLastLogin(int employeeId) {
        String sql = "UPDATE Employees SET LastLogin = GETDATE() WHERE EmployeeID = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // ==========================
    // UPDATE PROFILE (self-service)
    // ==========================
    public boolean updateProfile(Employee e, int performedBy) {
        String sql = """
            UPDATE Employees
            SET FullName = ?, Email = ?, Phone = ?
            WHERE EmployeeID = ?
        """;
        
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getFullName());
            ps.setString(2, e.getEmail());
            ps.setString(3, e.getPhone());
            ps.setInt(4, e.getEmployeeId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // ==========================
    // UPDATE PASSWORD
    // ==========================
    public boolean updatePassword(int employeeId, String newPassword) {
        String sql = """
            UPDATE Employees
            SET Password = ?
            WHERE EmployeeID = ?
        """;
        
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String hashedPassword = hashPassword(newPassword);
            ps.setString(1, hashedPassword);
            ps.setInt(2, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // ==========================
    // VERIFY PASSWORD (for change password)
    // ==========================
    public boolean verifyPassword(int employeeId, String hashedPassword) {
        String sql = """
            SELECT COUNT(*)
            FROM Employees
            WHERE EmployeeID = ? AND Password = ?
        """;
        
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setString(2, hashedPassword);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
}
