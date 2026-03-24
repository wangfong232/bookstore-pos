/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import entity.GoodsReceipt;
import entity.GoodsReceiptDetail;
import entity.PurchaseOrder;
import java.math.BigDecimal;
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

    public List<GoodsReceipt> searchGRsWithPaginated(String keyword, String status, LocalDate from, LocalDate to, int page, int pageSize) {
        List<GoodsReceipt> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        StringBuilder sql = new StringBuilder();
        sql.append("""
                   select gr.*, po.PONumber, s.SupplierName, e.FullName AS ReceivedByName
                   from GoodsReceipts gr
                   join PurchaseOrders po on gr.POID = po.POID
                   join Suppliers s on s.SupplierID = po.SupplierID
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

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql.toString())) {
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

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    list.add(extractGRFromResultSet(rs));
                }
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

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql.toString())) {
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

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
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
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, receiptNumber);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return extractGRFromResultSet(rs);
                }
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
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, receiptId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    list.add(extractGRDetailFromRS(rs));
                }
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
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
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
                     poi.UnitPrice, p.ProductName
                     from PurchaseOrderItems poi
                     join Products p on poi.ProductID = p.ProductID
                     where poi.POID = ?
                     order by poi.LineItemID
                     """;
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, poId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    GoodsReceiptDetail d = new GoodsReceiptDetail();
                    d.setPoLineItemId(rs.getLong("LineItemID"));
                    d.setProductId(rs.getInt("ProductID"));
                    d.setProductName(rs.getString("ProductName"));
                    int ordered = rs.getInt("QuantityOrdered");
                    int alreadyReceived = rs.getInt("QuantityReceived");
                    int remaining = ordered - alreadyReceived;

                    d.setQuantityOrdered(ordered);
                    d.setQuantityReceived(remaining > 0 ? remaining : 0);   //improve UX
                    d.setUnitCost(rs.getBigDecimal("UnitPrice"));
                    if (d.getUnitCost() != null && d.getQuantityReceived() != null && d.getQuantityReceived() > 0) {
                        d.calculateLineTotal();
                    } else {
                        d.setLineTotal(BigDecimal.ZERO);
                    }
                    list.add(d);
                }
            }
        } catch (Exception e) {
            System.out.println("ERR: getPoItemsForGR: " + e.getMessage());
        }
        return list;
    }

    public boolean isPoAvailableForGR(long poId) {
        String sql = "select COUNT(*) from PurchaseOrders where POID = ? and status in ('APPROVED','PARTIAL_RECEIVED')";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, poId);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.out.println("ERR isPoAvailableForGR: " + e.getMessage());
        }
        return false;
    }

    public boolean hasPendingGRForPO(long poId) {
        String sql = "select COUNT(*) from GoodsReceipts where POID = ? and Status = 'PENDING' ";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, poId);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.out.println("ERR hasPendingGRForPO: " + e.getMessage());
        }
        return false;
    }

    //add
    public boolean createGR(GoodsReceipt gr) {
        Connection connection = null;

        String sqlGR = """
                       insert into GoodsReceipts (ReceiptNumber, POID, ReceiptDate, Status, TotalQuantity, TotalAmount, Notes, ReceivedBy)
                       Values(?, ?, ?, 'PENDING', ?, ?, ?, ?) 
                       """;

        String sqlDetail = """
                           insert into GoodsReceiptDetails (ReceiptID, POLineItemID, ProductID, QuantityReceived, UnitCost, LineTotal, OldQty, OldCost, NewAvgCost, Notes)
                           values(?, ?, ?, ?, ?, ?, 0, 0, 0, ?)  
                           """;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            PreparedStatement stmGR = connection.prepareStatement(sqlGR, Statement.RETURN_GENERATED_KEYS);
            stmGR.setString(1, gr.getReceiptNumber());
            stmGR.setLong(2, gr.getPoId());

            Timestamp receiptDate = gr.getReceiptDate() != null
                    ? Timestamp.valueOf(gr.getReceiptDate())
                    : Timestamp.valueOf(LocalDateTime.now());
            stmGR.setTimestamp(3, receiptDate);
            stmGR.setInt(4, gr.getTotalQuantity() != null ? gr.getTotalQuantity() : 0);
            stmGR.setBigDecimal(5, gr.getTotalAmount() != null ? gr.getTotalAmount() : BigDecimal.ZERO);
            stmGR.setString(6, gr.getNotes());
            stmGR.setInt(7, gr.getReceivedBy());

            int rows = stmGR.executeUpdate();
            if (rows == 0) {
                connection.rollback();
                return false;
            }

            long receiptId = 0;
            ResultSet keys = stmGR.getGeneratedKeys();
            if (keys.next()) {
                receiptId = keys.getLong(1);
                gr.setId(receiptId);
            } else {
                connection.rollback();
                return false;
            }

            //insert details 
            PreparedStatement stmDet = connection.prepareStatement(sqlDetail);
            for (GoodsReceiptDetail d : gr.getDetails()) {
                stmDet.setLong(1, receiptId);
                stmDet.setLong(2, d.getPoLineItemId());
                stmDet.setInt(3, d.getProductId());
                stmDet.setInt(4, d.getQuantityReceived());
                stmDet.setBigDecimal(5, d.getUnitCost());
                stmDet.setBigDecimal(6, d.getLineTotal() != null ? d.getLineTotal() : BigDecimal.ZERO);
                stmDet.setString(7, d.getNotes());
                stmDet.addBatch();
            }
            stmDet.executeBatch();

            connection.commit();
            return true;

        } catch (Exception e) {
            System.out.println("ERR: createGR: " + e.getMessage());
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (Exception ex) {
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                }
            }
        }
        return false;
    }

    public boolean completeGR(String receiptNumber) {
        String sqlUpdateStock = """
                                update Products 
                                set Stock = Stock + ?, CostPrice = ?, UpdatedDate = GETDATE()
                                where ProductID = ?
                                """;

        String sqlInsertTx = """
                             insert into InventoryTransactions 
                             (ProductID, TransactionType, ReferenceType, ReferenceID, ReferenceCode,
                              QuantityChange, StockBefore, StockAfter, UnitCost, Notes, CreatedBy)
                             values (?, 'IN', 'GOODS_RECEIPT', ?, ?, ?, ?, ?, ?, ?, ?)
                             """;

        Connection connection = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            //load GR header
            GoodsReceipt gr = getGRByNumberInternal(connection, receiptNumber);
            if (gr == null || !GoodsReceipt.STATUS_PENDING.equals(gr.getStatus())) {
                connection.rollback();
                return false;
            }

            //load GR detail
            List<GoodsReceiptDetail> details = getGRDetailsInternal(connection, gr.getId());
            if (details.isEmpty()) {
                connection.rollback();
                return false;
            }

            try (PreparedStatement stmStock = connection.prepareStatement(sqlUpdateStock); PreparedStatement stmTx = connection.prepareStatement(sqlInsertTx)) {

                //for each detail, update product stock, cal MAC, and update the row with MAC snapshot
                for (GoodsReceiptDetail detail : details) {
                    int oldQty = getProductStock(connection, detail.getProductId());
                    BigDecimal oldCost = getProductCostPrice(connection, detail.getProductId());

                    detail.setMACSnapshot(oldQty, oldCost);
                    detail.calculateNewAvgCost();

                    int stockBefore = oldQty;
                    int stockAfter = oldQty + detail.getQuantityReceived();

                    stmStock.setInt(1, detail.getQuantityReceived());
                    stmStock.setBigDecimal(2, detail.getNewAvgCost());
                    stmStock.setInt(3, detail.getProductId());
                    stmStock.addBatch();
//                    int stockBefore = 0, stockAfter = 0;
//                    try (ResultSet out = stmStock.executeQuery()) {
//                        if (out.next()) {
//                            stockBefore = out.getInt("StockBefore");
//                            stockAfter = out.getInt("StockAfter");
//                        }
//                    }

                    stmTx.setInt(1, detail.getProductId());
                    stmTx.setLong(2, gr.getId());
                    stmTx.setString(3, receiptNumber);
                    stmTx.setInt(4, detail.getQuantityReceived());
                    stmTx.setInt(5, stockBefore);
                    stmTx.setInt(6, stockAfter);
                    stmTx.setBigDecimal(7, detail.getUnitCost());
                    String txNote = detail.getNotes();
                    if (txNote == null || txNote.trim().isEmpty()) {
                        txNote = gr.getNotes();
                    }
                    if (txNote == null || txNote.trim().isEmpty()) {
                        txNote = "Nhập kho theo ĐĐH " + (gr.getPoNumber() != null ? gr.getPoNumber() : "");
                    }

                    stmTx.setString(8, txNote);
                    stmTx.setInt(9, gr.getReceivedBy());
                    stmTx.addBatch();

                    updatePOItemReceived(connection, detail.getPoLineItemId(), detail.getQuantityReceived());
                    updateGRDetailMAC(connection, detail.getId(), detail.getOldQty(), detail.getOldCost(), detail.getNewAvgCost());
                }
                stmStock.executeBatch();
                stmTx.executeBatch();
            }

            String sqlCompleteGR = """
                                   update GoodsReceipts
                                   set Status='COMPLETED', CompletedAt = GETDATE()
                                   where ReceiptNumber = ?
                                   """;

            try (PreparedStatement stmGR = connection.prepareStatement(sqlCompleteGR)) {
                stmGR.setString(1, receiptNumber);
                stmGR.executeUpdate();
            }

            updatePOStatus(connection, gr.getPoId());

            connection.commit();
            return true;
        } catch (Exception e) {
            System.out.println("ERR: completeGR: " + e.getMessage());
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (Exception ex) {
            }
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (Exception ex) {
                }
            }
        }
        return false;
    }

    public boolean cancelGR(String receiptNumber) {
        String sqlDet = """
                        delete grd from GoodsReceiptDetails grd
                        join GoodsReceipts gr on grd.ReceiptID = gr.ReceiptID
                        where gr.ReceiptNumber = ? and gr.Status = 'PENDING'
                        """;
        String sqlGR = "delete from GoodsReceipts where ReceiptNumber = ? and Status = 'PENDING'";
        try (Connection connection = getConnection()) {
            try {
                connection.setAutoCommit(false);
                PreparedStatement stmDet = connection.prepareStatement(sqlDet);
                stmDet.setString(1, receiptNumber);
                stmDet.executeUpdate();

                PreparedStatement stmGR = connection.prepareStatement(sqlGR);
                stmGR.setString(1, receiptNumber);
                int rows = stmGR.executeUpdate();
                connection.commit();
                return rows > 0;
            } catch (Exception e) {
                connection.rollback();
                System.out.println("ERR cancelGR: " + e.getMessage());
                return false;
            }
        } catch (Exception e) {
            System.out.println("ERR cancelGR conn: " + e.getMessage());
            return false;
        }
    }

    public String generateNextGRNumber() {
        int currentYear = java.time.Year.now().getValue();
        String yearPrefix = "GR-" + currentYear + "-";
        String sql = """
                     select TOP 1 ReceiptNumber from GoodsReceipts where ReceiptNumber like ? order by ReceiptNumber desc
                     """;
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, yearPrefix + "%");
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    String last = rs.getString(1);
                    String numberPart = last.substring(last.lastIndexOf("-") + 1);
                    int next = Integer.parseInt(numberPart) + 1;
                    return String.format("GR-%d-%04d", currentYear, next);
                }
            }
        } catch (Exception e) {
            System.out.println("ERR: generateNextGRNumber: " + e.getMessage());
        }
        return String.format("GR-%d-%04d", currentYear, 1);
    }

    public boolean isReceiptNumberExist(String receiptNumber) {
        String sql = "select 1 from GoodsReceipts where ReceiptNumber = ? ";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, receiptNumber);
            try (ResultSet rs = stm.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.out.println("ERR: isReceiptNumberExist: " + e.getMessage());
        }
        return false;
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

        gr.setReceiptDate(getLocalDateTime(rs, "ReceiptDate"));

        gr.setCreatedAt(getLocalDateTime(rs, "CreatedAt"));

        gr.setCompletedAt(getLocalDateTime(rs, "CompletedAt"));

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

    private GoodsReceipt getGRByNumberInternal(Connection con, String receiptNumber) throws Exception {
        String sql = """
                    select gr.*, po.PONumber 
                     from GoodsReceipts gr
                     left join PurchaseOrders po on gr.POID = po.POID
                     where gr.ReceiptNumber = ?
                     """;
        try (PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, receiptNumber);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    GoodsReceipt gr = new GoodsReceipt();
                    gr.setId(rs.getLong("ReceiptID"));
                    gr.setReceiptNumber(rs.getString("ReceiptNumber"));
                    gr.setPoId(rs.getLong("POID"));
                    gr.setStatus(rs.getString("Status"));
                    gr.setReceiptDate(getLocalDateTime(rs, "ReceiptDate"));
                    gr.setReceivedBy(rs.getInt("ReceivedBy"));
                    gr.setNotes(rs.getString("Notes"));
                    gr.setPoNumber(rs.getString("PONumber"));
                    return gr;
                }
            }
        }
        return null;
    }

    private List<GoodsReceiptDetail> getGRDetailsInternal(Connection con, long receiptId) throws Exception {
        List<GoodsReceiptDetail> list = new ArrayList<>();
        String sql = """
                     select * from GoodsReceiptDetails
                     where ReceiptID = ?
                     order by ReceiptDetailID
                     """;
        try (PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setLong(1, receiptId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    GoodsReceiptDetail d = new GoodsReceiptDetail();
                    d.setId(rs.getLong("ReceiptDetailID"));
                    d.setPoLineItemId(rs.getLong("POLineItemID"));
                    d.setProductId(rs.getInt("ProductID"));
                    d.setQuantityReceived(rs.getInt("QuantityReceived"));
                    d.setUnitCost(rs.getBigDecimal("UnitCost"));
                    d.setLineTotal(rs.getBigDecimal("LineTotal"));
                    d.setNotes(rs.getString("Notes"));
                    list.add(d);
                }
            }
        }
        return list;
    }

    private int getProductStock(Connection conn, int productId) throws Exception {
        String sql = "SELECT Stock FROM Products WHERE ProductID = ?";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setInt(1, productId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Stock");
                }
            }
        }
        return 0;
    }

    private BigDecimal getProductCostPrice(Connection con, int productId) throws Exception {
        String sql = "select CostPrice from Products where ProductId =? ";
        try (PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setInt(1, productId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    BigDecimal cost = rs.getBigDecimal("CostPrice");
                    return cost != null ? cost : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    private void updatePOItemReceived(Connection con, long lineItemId, int addQty) throws Exception {
        String sql = "update PurchaseOrderItems set QuantityReceived = QuantityReceived + ?  where LineItemID = ? ";
        try (PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setInt(1, addQty);
            stm.setLong(2, lineItemId);
            stm.executeUpdate();
        }
    }

    private void updateGRDetailMAC(Connection con, long detailId, int oldQty, BigDecimal oldCost, BigDecimal newAvgCost) throws Exception {
        String sql = """
                     update GoodsReceiptDetails
                     set OldQty = ?, OldCost = ?, NewAvgCost = ?
                     where ReceiptDetailID = ?
                     """;
        try (PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setInt(1, oldQty);
            stm.setBigDecimal(2, oldCost != null ? oldCost : BigDecimal.ZERO);
            stm.setBigDecimal(3, newAvgCost != null ? newAvgCost : BigDecimal.ZERO);
            stm.setLong(4, detailId);
            stm.executeUpdate();
        }
    }

    private void updatePOStatus(Connection con, long poId) throws Exception {
        String sqlCheck = """
                     select COUNT(*) as total,
                              SUM(CASE WHEN QuantityReceived >= QuantityOrdered THEN 1 ELSE 0 END) as fullyReceived
                     from PurchaseOrderItems
                     where POID = ? 
                     """;
        try (PreparedStatement stmCheck = con.prepareStatement(sqlCheck)) {
            stmCheck.setLong(1, poId);
            try (ResultSet rs = stmCheck.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    int fullyReceived = rs.getInt("fullyReceived");

                    String newStatus = (total > 0 && fullyReceived == total) ? PurchaseOrder.STATUS_COMPLETED : PurchaseOrder.STATUS_PARTIAL_RECEIVED;

                    String sqlUpdatePO = "update PurchaseOrders set Status = ?, UpdatedAt = GETDATE() where POID = ? ";
                    try (PreparedStatement stmPO = con.prepareStatement(sqlUpdatePO)) {
                        stmPO.setString(1, newStatus);
                        stmPO.setLong(2, poId);
                        stmPO.executeUpdate();
                    }
                }
            }
        }
    }
}
