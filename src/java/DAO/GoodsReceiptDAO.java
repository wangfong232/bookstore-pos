/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import entity.GoodsReceipt;
import entity.GoodsReceiptDetail;
import entity.PurchaseOrder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.time.LocalDateTime;

/**
 *
 * @author qp
 */
public class GoodsReceiptDAO extends DBContext {

    PreparedStatement stm;
    ResultSet rs;
    Connection connection;

    public List<GoodsReceipt> searchGRsWithPaginated(String keyword, String status, LocalDate from, LocalDate to, int page, int pageSize) {
        List<GoodsReceipt> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        StringBuilder sql = new StringBuilder();
        sql.append("""
                   select gr.*, po.PONumber, s.SupplierName, e.FullName AS ReceivedByName
                   from GoodsReceipts gr
                   join PurchaseOrders po on gr.POID = po.POID
                   join Suppliers s on s.SupplierID = s.SupplierID
                   join Employees e on gr.ReceivedBy = e.EmployeeID
                   where 1 = 1 
                   """);
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (gr.ReceiptNumber like ? OR po.PONumber like ? or s.SupplierName like ?) ");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" and gr.Status = ? ");
        }
        if (from != null) {
            sql.append(" and CAST(gr.ReceiptDate AS DATE) >= ?");
        }
        if (to != null) {
            sql.append(" and CAST(gr.ReceiptDate AS DATE) <= ?");
        }

        sql.append(" order by gr.ReceiptID desc ");
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");

        try {
            connection = getConnection();
            stm = connection.prepareStatement(sql.toString());
            int index = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                String p = "%" + keyword + "%";
                stm.setString(index++, p);
                stm.setString(index++, p);
                stm.setString(index++, p);
            }
            if (status != null && !status.trim().isEmpty()) {
                stm.setString(index++, status);
            }
            if (from != null) {
                stm.setDate(index++, Date.valueOf(from));
            }

            if (to != null) {
                stm.setDate(index++, Date.valueOf(to));
            }
            stm.setInt(index++, offset);
            stm.setInt(index++, pageSize);

