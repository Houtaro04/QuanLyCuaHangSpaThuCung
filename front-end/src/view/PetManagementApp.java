package view;

import controller.DataManager;
import model.*;
import utils.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PetManagementApp extends JFrame {
    private User currentUser;
    // Label to display user info in the header, accessible for updates
    private JLabel lblUser;

    public PetManagementApp(User user) {
        this.currentUser = user;
        setTitle("Hệ Thống Quản Lý - " + user.fullName);
        setSize(1250, 750);
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
        
        // Initialize user label
        lblUser = new JLabel("Xin chào, " + user.fullName + " (" + user.role + ")");
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(UIStyle.FONT_NORMAL);

        JButton btnLogout = UIStyle.createButton("Đăng Xuất", "🚪", UIStyle.COLOR_DANGER);
        btnLogout.setForeground(Color.BLACK);
        btnLogout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginDialog login = new LoginDialog(null);
                login.setVisible(true);
                if (login.isSucceeded()) {
                    new PetManagementApp(login.getAuthenticatedUser()).setVisible(true);
                }
            });
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
            tabs.addTab(" QUẢN LÝ KHÁCH HÀNG ", new UIStyle.EmojiIcon("👥", 20), createCustomerManagerPanel());
        } else {
            tabs.addTab(" THÔNG TIN CÁ NHÂN ", new UIStyle.EmojiIcon("👤", 20), createProfilePanel());
            tabs.addTab(" ĐẶT LỊCH DỊCH VỤ ", new UIStyle.EmojiIcon("✨", 20), createCustomerBookingPanel());
            tabs.addTab(" THÚ CƯNG CỦA TÔI ", new UIStyle.EmojiIcon("🐕", 20), createMyPetPanel());
            tabs.addTab(" LỊCH SỬ ĐƠN HÀNG ", new UIStyle.EmojiIcon("📜", 20), createHistoryPanel());
        }

        add(tabs, BorderLayout.CENTER);
    }

    // =================================================================================
    // --- 1. PANEL THÔNG TIN CÁ NHÂN ---
    private JPanel createProfilePanel() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(UIStyle.COLOR_BG);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(30, 50, 30, 50)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;
        
        // Avatar
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        JLabel lblAvatar = new JLabel(new UIStyle.EmojiIcon("🧑‍💻", 80));
        lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblAvatar, g);

        // Tiêu đề
        g.gridy++;
        JLabel lblTitle = new JLabel("HỒ SƠ KHÁCH HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(UIStyle.COLOR_PRIMARY);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblTitle, g);

        // Reset gridwidth
        g.gridwidth = 1;

        // Các trường thông tin
        addProfileLabel(card, g, "Tên tài khoản:", 2);
        addProfileField(card, g, currentUser.username, false, 2);

        addProfileLabel(card, g, "Vai trò:", 3);
        addProfileField(card, g, currentUser.role, false, 3);

        addProfileLabel(card, g, "Họ và Tên:", 4);
        JTextField txtFullname = addProfileField(card, g, currentUser.fullName, true, 4);

        addProfileLabel(card, g, "Số điện thoại:", 5);
        JTextField txtPhone = addProfileField(card, g, (currentUser.phone != null ? currentUser.phone : ""), true, 5);

        addProfileLabel(card, g, "Email:", 6);
        JTextField txtEmail = addProfileField(card, g, (currentUser.email != null ? currentUser.email : ""), true, 6);

        // --- KHU VỰC NÚT BẤM (CẬP NHẬT MỚI) ---
        g.gridy = 7; g.gridx = 0; g.gridwidth = 2; g.insets = new Insets(20, 10, 10, 10);
        
        // Tạo Panel chứa 2 nút để xếp ngang hàng
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);
        
        JButton btnSave = UIStyle.createButton("LƯU THAY ĐỔI", "💾", UIStyle.COLOR_SUCCESS);
        JButton btnChangePass = UIStyle.createButton("ĐỔI MẬT KHẨU", "🔒", UIStyle.COLOR_WARNING); // Nút mới
        
        btnPanel.add(btnSave);
        btnPanel.add(btnChangePass);
        
        card.add(btnPanel, g);

        // --- SỰ KIỆN LƯU THÔNG TIN ---
        btnSave.addActionListener(e -> {
            String newName = txtFullname.getText().trim();
            String newPhone = txtPhone.getText().trim();
            String newEmail = txtEmail.getText().trim();

            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Họ tên không được để trống!");
                return;
            }

            try {
                DataManager.updateCustomerInfo(currentUser.username, newName, newPhone, newEmail);
                
                currentUser.fullName = newName;
                currentUser.phone = newPhone;
                currentUser.email = newEmail;
                
                lblUser.setText("Xin chào, " + currentUser.fullName + " (" + currentUser.role + ")");
                setTitle("Hệ Thống Quản Lý - " + currentUser.fullName);
                
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage());
            }
        });

        // --- SỰ KIỆN ĐỔI MẬT KHẨU (MỚI) ---
        btnChangePass.addActionListener(e -> {
            // Tạo form nhập mật khẩu
            JPasswordField txtOldPass = new JPasswordField();
            JPasswordField txtNewPass = new JPasswordField();
            JPasswordField txtConfirmPass = new JPasswordField();

            Object[] message = {
                "Mật khẩu cũ:", txtOldPass,
                "Mật khẩu mới:", txtNewPass,
                "Nhập lại mật khẩu mới:", txtConfirmPass
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Đổi Mật Khẩu", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String oldPass = new String(txtOldPass.getPassword());
                String newPass = new String(txtNewPass.getPassword());
                String confirmPass = new String(txtConfirmPass.getPassword());

                // 1. Kiểm tra mật khẩu cũ
                if (DataManager.checkLogin(currentUser.username, oldPass) == null) {
                    JOptionPane.showMessageDialog(this, "Mật khẩu cũ không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 2. Validate mật khẩu mới
                if (newPass.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Mật khẩu mới không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!newPass.equals(confirmPass)) {
                    JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 3. Cập nhật và Đăng nhập lại
                if (DataManager.updatePassword(currentUser.username, newPass)) {
                    JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
                    
                    dispose(); // Đóng cửa sổ hiện tại
                    
                    // --- ĐOẠN CODE SỬA LỖI ---
                    LoginDialog login = new LoginDialog(null);
                    login.setVisible(true); // Chờ người dùng đăng nhập
                    
                    // Kiểm tra nếu đăng nhập thành công thì mở lại App
                    if (login.isSucceeded()) {
                        new PetManagementApp(login.getAuthenticatedUser()).setVisible(true);
                    }
                    // -------------------------
                    
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi hệ thống, không thể đổi mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        container.add(card);
        return container;
    }

    private void addProfileLabel(JPanel p, GridBagConstraints g, String text, int y) {
        g.gridx = 0; g.gridy = y; g.weightx = 0.3;
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIStyle.FONT_BUTTON);
        p.add(lbl, g);
    }

    private JTextField addProfileField(JPanel p, GridBagConstraints g, String text, boolean editable, int y) {
        g.gridx = 1; g.gridy = y; g.weightx = 0.7;
        JTextField txt = new JTextField(text);
        txt.setFont(UIStyle.FONT_NORMAL);
        txt.setEditable(editable);
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(editable ? UIStyle.COLOR_ACCENT : Color.LIGHT_GRAY),
                new EmptyBorder(5, 5, 5, 5)
        ));
        if (!editable) txt.setBackground(new Color(240, 240, 240));
        p.add(txt, g);
        return txt;
    }

    // =================================================================================
    // 2. PANEL QUẢN LÝ DỊCH VỤ (ADMIN)
    // =================================================================================
    private JPanel createServiceMgmtPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        p.setBackground(UIStyle.COLOR_BG);

        // Input Panel
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        top.setBackground(Color.WHITE);

        JTextField tName = UIStyle.createTextField();
        JTextField tPrice = UIStyle.createTextField();
        JLabel lblId = new JLabel(); // Hidden ID label

        top.add(new JLabel("Dịch Vụ:")); top.add(tName);
        top.add(new JLabel("Giá:")); top.add(tPrice);

        JButton btnAdd = UIStyle.createButton("THÊM", "➕", UIStyle.COLOR_SUCCESS);
        JButton btnEdit = UIStyle.createButton("SỬA", "✏️", UIStyle.COLOR_WARNING);
        JButton btnDelete = UIStyle.createButton("XÓA", "🗑️", UIStyle.COLOR_DANGER);

        top.add(btnAdd); top.add(btnEdit); top.add(btnDelete);

        // Table
        DefaultTableModel m = new DefaultTableModel(new String[]{"ID", "Tên Dịch Vụ", "Giá Tiền"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable t = new JTable(m);
        UIStyle.styleTable(t);
        DataManager.loadServicesToTable(m);

        // Table Click Event
        t.getSelectionModel().addListSelectionListener(e -> {
            int r = t.getSelectedRow();
            if (r >= 0) {
                lblId.setText(m.getValueAt(r, 0).toString());
                tName.setText(m.getValueAt(r, 1).toString());
                // Clean price string for editing
                String priceStr = m.getValueAt(r, 2).toString().replace(" đ", "").replace(",", "").replace(".", "");
                tPrice.setText(priceStr);
            }
        });

        // Add Button Action
        btnAdd.addActionListener(e -> {
            try {
                if (tName.getText().isEmpty() || tPrice.getText().isEmpty()) return;
                DataManager.addService(tName.getText(), Double.parseDouble(tPrice.getText()));
                DataManager.loadServicesToTable(m);
                tName.setText(""); tPrice.setText(""); lblId.setText("");
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Giá phải là số!"); }
        });

        // Edit Button Action
        btnEdit.addActionListener(e -> {
            try {
                if (lblId.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Chọn dịch vụ để sửa!"); return; }
                int id = Integer.parseInt(lblId.getText());
                DataManager.updateService(id, tName.getText(), Double.parseDouble(tPrice.getText()));
                DataManager.loadServicesToTable(m);
                tName.setText(""); tPrice.setText(""); lblId.setText("");
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi cập nhật!"); }
        });

        // Delete Button Action
        btnDelete.addActionListener(e -> {
            if (lblId.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Chọn dịch vụ để xóa!"); return; }
            if (JOptionPane.showConfirmDialog(this, "Chắc chắn xóa?", "Xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(lblId.getText());
                DataManager.deleteService(id);
                DataManager.loadServicesToTable(m);
                tName.setText(""); tPrice.setText(""); lblId.setText("");
                JOptionPane.showMessageDialog(this, "Đã xóa!");
            }
        });

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }

    // =================================================================================
    // 3. CÁC PANEL KHÁC (USER BOOKING, HISTORY, PETS, ADMIN PANELS)
    // =================================================================================

    // --- PANEL: LỊCH SỬ ĐƠN HÀNG (ĐÃ HOÀN THÀNH) ---
    private JPanel createHistoryPanel() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(UIStyle.COLOR_BG);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setOpaque(false);
        JLabel lblTitle = new JLabel("LỊCH SỬ CÁC ĐƠN HÀNG ĐÃ HOÀN THÀNH");
        lblTitle.setFont(UIStyle.FONT_HEADER);
        header.add(lblTitle);
        p.add(header, BorderLayout.NORTH);

        // Table
        DefaultTableModel model = new DefaultTableModel(new String[]{"Mã Đơn", "Thú Cưng", "Dịch Vụ", "Giá", "Ngày", "Trạng Thái"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        UIStyle.styleTable(table);
        
        // Load dữ liệu
        Runnable loadHistory = () -> {
            model.setRowCount(0);
            for(Booking b : DataManager.mockBookings) {
                // Lọc: Của user hiện tại VÀ Trạng thái là "Đã Xong"
                if(b.customerUser.equals(currentUser.username) && b.status.equals("Đã Xong")) {
                    model.addRow(new Object[]{b.id, b.petName, b.serviceName, String.format("%,.0f đ", b.price), b.date, b.status});
                }
            }
        };
        loadHistory.run();
        
        // --- BUTTONS (Chỉ còn nút Làm mới) ---
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT)); 
        bottom.setOpaque(false); 
        
        JButton btnRefresh = UIStyle.createButton("LÀM MỚI", "🔄", UIStyle.COLOR_PRIMARY);
        btnRefresh.addActionListener(e -> loadHistory.run());
        
        bottom.add(btnRefresh);
        p.add(bottom, BorderLayout.SOUTH);
        
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    // --- PANEL: CUSTOMER BOOKING ---
    private JPanel createCustomerBookingPanel() {
        JPanel container = new JPanel(new BorderLayout(20, 20));
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        container.setBackground(UIStyle.COLOR_BG);
        
        // --- FORM ĐĂNG KÝ (GIỮ NGUYÊN) ---
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(UIStyle.COLOR_ACCENT, 1, true), 
            " ĐĂNG KÝ DỊCH VỤ MỚI ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
            new Font("Segoe UI", Font.BOLD, 14), UIStyle.COLOR_ACCENT));
        
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

        g.gridx=0; g.gridy=1; form.add(new JLabel("Chọn Dịch Vụ:"), g); g.gridx=1; form.add(cbServices, g);
        g.gridx=0; g.gridy=2; form.add(new JLabel("Ngày Hẹn:"), g); g.gridx=1; form.add(txtDate, g);
        g.gridx=0; g.gridy=3; form.add(new JLabel("Giờ Hẹn:"), g); g.gridx=1; form.add(cbTime, g);
        
        JButton btnBook = UIStyle.createButton("GỬI YÊU CẦU", "📩", UIStyle.COLOR_SUCCESS);
        g.gridx=0; g.gridy=4; g.gridwidth=2; g.insets = new Insets(20, 15, 10, 15);
        form.add(btnBook, g);

        // --- BẢNG DỮ LIỆU ---
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Tài Khoản", "Thú Cưng", "Dịch Vụ", "Giá", "Thời Gian Hẹn", "Trạng Thái"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        UIStyle.styleTable(table);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new LineBorder(new Color(200,200,200)));
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // --- NÚT IN ---
        JButton btnViewInvoice = UIStyle.createButton("IN ĐƠN ĐÃ CHỌN", "🖨️", UIStyle.COLOR_WARNING);
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setOpaque(false);
        bottomBar.add(btnViewInvoice);

        container.add(form, BorderLayout.NORTH);
        container.add(tablePanel, BorderLayout.CENTER);
        container.add(bottomBar, BorderLayout.SOUTH);

        // --- LOGIC ---
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
            JOptionPane.showMessageDialog(this, "Đã đặt lịch hẹn thành công!");
        });

        // --- LOGIC MỚI: Xử lý in nhiều dòng ---
        btnViewInvoice.addActionListener(e -> {
            // Lấy danh sách các dòng được chọn (Giữ Ctrl/Shift để chọn nhiều)
            int[] selectedRows = table.getSelectedRows();
            
            if (selectedRows.length == 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một đơn hàng để in!\n(Giữ phím Ctrl để chọn nhiều đơn)", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            java.util.List<Booking> list = new java.util.ArrayList<>();
            boolean hasError = false; // Cờ kiểm tra lỗi

            for (int r : selectedRows) {
                String status = (String) model.getValueAt(r, 6); // Cột trạng thái
                
                // Chỉ cho phép in đơn đã hoàn thành
                if (!status.equals("Đã Xong")) {
                    hasError = true;
                    break; // Dừng lại nếu gặp đơn chưa xong
                }
                
                int id = Integer.parseInt(model.getValueAt(r, 0).toString());
                Booking b = DataManager.getBookingById(id);
                if (b != null) list.add(b);
            }

            if (hasError) {
                JOptionPane.showMessageDialog(this, "Bạn đã chọn đơn hàng chưa hoàn thành (Chờ duyệt/Đã duyệt).\nChỉ có thể xuất hóa đơn cho các đơn 'Đã Xong'!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Gọi hàm in danh sách
            showInvoice(list);
        });

        return container;
    }

    // --- PANEL: ADMIN BOOKING ---
    private JPanel createBookingMgmtPanel() {
        JPanel container = new JPanel(new BorderLayout(20, 20));
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        container.setBackground(UIStyle.COLOR_BG);
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Khách Hàng", "Thú Cưng", "Dịch Vụ", "Giá", "Ngày Đặt", "Trạng Thái"}, 0);
        JTable table = new JTable(model); UIStyle.styleTable(table);
        DataManager.loadBookingsToTable(model, currentUser.username, currentUser.role);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); actions.setOpaque(false);
        JButton btnApprove = UIStyle.createButton("DUYỆT / HOÀN THÀNH", "✅", UIStyle.COLOR_SUCCESS);
        JButton btnRefresh = UIStyle.createButton("Làm Mới", "🔄", UIStyle.COLOR_PRIMARY);
        actions.add(btnApprove); actions.add(btnRefresh);
        container.add(new JScrollPane(table), BorderLayout.CENTER); container.add(actions, BorderLayout.SOUTH);
        btnApprove.addActionListener(e -> {
            int r = table.getSelectedRow();
            if(r != -1) {
                int id = (int)model.getValueAt(r, 0);
                String curr = (String)model.getValueAt(r, 6);
                String next = curr.equals("Chờ duyệt") ? "Đã Duyệt" : (curr.equals("Đã Duyệt") ? "Đã Xong" : curr);
                DataManager.updateBookingStatus(id, next);
                DataManager.loadBookingsToTable(model, currentUser.username, currentUser.role);
                if(next.equals("Đã Xong")) JOptionPane.showMessageDialog(this, "Đơn hàng hoàn tất!");
            }
        });
        btnRefresh.addActionListener(e -> DataManager.loadBookingsToTable(model, currentUser.username, currentUser.role));
        return container;
    }

    // --- PANEL: CUSTOMER MANAGER (ADMIN) ---
    private JPanel createCustomerManagerPanel() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(UIStyle.COLOR_BG); p.setBorder(new EmptyBorder(20, 20, 20, 20));
        DefaultTableModel model = new DefaultTableModel(new String[]{"Tài khoản", "Họ Tên", "Số ĐT", "Email"}, 0);
        JTable table = new JTable(model); UIStyle.styleTable(table);
        DataManager.loadCustomersToTable(model);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton btnRefresh = UIStyle.createButton("LÀM MỚI DANH SÁCH", "🔄", UIStyle.COLOR_PRIMARY);
        btnRefresh.addActionListener(e -> DataManager.loadCustomersToTable(model));
        JPanel bottom = new JPanel(); bottom.setOpaque(false); bottom.add(btnRefresh);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    // --- PANEL: MY PETS (USER) ---
    private JPanel createMyPetPanel() {
        JPanel container = new JPanel(new BorderLayout(20, 20));
        container.setBorder(new EmptyBorder(20, 20, 20, 20)); container.setBackground(UIStyle.COLOR_BG);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE); form.setPreferredSize(new Dimension(300, 0));
        form.setBorder(BorderFactory.createTitledBorder(new LineBorder(UIStyle.COLOR_ACCENT, 1, true), " THÔNG TIN THÚ CƯNG ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, UIStyle.FONT_HEADER, UIStyle.COLOR_ACCENT));
        GridBagConstraints g = new GridBagConstraints(); g.insets = new Insets(8, 10, 8, 10); g.fill = GridBagConstraints.HORIZONTAL; g.gridx = 0;
        JTextField txtName = UIStyle.createTextField(); JTextField txtSpecies = UIStyle.createTextField(); JTextField txtAge = UIStyle.createTextField(); JLabel lblRealID = new JLabel();
        int row = 0; g.gridy = row++; form.add(new JLabel("Tên thú cưng:"), g); g.gridy = row++; form.add(txtName, g);
        g.gridy = row++; form.add(new JLabel("Loài:"), g); g.gridy = row++; form.add(txtSpecies, g);
        g.gridy = row++; form.add(new JLabel("Tuổi:"), g); g.gridy = row++; form.add(txtAge, g);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0)); buttonPanel.setOpaque(false);
        JButton btnAdd = UIStyle.createButton("THÊM", "➕", UIStyle.COLOR_ACCENT); JButton btnDelete = UIStyle.createButton("XÓA", "🗑️", UIStyle.COLOR_DANGER);
        buttonPanel.add(btnAdd); buttonPanel.add(btnDelete);
        g.gridy = row++; g.insets = new Insets(20, 10, 10, 10); form.add(buttonPanel, g); g.gridy = 99; g.weighty = 1.0; form.add(new JPanel(){{setOpaque(false);}}, g);
        
        // Table with Hidden ID
        DefaultTableModel model = new DefaultTableModel(new String[]{"STT", "Tên", "Loài", "Tuổi", "Chủ", "ID_HIDDEN"}, 0){ @Override public boolean isCellEditable(int row, int col) { return false; } };
        JTable table = new JTable(model); UIStyle.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        // Hide ID Column
        table.getColumnModel().getColumn(5).setMinWidth(0); table.getColumnModel().getColumn(5).setMaxWidth(0); table.getColumnModel().getColumn(5).setWidth(0);
        
        DataManager.loadPetsToTable(model, currentUser.username, currentUser.role);
        JPanel tablePanel = new JPanel(new BorderLayout()); tablePanel.setBackground(Color.WHITE); tablePanel.setBorder(new LineBorder(new Color(200,200,200))); tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        table.getSelectionModel().addListSelectionListener(e -> { int r = table.getSelectedRow(); if (r >= 0) { txtName.setText(model.getValueAt(r, 1).toString()); txtSpecies.setText(model.getValueAt(r, 2).toString()); String ageStr = model.getValueAt(r, 3).toString().replace(" tuổi", ""); txtAge.setText(ageStr); lblRealID.setText(model.getValueAt(r, 5).toString()); } });
        
        btnAdd.addActionListener(e -> { try { DataManager.addPet(txtName.getText(), txtSpecies.getText(), Integer.parseInt(txtAge.getText()), currentUser.username); DataManager.loadPetsToTable(model, currentUser.username, currentUser.role); txtName.setText(""); txtSpecies.setText(""); txtAge.setText(""); JOptionPane.showMessageDialog(this, "Đã thêm thú cưng thành công!"); } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Tuổi phải là số!"); } });
        
        btnDelete.addActionListener(e -> { int r = table.getSelectedRow(); if (r == -1) { JOptionPane.showMessageDialog(this, "Chọn thú cưng cần xóa!"); return; } if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa?", "Xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { int realID = Integer.parseInt(model.getValueAt(r, 5).toString()); DataManager.deletePet(realID); DataManager.loadPetsToTable(model, currentUser.username, currentUser.role); txtName.setText(""); txtSpecies.setText(""); txtAge.setText(""); JOptionPane.showMessageDialog(this, "Đã xóa!"); } });
        
        container.add(form, BorderLayout.WEST); container.add(tablePanel, BorderLayout.CENTER);
        return container;
    }

    // --- PANEL: PET LIST (ADMIN) ---
    private JPanel createPetListPanel() {
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBorder(new EmptyBorder(20, 20, 20, 20)); container.setBackground(UIStyle.COLOR_BG);
        JPanel searchPanel = new JPanel(new BorderLayout(15, 0)); searchPanel.setOpaque(false);
        JLabel lblSearch = new JLabel("Nhập thông tin tìm kiếm:"); lblSearch.setFont(UIStyle.FONT_NORMAL);
        JTextField txtSearch = UIStyle.createTextField(); JButton btnSearch = UIStyle.createButton("TÌM KIẾM", "🔍", UIStyle.COLOR_PRIMARY); btnSearch.setPreferredSize(new Dimension(150, 40)); 
        searchPanel.add(lblSearch, BorderLayout.WEST); searchPanel.add(txtSearch, BorderLayout.CENTER); searchPanel.add(btnSearch, BorderLayout.EAST);
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Tên Thú Cưng", "Loài", "Tuổi", "Chủ Sở Hữu"}, 0){ @Override public boolean isCellEditable(int row, int col) { return false; } };
        JTable table = new JTable(model); UIStyle.styleTable(table); JScrollPane scrollPane = new JScrollPane(table); scrollPane.setBorder(new LineBorder(new Color(200, 200, 200))); scrollPane.getViewport().setBackground(Color.WHITE);
        DataManager.loadPetsToTable(model, "admin", "ADMIN");
        btnSearch.addActionListener(e -> { String keyword = txtSearch.getText().trim(); if (keyword.isEmpty()) DataManager.loadPetsToTable(model, "admin", "ADMIN"); else DataManager.searchPetsToTable(model, keyword); }); 
        container.add(searchPanel, BorderLayout.NORTH); container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    // --- [CẬP NHẬT] HÀM IN NHIỀU HÓA ĐƠN ---
    private void showInvoice(java.util.List<Booking> bookings) {
        if (bookings.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        double grandTotal = 0;
        
        sb.append("=================================================\n");
        sb.append("          DANH SÁCH HÓA ĐƠN THANH TOÁN           \n");
        sb.append("=================================================\n\n");

        for (Booking b : bookings) {
            sb.append("-------------------------------------------------\n");
            sb.append(" MÃ ĐƠN     : #").append(b.id).append("\n");
            sb.append(" NGÀY       : ").append(b.date).append("\n");
            sb.append(" KHÁCH HÀNG : ").append(currentUser.fullName).append("\n");
            sb.append(" SĐT        : ").append(currentUser.phone != null ? currentUser.phone : "N/A").append("\n");
            sb.append(" DỊCH VỤ    : ").append(b.serviceName).append("\n");
            sb.append(" THÚ CƯNG   : ").append(b.petName).append("\n");
            sb.append(" TRẠNG THÁI : ").append(b.status).append("\n");
            sb.append(" THÀNH TIỀN : ").append(String.format("%,.0f VND", b.price)).append("\n");
            sb.append("-------------------------------------------------\n\n");
            
            grandTotal += b.price;
        }

        sb.append("=================================================\n");
        sb.append(" TỔNG CỘNG ("+ bookings.size() +" đơn): ").append(String.format("%,.0f VND", grandTotal)).append("\n");
        sb.append("=================================================\n");
        sb.append("          Cảm ơn quý khách đã tin dùng!          \n");

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced", Font.BOLD, 13));
        area.setEditable(false);

        JPanel pnl = new JPanel(new BorderLayout());
        pnl.add(new JScrollPane(area), BorderLayout.CENTER);

        JButton btnPrint = new JButton("XUẤT PDF / IN TẤT CẢ");
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPrint.addActionListener(e -> {
            try {
                JOptionPane.showMessageDialog(this, "Chọn 'Microsoft Print to PDF' hoặc 'Save as PDF' để lưu file.");
                boolean complete = area.print();
                if (complete) JOptionPane.showMessageDialog(this, "Đã xuất thành công!");
            } catch (PrinterException ex) {
                ex.printStackTrace();
            }
        });
        pnl.add(btnPrint, BorderLayout.SOUTH);

        JDialog dialog = new JDialog(this, "Chi Tiết Hóa Đơn (" + bookings.size() + ")", true);
        dialog.setContentPane(pnl);
        dialog.setSize(450, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}