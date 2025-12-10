package view;

import controller.DataManager;
import model.*;
import utils.UIStyle;
// import main.Main; // Bỏ dòng này nếu bạn muốn dùng cách gọi Login trực tiếp bên dưới

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException; // Import thư viện in ấn
import java.text.SimpleDateFormat;
import java.util.Date;

public class PetManagementApp extends JFrame {
    private User currentUser;

    public PetManagementApp(User user) {
        this.currentUser = user;
        setTitle("Hệ Thống Quản Lý - " + user.fullName);
        setSize(1250, 750); // Mở rộng chiều ngang một chút để hiển thị đủ cột
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyle.COLOR_PRIMARY);
        header.setPreferredSize(new Dimension(1200, 70));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel title = new JLabel("PET CARE SYSTEM");
        title.setIcon(new UIStyle.EmojiIcon("🐾", 28));
        title.setIconTextGap(15);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24)); 
        
        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        userInfo.setOpaque(false);
        JLabel lblUser = new JLabel("Xin chào, " + user.fullName + " (" + user.role + ")");
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(UIStyle.FONT_NORMAL);
        
        JButton btnLogout = UIStyle.createButton("Đăng Xuất", "🚪", UIStyle.COLOR_DANGER);
        btnLogout.setForeground(Color.BLACK);
        btnLogout.addActionListener(e -> { 
            dispose(); // 1. Đóng cửa sổ hiện tại
            
            // 2. Mở lại màn hình đăng nhập
            LoginDialog login = new LoginDialog(null);
            login.setVisible(true); // Code sẽ dừng ở đây chờ người dùng đăng nhập xong
            
            // 3. Sau khi LoginDialog đóng, kiểm tra xem đăng nhập có thành công không
            if (login.isSucceeded()) {
                // Nếu thành công -> Mở lại màn hình chính với user mới
                new PetManagementApp(login.getAuthenticatedUser()).setVisible(true);
            }
        });

        userInfo.add(lblUser);
        userInfo.add(btnLogout);
        
        header.add(title, BorderLayout.WEST);
        header.add(userInfo, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- TABS ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        tabs.setBackground(Color.WHITE);
        tabs.setBorder(new EmptyBorder(10, 10, 10, 10));

        if (user.role.equals("ADMIN")) {
            tabs.addTab(" QUẢN LÝ ĐẶT LỊCH ", new UIStyle.EmojiIcon("📅", 20), createBookingMgmtPanel());
            tabs.addTab(" QUẢN LÝ DỊCH VỤ ", new UIStyle.EmojiIcon("✂️", 20), createServiceMgmtPanel());
            tabs.addTab(" DANH SÁCH THÚ CƯNG ", new UIStyle.EmojiIcon("🐶", 20), createPetListPanel());
            // [MỚI] Tab quản lý hồ sơ khách hàng
            tabs.addTab(" QUẢN LÝ KHÁCH HÀNG ", new UIStyle.EmojiIcon("👥", 20), createCustomerManagerPanel());
        } else {
            tabs.addTab(" ĐẶT LỊCH DỊCH VỤ ", new UIStyle.EmojiIcon("✨", 20), createCustomerBookingPanel());
            tabs.addTab(" THÚ CƯNG CỦA TÔI ", new UIStyle.EmojiIcon("🐕", 20), createMyPetPanel());
            // [MỚI] Tab xem lịch sử
            tabs.addTab(" LỊCH SỬ SỬ DỤNG ", new UIStyle.EmojiIcon("📜", 20), createHistoryPanel());
        }

        add(tabs, BorderLayout.CENTER);
    }

    // --- [MỚI] PANEL: ADMIN QUẢN LÝ KHÁCH HÀNG ---
    private JPanel createCustomerManagerPanel() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(UIStyle.COLOR_BG);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        DefaultTableModel model = new DefaultTableModel(new String[]{"Tài khoản", "Họ Tên", "Số ĐT", "Email"}, 0);
        JTable table = new JTable(model);
        UIStyle.styleTable(table);
        
        // Load dữ liệu (Cần đảm bảo bạn đã update DataManager ở bước trước)
        DataManager.loadCustomersToTable(model);
        
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        
        JButton btnRefresh = UIStyle.createButton("LÀM MỚI DANH SÁCH", "🔄", UIStyle.COLOR_PRIMARY);
        btnRefresh.addActionListener(e -> DataManager.loadCustomersToTable(model));
        JPanel bottom = new JPanel(); bottom.setOpaque(false); bottom.add(btnRefresh);
        p.add(bottom, BorderLayout.SOUTH);
        
        return p;
    }

    // --- [MỚI] PANEL: LỊCH SỬ DÙNG DỊCH VỤ (CHO KHÁCH) ---
    private JPanel createHistoryPanel() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(UIStyle.COLOR_BG);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        DefaultTableModel model = new DefaultTableModel(new String[]{"Mã Đơn", "Thú Cưng", "Dịch Vụ", "Giá", "Ngày", "Trạng Thái"}, 0);
        JTable table = new JTable(model);
        UIStyle.styleTable(table);
        
        Runnable loadHistory = () -> {
            model.setRowCount(0);
            for(Booking b : DataManager.mockBookings) {
                // Chỉ lấy booking của user hiện tại
                if(b.customerUser.equals(currentUser.username)) {
                    model.addRow(new Object[]{b.id, b.petName, b.serviceName, String.format("%,.0f đ", b.price), b.date, b.status});
                }
            }
        };
        loadHistory.run();
        
        JButton btnRefresh = UIStyle.createButton("CẬP NHẬT", "🔄", UIStyle.COLOR_PRIMARY);
        btnRefresh.addActionListener(e -> loadHistory.run());
        
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bottom.setOpaque(false); bottom.add(btnRefresh);
        p.add(bottom, BorderLayout.SOUTH);
        
        return p;
    }

    // --- PANEL: CUSTOMER BOOKING (Cập nhật nút Hóa đơn) ---
    private JPanel createCustomerBookingPanel() {
        JPanel container = new JPanel(new BorderLayout(20, 20));
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        container.setBackground(UIStyle.COLOR_BG);
        
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        
        TitledBorder border = BorderFactory.createTitledBorder(
            new LineBorder(UIStyle.COLOR_ACCENT, 1, true), 
            " ĐĂNG KÝ DỊCH VỤ MỚI ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
            new Font("Segoe UI", Font.BOLD, 14), UIStyle.COLOR_ACCENT);
        form.setBorder(border);
        
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 15, 10, 15); g.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<Pet> cbMyPets = new JComboBox<>();
        JComboBox<Service> cbServices = new JComboBox<>();
        
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();

        SpinnerDateModel dateModel = new SpinnerDateModel(today, today, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner txtDate = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(txtDate, "dd/MM/yyyy");
        txtDate.setEditor(dateEditor);
        txtDate.setPreferredSize(new Dimension(200, 35));

        String[] timeSlots = {"08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", 
                            "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"};
        JComboBox<String> cbTime = new JComboBox<>(timeSlots);
        cbTime.setPreferredSize(new Dimension(200, 35));

        cbMyPets.setPreferredSize(new Dimension(200, 35));
        cbServices.setPreferredSize(new Dimension(200, 35));

        g.gridx=0; g.gridy=0; form.add(new JLabel("Chọn Thú Cưng:"), g);
        JPanel pnlPetSelect = new JPanel(new BorderLayout(5, 0));
        pnlPetSelect.setBackground(Color.WHITE);
        pnlPetSelect.add(cbMyPets, BorderLayout.CENTER);
        JButton btnRefreshData = new JButton("🔄");
        btnRefreshData.setBackground(Color.WHITE);
        btnRefreshData.setBorder(new LineBorder(new Color(200,200,200)));
        btnRefreshData.setPreferredSize(new Dimension(35, 35));
        pnlPetSelect.add(btnRefreshData, BorderLayout.EAST);
        g.gridx=1; form.add(pnlPetSelect, g);

        g.gridx=0; g.gridy=1; form.add(new JLabel("Chọn Dịch Vụ:"), g);
        g.gridx=1; form.add(cbServices, g);
        g.gridx=0; g.gridy=2; form.add(new JLabel("Ngày Hẹn:"), g);
        g.gridx=1; form.add(txtDate, g);
        g.gridx=0; g.gridy=3; form.add(new JLabel("Giờ Hẹn:"), g);
        g.gridx=1; form.add(cbTime, g);
        
        JButton btnBook = UIStyle.createButton("GỬI YÊU CẦU", "📩", UIStyle.COLOR_SUCCESS);
        g.gridx=0; g.gridy=4; g.gridwidth=2; g.insets = new Insets(20, 15, 10, 15);
        form.add(btnBook, g);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Tài Khoản", "Thú Cưng", "Dịch Vụ", "Giá", "Thời Gian Hẹn", "Trạng Thái"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        UIStyle.styleTable(table);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new LineBorder(new Color(200,200,200)));
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // [CẬP NHẬT] Đổi icon và tên nút
        JButton btnViewInvoice = UIStyle.createButton("XEM HÓA ĐƠN & IN", "🖨️", UIStyle.COLOR_WARNING);
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setOpaque(false);
        bottomBar.add(btnViewInvoice);

        container.add(form, BorderLayout.NORTH);
        container.add(tablePanel, BorderLayout.CENTER);
        container.add(bottomBar, BorderLayout.SOUTH);

        Runnable reload = () -> {
            Object selectedPet = cbMyPets.getSelectedItem();
            cbMyPets.removeAllItems(); 
            for(Pet p : DataManager.getPetsByOwner(currentUser.username)) cbMyPets.addItem(p);
            
            cbServices.removeAllItems(); 
            for(Service s : DataManager.mockServices) cbServices.addItem(s);
            
            DataManager.loadBookingsToTable(model, currentUser.username, currentUser.role);
        };
        reload.run();

        btnRefreshData.addActionListener(e -> {
            reload.run();
            JOptionPane.showMessageDialog(this, "Đã cập nhật dữ liệu!");
        });

        btnBook.addActionListener(e -> {
            Pet pt = (Pet)cbMyPets.getSelectedItem(); 
            Service sv = (Service)cbServices.getSelectedItem();
            Date date = (Date)txtDate.getValue(); 
            String time = (String)cbTime.getSelectedItem(); 

            if(pt == null) { JOptionPane.showMessageDialog(this, "Chưa có thú cưng!"); return; }
            
            DataManager.addBooking(currentUser.username, pt.name, sv.name, sv.price, date, time);
            reload.run();
            JOptionPane.showMessageDialog(this, "Đã đặt lịch hẹn ngày " + new SimpleDateFormat("dd/MM/yyyy").format(date) + " lúc " + time + " thành công!");
        });

        btnViewInvoice.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng!"); return; }
            String status = (String) model.getValueAt(r, 6); 
            if(!status.equals("Đã Xong")) { JOptionPane.showMessageDialog(this, "Đơn chưa hoàn thành!", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return; }
            Booking b = DataManager.getBookingById((int)model.getValueAt(r, 0));
            showInvoice(b);
        });

        return container;
    }

    // --- PANEL: ADMIN BOOKING ---
    private JPanel createBookingMgmtPanel() {
        JPanel container = new JPanel(new BorderLayout(20, 20));
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        container.setBackground(UIStyle.COLOR_BG);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Khách Hàng", "Thú Cưng", "Dịch Vụ", "Giá", "Ngày Đặt", "Trạng Thái"}, 0);
        JTable table = new JTable(model);
        UIStyle.styleTable(table);
        DataManager.loadBookingsToTable(model, currentUser.username, currentUser.role);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actions.setOpaque(false);
        JButton btnApprove = UIStyle.createButton("DUYỆT / HOÀN THÀNH", "✅", UIStyle.COLOR_SUCCESS);
        JButton btnRefresh = UIStyle.createButton("Làm Mới", "🔄", UIStyle.COLOR_PRIMARY);
        actions.add(btnApprove); actions.add(btnRefresh);

        container.add(new JScrollPane(table), BorderLayout.CENTER);
        container.add(actions, BorderLayout.SOUTH);

        btnApprove.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r != -1) {
                int id = (int)model.getValueAt(r, 0);
                String curr = (String)model.getValueAt(r, 6);
                String next = curr.equals("Chờ duyệt") ? "Đã Duyệt" : (curr.equals("Đã Duyệt") ? "Đã Xong" : curr);
                DataManager.updateBookingStatus(id, next);
                DataManager.loadBookingsToTable(model, currentUser.username, currentUser.role);
                if(next.equals("Đã Xong")) JOptionPane.showMessageDialog(this, "Đơn hàng hoàn tất!");
            } else JOptionPane.showMessageDialog(this, "Chọn đơn để xử lý!");
        });
        btnRefresh.addActionListener(e -> DataManager.loadBookingsToTable(model, currentUser.username, currentUser.role));
        
        return container;
    }

    // --- PANEL: KHÁCH HÀNG - QUẢN LÝ PET CÁ NHÂN ---
    private JPanel createMyPetPanel() {
        JPanel container = new JPanel(new BorderLayout(20, 20));
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        container.setBackground(UIStyle.COLOR_BG);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setPreferredSize(new Dimension(300, 0)); 
        form.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(UIStyle.COLOR_ACCENT, 1, true), 
            " THÔNG TIN THÚ CƯNG ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
            UIStyle.FONT_HEADER, UIStyle.COLOR_ACCENT));
        
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10); g.fill = GridBagConstraints.HORIZONTAL; g.gridx = 0; 

        JTextField txtName = UIStyle.createTextField();
        JTextField txtSpecies = UIStyle.createTextField();
        JTextField txtAge = UIStyle.createTextField();
        JLabel lblRealID = new JLabel(); 
        
        int row = 0;
        g.gridy = row++; form.add(new JLabel("Tên thú cưng:"), g);
        g.gridy = row++; form.add(txtName, g);
        g.gridy = row++; form.add(new JLabel("Loài:"), g);
        g.gridy = row++; form.add(txtSpecies, g);
        g.gridy = row++; form.add(new JLabel("Tuổi:"), g);
        g.gridy = row++; form.add(txtAge, g);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        JButton btnAdd = UIStyle.createButton("THÊM", "➕", UIStyle.COLOR_ACCENT);
        JButton btnDelete = UIStyle.createButton("XÓA", "🗑️", UIStyle.COLOR_DANGER);
        buttonPanel.add(btnAdd); buttonPanel.add(btnDelete);

        g.gridy = row++; g.insets = new Insets(20, 10, 10, 10); 
        form.add(buttonPanel, g);
        g.gridy = 99; g.weighty = 1.0; form.add(new JPanel(){{setOpaque(false);}}, g);

        DefaultTableModel model = new DefaultTableModel(new String[]{"STT", "Tên", "Loài", "Tuổi", "Chủ", "ID_HIDDEN"}, 0){
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        UIStyle.styleTable(table);
        
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(5).setMinWidth(0);
        table.getColumnModel().getColumn(5).setMaxWidth(0);
        table.getColumnModel().getColumn(5).setWidth(0);

        DataManager.loadPetsToTable(model, currentUser.username, currentUser.role);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new LineBorder(new Color(200,200,200)));
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                txtName.setText(model.getValueAt(r, 1).toString());
                txtSpecies.setText(model.getValueAt(r, 2).toString());
                String ageStr = model.getValueAt(r, 3).toString().replace(" tuổi", "");
                txtAge.setText(ageStr);
                lblRealID.setText(model.getValueAt(r, 5).toString());
            }
        });

        btnAdd.addActionListener(e -> { 
            try { 
                DataManager.addPet(txtName.getText(), txtSpecies.getText(), Integer.parseInt(txtAge.getText()), currentUser.username); 
                DataManager.loadPetsToTable(model, currentUser.username, currentUser.role); 
                txtName.setText(""); txtSpecies.setText(""); txtAge.setText("");
                JOptionPane.showMessageDialog(this, "Đã thêm thú cưng thành công!");
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Tuổi phải là số!"); }
        });
        
        btnDelete.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r == -1) { JOptionPane.showMessageDialog(this, "Chọn thú cưng cần xóa!"); return; }
            if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa?", "Xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                int realID = Integer.parseInt(model.getValueAt(r, 5).toString());
                DataManager.deletePet(realID); 
                DataManager.loadPetsToTable(model, currentUser.username, currentUser.role);
                txtName.setText(""); txtSpecies.setText(""); txtAge.setText("");
                JOptionPane.showMessageDialog(this, "Đã xóa!");
            }
        });
        
        container.add(form, BorderLayout.WEST);
        container.add(tablePanel, BorderLayout.CENTER);
        return container;
    }

    private JPanel createServiceMgmtPanel() {
        JPanel p = new JPanel(new BorderLayout(10,10)); p.setBorder(new EmptyBorder(20,20,20,20)); p.setBackground(UIStyle.COLOR_BG);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10)); top.setBackground(Color.WHITE);
        JTextField t1 = UIStyle.createTextField(); JTextField t2 = UIStyle.createTextField();
        top.add(new JLabel("Dịch Vụ:")); top.add(t1); top.add(new JLabel("Giá:")); top.add(t2);
        JButton btn = UIStyle.createButton("THÊM", "➕", UIStyle.COLOR_ACCENT); top.add(btn);
        
        DefaultTableModel m = new DefaultTableModel(new String[]{"ID", "Tên Dịch Vụ", "Giá Tiền"}, 0);
        JTable t = new JTable(m); UIStyle.styleTable(t);
        DataManager.loadServicesToTable(m);
        btn.addActionListener(e -> { DataManager.addService(t1.getText(), Double.parseDouble(t2.getText())); DataManager.loadServicesToTable(m); });
        
        p.add(top, BorderLayout.NORTH); p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }

    // --- PANEL: ADMIN - DANH SÁCH THÚ CƯNG ---
    private JPanel createPetListPanel() {
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        container.setBackground(UIStyle.COLOR_BG);
        JPanel searchPanel = new JPanel(new BorderLayout(15, 0));
        searchPanel.setOpaque(false);
        
        JLabel lblSearch = new JLabel("Nhập thông tin tìm kiếm:");
        lblSearch.setFont(UIStyle.FONT_NORMAL);
        JTextField txtSearch = UIStyle.createTextField();
        JButton btnSearch = UIStyle.createButton("TÌM KIẾM", "🔍", UIStyle.COLOR_PRIMARY);
        btnSearch.setPreferredSize(new Dimension(150, 40)); 
        
        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Tên Thú Cưng", "Loài", "Tuổi", "Chủ Sở Hữu"}, 0){
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        UIStyle.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        DataManager.loadPetsToTable(model, "admin", "ADMIN");

        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            if (keyword.isEmpty()) DataManager.loadPetsToTable(model, "admin", "ADMIN");
            else DataManager.searchPetsToTable(model, keyword);
        }); 

        container.add(searchPanel, BorderLayout.NORTH);
        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    // --- [CẬP NHẬT] HÀM HIỂN THỊ & IN HÓA ĐƠN ---
    private void showInvoice(Booking b) {
        String txt = 
            "================================\n" +
            "      HÓA ĐƠN THANH TOÁN      \n" +
            "================================\n\n" +
            " Mã Đơn   : #" + b.id + "\n" +
            " Ngày     : " + b.date + "\n" +
            " Khách    : " + currentUser.fullName + "\n" +
            " SĐT      : " + (currentUser.phone != null ? currentUser.phone : "N/A") + "\n" + // Hiện SĐT
            "--------------------------------\n" +
            " Dịch Vụ  : " + b.serviceName + "\n" +
            " Thú Cưng : " + b.petName + "\n" +
            "--------------------------------\n" +
            " TỔNG TIỀN: " + String.format("%,.0f VND", b.price) + "\n\n" +
            "================================\n" +
            "   Cảm ơn quý khách đã tin dùng!   ";
            
        JTextArea area = new JTextArea(txt); 
        area.setFont(new Font("Monospaced", Font.BOLD, 14)); 
        area.setEditable(false);
        
        // Tạo panel chứa nút In
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.add(new JScrollPane(area), BorderLayout.CENTER);
        
        JButton btnPrint = new JButton("Xuất PDF / In");
        btnPrint.addActionListener(e -> {
            try {
                // Lệnh này sẽ mở hộp thoại in của máy tính -> Chọn "Save as PDF"
                boolean complete = area.print(); 
                if (complete) JOptionPane.showMessageDialog(this, "Đã in thành công!");
            } catch (PrinterException ex) {
                ex.printStackTrace();
            }
        });
        pnl.add(btnPrint, BorderLayout.SOUTH);
        
        // Hiển thị dialog to hơn một chút
        JDialog dialog = new JDialog(this, "Hóa Đơn Chi Tiết", true);
        dialog.setContentPane(pnl);
        dialog.setSize(400, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}