            rs = stm.executeQuery();
            while (rs.next()) {
                list.add(extractGRFromResultSet(rs));
            }
        } catch (Exception e) {
            System.out.println("ERR: searchGRsWithPaginated: " + e.getMessage());
        }
        return list;
    }

    public int countGRs(String keyword, String status, LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder();
        sql.append("""
                select COUNT(*) from GoodsReceipts gr
                join PurchaseOrders po on gr.POID = po.POID
                join Suppliers s on po.SupplierID = s.SupplierID
                where 1 = 1 
                """);

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" and (gr.ReceiptNumber LIKE ? OR po.PONumber LIKE ? OR s.SupplierName LIKE ?) ");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" and gr.Status = ? ");
        }
        if (from != null) {
            sql.append(" and CAST(gr.ReceiptDate AS DATE) >= ? ");
        }
        if (to != null) {
            sql.append(" and CAST(gr.ReceiptDate AS DATE) <= ? ");
        }

        try {
            connection = getConnection();
            stm = connection.prepareStatement(sql.toString());
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String p = "%" + keyword + "%";
                stm.setString(idx++, p);
                stm.setString(idx++, p);
                stm.setString(idx++, p);
            }
            if (status != null && !status.trim().isEmpty()) {
                stm.setString(idx++, status);
            }
            if (from != null) {
                stm.setDate(idx++, java.sql.Date.valueOf(from));
            }
            if (to != null) {
                stm.setDate(idx++, java.sql.Date.valueOf(to));
            }

            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("ERR: countGRs: " + e.getMessage());
        }
        return 0;
    }

    public GoodsReceipt getGRByNumber(String receiptNumber) {
        String sql = """
                select gr.*,
                       po.PONumber, s.SupplierName, e.FullName AS ReceivedByName
                from GoodsReceipts gr
                join PurchaseOrders po ON gr.POID = po.POID
                join Suppliers s ON po.SupplierID = s.SupplierID
                join Employees e ON gr.ReceivedBy = e.EmployeeID
                where gr.ReceiptNumber = ?
                """;
        try {
            connection = getConnection();
            stm = connection.prepareStatement(sql);
            stm.setString(1, receiptNumber);
            rs = stm.executeQuery();
            if (rs.next()) {
                return extractGRFromResultSet(rs);
            }
        } catch (Exception e) {
            System.out.println("ERR: getGRByNumber: " + e.getMessage());
        }
        return null;
    }

    public List<GoodsReceiptDetail> getGRDetailsByReceiptId(long receiptId) {
        List<GoodsReceiptDetail> list = new ArrayList<>();
        String sql = """
                     select grd.*, p.ProductName, poi.QuantityOrdered
                     from GoodsReceiptDetails grd
                     join Products p on grd.ProductID = p.ProductID
                     join PurchaseOrderItems poi on grd.POLineItemID = poi.LineItemID
                     where grd.ReceiptID = ?
                     order by grd.ReceiptDetailID
                     """;
        try {
            connection = getConnection();
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                list.add(extractGRDetailFromRS(rs));
            }
        } catch (Exception e) {
            System.out.println("ERR: getGRDetailsByReceiptId: " + e.getMessage());
        }
        return list;
    }

    //get PO List for DropDown 
    public List<PurchaseOrder> getApprovedPOs() {
        List<PurchaseOrder> list = new ArrayList<>();
        String sql = """
                     select po.POID, po.PONumber, s.SupplierName, po.Status
                     from PurchaseOrders po
                     join Suppliers s on po.SupplierID = s.SupplierID
                     where po.Status in ('APPROVED', 'PARTIAL_RECEIVED')
                     order by po.PONumber
                     """;
        try {
            connection = getConnection();
            stm = connection.prepareStatement(sql);
            while (rs.next()) {
                PurchaseOrder po = new PurchaseOrder();
                po.setId(rs.getLong("POID"));
                po.setPoNumber(rs.getString("PONumber"));
                po.setSupplierName(rs.getString("SupplierName"));
                po.setStatus(rs.getString("Status"));
                list.add(po);
            }
        } catch (Exception e) {
            System.out.println("ERR: getApprovedPOs: " + e.getMessage());
        }
        return list;
    }

    //PO items as GoodsReceiptDetail 
    //lay tensp, so luong order, so luong nhan, gia nhap 
    public List<GoodsReceiptDetail> getPoItemsForGR(long poId) {
        List<GoodsReceiptDetail> list = new ArrayList<>();
        String sql = """
                     select poi.LineItemID, poi.ProductID, poi.QuantityOrdered, poi.QuantityReceived,
                     poi,UnitPrice, p.ProductName
                     from PurchaseOrderItems poi
                     join Products p on poi.ProductID = p.ProductID
                     where poi.POID = ?
                     order by poi.LineItemID
                     """;
        try {
            connection = getConnection();
            stm = connection.prepareStatement(sql);
            stm.setLong(1, poId);
            rs = stm.executeQuery();
            while (rs.next()) {
                GoodsReceiptDetail d = new GoodsReceiptDetail();
                d.setPoLineItemId(rs.getLong("LineItemID"));
                d.setProductId(rs.getInt("ProductID"));
                d.setProductName(rs.getString("ProductName"));
                int ordered = rs.getInt("QuantityOrdered");
                int alreadyReceived = rs.getInt("QuantityReceived");
                int remaining = ordered - alreadyReceived;
                
                d.setQuantityOrdered(ordered);
                d.setQuantityReceived(alreadyReceived);
            }
        } catch (Exception e) {
        }
        return list;
    }

    private GoodsReceipt extractGRFromResultSet(ResultSet rs) throws SQLException {
        GoodsReceipt gr = new GoodsReceipt();
        gr.setId(rs.getLong("ReceiptID"));
        gr.setReceiptNumber(rs.getString("ReceiptNumber"));
        gr.setPoId(rs.getLong("POID"));
        gr.setStatus(rs.getString("Status"));
        gr.setTotalQuantity(rs.getInt("TotalQuantity"));
        gr.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        gr.setNotes(rs.getString("Notes"));
        gr.setReceivedBy(rs.getInt("ReceivedBy"));

        gr.setReceiptDate(getLocalDateTime(rs, "receiptDate"));

        gr.setCreatedAt(getLocalDateTime(rs, "createdAt"));

        gr.setCompletedAt(getLocalDateTime(rs, "completedAt"));

        try {
            gr.setPoNumber(rs.getString("PONumber"));
        } catch (Exception e) {
        }
        try {
            gr.setSupplierName(rs.getString("SupplierName"));
        } catch (Exception e) {
        }
        try {
            gr.setReceivedByName(rs.getString("ReceivedByName"));
        } catch (Exception e) {
        }
        return gr;
    }

    private LocalDateTime getLocalDateTime(ResultSet rs, String columnName) throws SQLException {
        Timestamp ts = rs.getTimestamp(columnName);
        return (ts != null) ? ts.toLocalDateTime() : null;
    }

    private GoodsReceiptDetail extractGRDetailFromRS(ResultSet rs) throws Exception {
        GoodsReceiptDetail d = new GoodsReceiptDetail();
        d.setId(rs.getLong("ReceiptDetailID"));
        d.setPoLineItemId(rs.getLong("POLineItemID"));
        d.setProductId(rs.getInt("ProductID"));
        d.setQuantityReceived(rs.getInt("QuantityReceived"));
        d.setUnitCost(rs.getBigDecimal("UnitCost"));
        d.setLineTotal(rs.getBigDecimal("LineTotal"));
        d.setOldQty(rs.getInt("OldQty"));
        d.setOldCost(rs.getBigDecimal("OldCost"));
        d.setNewAvgCost(rs.getBigDecimal("NewAvgCost"));
        d.setNotes(rs.getString("Notes"));
        try {
            d.setProductName(rs.getString("ProductName"));
        } catch (Exception e) {
        }
        try {
            d.setQuantityOrdered(rs.getInt("QuantityOrdered"));
        } catch (Exception e) {
        }
        return d;
    }
}
