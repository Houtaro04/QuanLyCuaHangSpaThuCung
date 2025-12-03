import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

// ==========================================
// 1. DATA ENTITIES (Giữ nguyên)
// ==========================================
class User {
    String username, password, role, fullName;
    public User(String u, String p, String r, String name) { 
        username = u; password = p; role = r; fullName = name;
    }
}

class Pet {
    int id; String name, species; int age; String ownerUsername;
    public Pet(int id, String n, String s, int a, String oUser) {
        this.id = id; name = n; species = s; age = a; ownerUsername = oUser;
    }
    @Override public String toString() { return name + " (" + species + ")"; } 
}

class Service {
    int id; String name; double price;
    public Service(int id, String name, double price) { this.id = id; this.name = name; this.price = price; }
    @Override public String toString() { return name + " - " + String.format("%,.0f đ", price); }
}

class Booking {
    int id; String customerUser, petName, serviceName, status, date; double price;
    public Booking(int id, String cUser, String pName, String sName, double pr, String st, String d) {
        this.id = id; customerUser = cUser; petName = pName; serviceName = sName; price = pr; status = st; date = d;
    }
}

// ==========================================
// 2. DATA MANAGER (Cập nhật đầy đủ)
// ==========================================
class DataManager {
    public static Vector<User> mockUsers = new Vector<>();
    public static Vector<Pet> mockPets = new Vector<>();
    public static Vector<Service> mockServices = new Vector<>();
    public static Vector<Booking> mockBookings = new Vector<>();

    private static int petIdCounter = 0, serviceIdCounter = 0, bookingIdCounter = 0;

    static {
        mockUsers.add(new User("admin", "123", "ADMIN", "Quản Lý Cửa Hàng"));
        mockUsers.add(new User("khach", "123", "CUSTOMER", "Nguyễn Văn Khách"));

        addService("Tắm gội thơm tho", 150000);
        addService("Cắt tỉa tạo kiểu", 250000);
        addService("Combo VIP (Tắm+Cắt)", 350000);
        addService("Tiêm phòng", 100000);
        
        addPet("Mimi", "Mèo Anh", 2, "khach");
        addPet("LuLu", "Poodle", 4, "khach");
    }

    public static User checkLogin(String u, String p) {
        for (User user : mockUsers) if (user.username.equals(u) && user.password.equals(p)) return user;
        return null;
    }
    public static void registerCustomer(String u, String p, String name) { mockUsers.add(new User(u, p, "CUSTOMER", name)); }
    
    // --- CÁC HÀM THÊM ---
    public static void addService(String name, double price) { mockServices.add(new Service(++serviceIdCounter, name, price)); }
    public static void addPet(String name, String species, int age, String ownerUser) { mockPets.add(new Pet(++petIdCounter, name, species, age, ownerUser)); }
    public static void addBooking(String user, String pName, String sName, double price, Date date, String time) {
        // Định dạng ngày thành chuỗi dd/MM/yyyy (ví dụ: 25/12/2025)
        String dateStr = new SimpleDateFormat("dd/MM/yyyy").format(date);
        
        // Ghép ngày và giờ thành chuỗi hoàn chỉnh
        String finalDateTime = dateStr + " " + time;
        
        mockBookings.add(new Booking(++bookingIdCounter, user, pName, sName, price, "Chờ duyệt", finalDateTime));
    }

    // --- CÁC HÀM CẬP NHẬT/XÓA (ĐÂY LÀ PHẦN BẠN ĐANG THIẾU) ---
    public static void deletePet(int id) {
        mockPets.removeIf(p -> p.id == id);
    }

    public static void deleteService(int id) {
        mockServices.removeIf(s -> s.id == id);
    }

    public static void updateBookingStatus(int id, String newStatus) {
        for(Booking b : mockBookings) if(b.id == id) { b.status = newStatus; break; }
    }
    
    public static Booking getBookingById(int id) {
        for(Booking b : mockBookings) if(b.id == id) return b;
        return null;
    }
    
    // --- CÁC HÀM LOAD DỮ LIỆU ---
    public static void loadPetsToTable(DefaultTableModel model, String viewerUser, String role) {
        model.setRowCount(0);
        for (Pet p : mockPets) if (role.equals("ADMIN") || p.ownerUsername.equals(viewerUser)) 
            model.addRow(new Object[]{p.id, p.name, p.species, p.age + " tuổi", p.ownerUsername});
    }
    public static void loadServicesToTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Service s : mockServices) model.addRow(new Object[]{s.id, s.name, String.format("%,.0f đ", s.price)});
    }
    public static void loadBookingsToTable(DefaultTableModel model, String viewerUser, String role) {
        model.setRowCount(0);
        for (Booking b : mockBookings) if (role.equals("ADMIN") || b.customerUser.equals(viewerUser)) 
            model.addRow(new Object[]{b.id, b.customerUser, b.petName, b.serviceName, String.format("%,.0f đ", b.price), b.date, b.status});
    }
    public static Vector<Pet> getPetsByOwner(String ownerUser) {
        Vector<Pet> list = new Vector<>();
        for(Pet p : mockPets) if(p.ownerUsername.equals(ownerUser)) list.add(p);
        return list;
    }
    public static void searchPetsToTable(DefaultTableModel model, String keyword) {
    model.setRowCount(0);
    String key = keyword.toLowerCase(); // Chuyển về chữ thường để tìm kiếm không phân biệt hoa thường
    for (Pet p : mockPets) {
        // Tìm kiếm theo Tên, Loài hoặc Chủ sở hữu
        if (p.name.toLowerCase().contains(key) || 
            p.species.toLowerCase().contains(key) || 
            p.ownerUsername.toLowerCase().contains(key)) {
            
            model.addRow(new Object[]{p.id, p.name, p.species, p.age + " tuổi", p.ownerUsername});
        }
    }
}
}

// ==========================================
// 3. UI HELPER & STYLES (CẬP NHẬT: EMOJI ICON RIÊNG BIỆT)
// ==========================================
class UIStyle {
    public static final Color COLOR_PRIMARY = new Color(44, 62, 80);    
    public static final Color COLOR_ACCENT = new Color(52, 152, 219);   
    public static final Color COLOR_SUCCESS = new Color(39, 174, 96);   
    public static final Color COLOR_WARNING = new Color(243, 156, 18);  
    public static final Color COLOR_DANGER = new Color(192, 57, 43);    
    public static final Color COLOR_BG = new Color(236, 240, 241);      
    public static final Color COLOR_TEXT = new Color(44, 62, 80);

    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13); // Font chữ thường, hỗ trợ tiếng Việt

    // --- CLASS MỚI: Dùng để vẽ Emoji như một Icon ảnh ---
    // Giúp hiển thị icon đẹp mà không làm lỗi font chữ
    static class EmojiIcon implements Icon {
        private String emoji;
        private Font font;
        private int size;

        public EmojiIcon(String emoji, int size) {
            this.emoji = emoji;
            this.size = size;
            // Ép dùng font Emoji của Windows
            this.font = new Font("Segoe UI Emoji", Font.PLAIN, size);
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(font);
            g2.setColor(c.getForeground()); // Lấy màu của component cha
            // Vẽ emoji
            g2.drawString(emoji, x, y + size - 2);
            g2.dispose();
        }

        @Override public int getIconWidth() { return size + 4; }
        @Override public int getIconHeight() { return size; }
    }

    public static JButton createButton(String text, String icon, Color bg) {
        JButton btn = new JButton(text.toUpperCase());
        
        // --- SỬ DỤNG EMOJI ICON ---
        // Icon và Text được tách riêng, Swing tự động xử lý cùng dòng
        if (icon != null && !icon.isEmpty()) {
            btn.setIcon(new EmojiIcon(icon, 18)); // Icon kích thước 18
            btn.setIconTextGap(8); // Khoảng cách giữa icon và chữ
        }

        btn.setFont(FONT_BUTTON); 
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK); // Chữ màu đen
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(bg.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(bg); }
        });
        return btn;
    }

    public static JTextField createTextField() {
        JTextField txt = new JTextField(20); // Độ rộng vừa phải
        txt.setFont(FONT_NORMAL);
        txt.setForeground(Color.BLACK);
        txt.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(189, 195, 199), 1), 
            new EmptyBorder(5, 10, 5, 10)));
        return txt;
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_NORMAL);
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(223, 230, 233));
        table.setSelectionForeground(Color.BLACK);
        table.setForeground(Color.BLACK);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(236, 240, 241));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setReorderingAllowed(false);
        header.setBackground(new Color(230, 230, 230));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 40));
        
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setForeground(Color.BLACK);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}

// ==========================================
// 4. LOGIN SCREEN
// ==========================================
class LoginDialog extends JDialog {
    private boolean succeeded = false;
    private User authenticatedUser = null;
    private JTextField txtUser;
    private JPasswordField txtPass;

    public LoginDialog(Frame parent) {
        super(parent, "Đăng Nhập Hệ Thống", true);
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UIStyle.COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200,200,200), 1),
            new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; 

        // Logo dùng EmojiIcon để vẽ hình to đẹp
        JLabel lblIcon = new JLabel(new UIStyle.EmojiIcon("🐾", 60));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblIcon, gbc);

        gbc.gridy++;
        JLabel lblTitle = new JLabel("PET SHOP LOGIN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(UIStyle.COLOR_PRIMARY);
        card.add(lblTitle, gbc);

        gbc.gridy++;
        card.add(new JLabel("Tài Khoản:"), gbc);
        gbc.gridy++;
        txtUser = UIStyle.createTextField();
        card.add(txtUser, gbc);

        gbc.gridy++;
        card.add(new JLabel("Mật Khẩu:"), gbc);
        gbc.gridy++;
        txtPass = new JPasswordField(20);
        txtPass.setBorder(txtUser.getBorder());
        txtPass.setFont(UIStyle.FONT_NORMAL);
        card.add(txtPass, gbc);

        gbc.gridy++; gbc.insets = new Insets(25, 0, 10, 0);
        JButton btnLogin = UIStyle.createButton("Đăng nhập", "🔓", UIStyle.COLOR_ACCENT);
        card.add(btnLogin, gbc);

        gbc.gridy++; gbc.insets = new Insets(0, 0, 0, 0);
        JButton btnRegister = UIStyle.createButton("Đăng ký", "📝", UIStyle.COLOR_SUCCESS);
        card.add(btnRegister, gbc);

        mainPanel.add(card);
        
        btnLogin.addActionListener(e -> {
            User user = DataManager.checkLogin(txtUser.getText(), new String(txtPass.getPassword()));
            if (user != null) { authenticatedUser = user; succeeded = true; dispose(); } 
            else JOptionPane.showMessageDialog(this, "Sai thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        });
        
        btnRegister.addActionListener(e -> {
            String u = JOptionPane.showInputDialog("Nhập tên tài khoản mới:");
            if(u!=null && !u.isEmpty()) DataManager.registerCustomer(u, "123", "Khách Mới");
            JOptionPane.showMessageDialog(this, "Đăng ký thành công! Mật khẩu mặc định: 123");
        });

        getContentPane().add(mainPanel);
        pack(); setLocationRelativeTo(null);
    }
    public User getAuthenticatedUser() { return authenticatedUser; }
    public boolean isSucceeded() { return succeeded; }
}

// ==========================================
// 5. MAIN APP
// ==========================================
public class PetManagementApp extends JFrame {
    private User currentUser;

    public PetManagementApp(User user) {
        this.currentUser = user;
        setTitle("Hệ Thống Quản Lý - " + user.fullName);
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIStyle.COLOR_PRIMARY);
        header.setPreferredSize(new Dimension(1200, 70));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel title = new JLabel("PET CARE SYSTEM");
        title.setIcon(new UIStyle.EmojiIcon("🐾", 28)); // Dùng icon emoji
        title.setIconTextGap(15);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24)); 
        
        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        userInfo.setOpaque(false);
        JLabel lblUser = new JLabel("Xin chào, " + user.fullName + " (" + user.role + ")");
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(UIStyle.FONT_NORMAL);
        
        JButton btnLogout = UIStyle.createButton("Đăng Xuất", "🚪", UIStyle.COLOR_DANGER);
        // Không setPreferredSize để nút tự dãn
        btnLogout.setForeground(Color.BLACK);
        btnLogout.addActionListener(e -> { dispose(); main(null); });

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

        // Dùng EmojiIcon cho Tabs để đẹp và chuẩn
        if (user.role.equals("ADMIN")) {
            tabs.addTab(" QUẢN LÝ ĐẶT LỊCH ", new UIStyle.EmojiIcon("📅", 20), createBookingMgmtPanel());
            tabs.addTab(" QUẢN LÝ DỊCH VỤ ", new UIStyle.EmojiIcon("✂️", 20), createServiceMgmtPanel());
            tabs.addTab(" DANH SÁCH THÚ CƯNG ", new UIStyle.EmojiIcon("🐶", 20), createPetListPanel());
        } else {
            tabs.addTab(" ĐẶT LỊCH DỊCH VỤ ", new UIStyle.EmojiIcon("✨", 20), createCustomerBookingPanel());
            tabs.addTab(" THÚ CƯNG CỦA TÔI ", new UIStyle.EmojiIcon("🐕", 20), createMyPetPanel());
        }

        add(tabs, BorderLayout.CENTER);
    }

    // --- PANEL: CUSTOMER BOOKING ---
    private JPanel createCustomerBookingPanel() {
        JPanel container = new JPanel(new BorderLayout(20, 20));
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        container.setBackground(UIStyle.COLOR_BG);
        
        // --- FORM SECTION ---
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        
        TitledBorder border = BorderFactory.createTitledBorder(
            new LineBorder(UIStyle.COLOR_ACCENT, 1, true), 
            " ĐĂNG KÝ DỊCH VỤ MỚI ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
            new Font("Segoe UI", Font.BOLD, 14), UIStyle.COLOR_ACCENT);
        form.setBorder(border);
        
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 15, 10, 15); g.fill = GridBagConstraints.HORIZONTAL;

        // --- CÁC THÀNH PHẦN NHẬP LIỆU ---
        JComboBox<Pet> cbMyPets = new JComboBox<>();
        JComboBox<Service> cbServices = new JComboBox<>();
        
        // 1. Tạo ô chọn NGÀY (JSpinner)
        // Lấy thời điểm 00:00:00 của ngày hôm nay để làm mốc
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        Date today = calendar.getTime();

        // Cấu hình Model:
        // - value: today (mặc định hiển thị hôm nay)
        // - start: today (không cho lùi về quá khứ)
        // - end: null (không giới hạn tương lai)
        // - step: Calendar.DAY_OF_MONTH (bước nhảy theo ngày)
        SpinnerDateModel dateModel = new SpinnerDateModel(today, today, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner txtDate = new JSpinner(dateModel);
        
        // Định dạng hiển thị ngày/tháng/năm
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(txtDate, "dd/MM/yyyy");
        txtDate.setEditor(dateEditor);
        txtDate.setPreferredSize(new Dimension(200, 35));

        // 2. Tạo ô chọn GIỜ (JComboBox)
        String[] timeSlots = {"08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", 
                            "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"};
        JComboBox<String> cbTime = new JComboBox<>(timeSlots);
        cbTime.setPreferredSize(new Dimension(200, 35));

        cbMyPets.setPreferredSize(new Dimension(200, 35));
        cbServices.setPreferredSize(new Dimension(200, 35));

        // --- BỐ CỤC GIAO DIỆN ---
        // Dòng 1: Thú cưng + Nút refresh
        g.gridx=0; g.gridy=0; form.add(new JLabel("Chọn Thú Cưng:"), g);
        
        JPanel pnlPetSelect = new JPanel(new BorderLayout(5, 0));
        pnlPetSelect.setBackground(Color.WHITE);
        pnlPetSelect.add(cbMyPets, BorderLayout.CENTER);
        
        JButton btnRefreshData = new JButton("🔄");
        btnRefreshData.setToolTipText("Cập nhật danh sách");
        btnRefreshData.setBackground(Color.WHITE);
        btnRefreshData.setBorder(new LineBorder(new Color(200,200,200)));
        btnRefreshData.setPreferredSize(new Dimension(35, 35));
        pnlPetSelect.add(btnRefreshData, BorderLayout.EAST);
        g.gridx=1; form.add(pnlPetSelect, g);

        // Dòng 2: Dịch vụ
        g.gridx=0; g.gridy=1; form.add(new JLabel("Chọn Dịch Vụ:"), g);
        g.gridx=1; form.add(cbServices, g);

        // Dòng 3: Chọn Ngày (MỚI)
        g.gridx=0; g.gridy=2; form.add(new JLabel("Ngày Hẹn:"), g);
        g.gridx=1; form.add(txtDate, g);

        // Dòng 4: Chọn Giờ
        g.gridx=0; g.gridy=3; form.add(new JLabel("Giờ Hẹn:"), g);
        g.gridx=1; form.add(cbTime, g);
        
        // Dòng 5: Nút Gửi
        JButton btnBook = UIStyle.createButton("GỬI YÊU CẦU", "📩", UIStyle.COLOR_SUCCESS);
        g.gridx=0; g.gridy=4; g.gridwidth=2; g.insets = new Insets(20, 15, 10, 15);
        form.add(btnBook, g);

        // --- TABLE SECTION (Chỉ Xem) ---
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Tài Khoản", "Thú Cưng", "Dịch Vụ", "Giá", "Thời Gian Hẹn", "Trạng Thái"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        UIStyle.styleTable(table);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new LineBorder(new Color(200,200,200)));
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnViewInvoice = UIStyle.createButton("XEM HÓA ĐƠN", "📄", UIStyle.COLOR_WARNING);
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
            if(selectedPet != null) {} 
            
            cbServices.removeAllItems(); 
            for(Service s : DataManager.mockServices) cbServices.addItem(s);
            
            DataManager.loadBookingsToTable(model, currentUser.username, currentUser.role);
        };
        
        reload.run();

        btnRefreshData.addActionListener(e -> {
            reload.run();
            JOptionPane.showMessageDialog(this, "Đã cập nhật dữ liệu!");
        });

        // --- XỬ LÝ SỰ KIỆN GỬI YÊU CẦU ---
        btnBook.addActionListener(e -> {
            Pet pt = (Pet)cbMyPets.getSelectedItem(); 
            Service sv = (Service)cbServices.getSelectedItem();
            Date date = (Date)txtDate.getValue(); // Lấy ngày từ Spinner
            String time = (String)cbTime.getSelectedItem(); // Lấy giờ

            if(pt == null) { JOptionPane.showMessageDialog(this, "Chưa có thú cưng!"); return; }
            
            // Gọi hàm addBooking mới
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

    // --- OTHER PANELS ---
    // --- PANEL: KHÁCH HÀNG - QUẢN LÝ PET CÁ NHÂN ---
    private JPanel createMyPetPanel() {
        JPanel container = new JPanel(new BorderLayout(20, 20));
        container.setBorder(new EmptyBorder(20, 20, 20, 20));
        container.setBackground(UIStyle.COLOR_BG);

        // FORM SECTION (LEFT)
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setPreferredSize(new Dimension(300, 0)); // Cố định chiều rộng form
        form.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(UIStyle.COLOR_ACCENT, 1, true), 
            " THÔNG TIN THÚ CƯNG ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
            UIStyle.FONT_HEADER, UIStyle.COLOR_ACCENT));
        
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10); 
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; 

        JTextField txtName = UIStyle.createTextField();
        JTextField txtSpecies = UIStyle.createTextField();
        JTextField txtAge = UIStyle.createTextField();
        
        int row = 0;
        g.gridy = row++; form.add(new JLabel("Tên thú cưng:"), g);
        g.gridy = row++; form.add(txtName, g);
        
        g.gridy = row++; form.add(new JLabel("Loài:"), g);
        g.gridy = row++; form.add(txtSpecies, g);
        
        g.gridy = row++; form.add(new JLabel("Tuổi:"), g);
        g.gridy = row++; form.add(txtAge, g);

        JButton btnAdd = UIStyle.createButton("THÊM MỚI", "➕", UIStyle.COLOR_ACCENT);
        g.gridy = row++; 
        g.insets = new Insets(20, 10, 10, 10); // Cách xa một chút
        form.add(btnAdd, g);

        // Đẩy toàn bộ nội dung lên trên cùng
        g.gridy = 99; g.weighty = 1.0; 
        form.add(new JPanel(){{setOpaque(false);}}, g);

        // TABLE SECTION (RIGHT)
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Tên", "Loài", "Tuổi", "Chủ"}, 0){
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        UIStyle.styleTable(table);
        DataManager.loadPetsToTable(model, currentUser.username, currentUser.role);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new LineBorder(new Color(200,200,200)));
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Action
        btnAdd.addActionListener(e -> { 
            try { 
                DataManager.addPet(txtName.getText(), txtSpecies.getText(), Integer.parseInt(txtAge.getText()), currentUser.username); 
                DataManager.loadPetsToTable(model, currentUser.username, currentUser.role); 
                txtName.setText(""); txtSpecies.setText(""); txtAge.setText("");
                JOptionPane.showMessageDialog(this, "Đã thêm thú cưng thành công!");
            } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Tuổi phải là số!"); }
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

    // --- PANEL: ADMIN - DANH SÁCH THÚ CƯNG (CHỈ XEM VÀ TÌM KIẾM) ---
    private JPanel createPetListPanel() {
        // Container chính
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBorder(new EmptyBorder(20, 20, 20, 20)); // Căn lề rộng rãi
        container.setBackground(UIStyle.COLOR_BG);

        // ============================
        // 1. TOP: THANH TÌM KIẾM
        // ============================
        JPanel searchPanel = new JPanel(new BorderLayout(15, 0));
        searchPanel.setOpaque(false); // Trong suốt để lấy màu nền của container
        
        JLabel lblSearch = new JLabel("Nhập thông tin tìm kiếm:");
        lblSearch.setFont(UIStyle.FONT_NORMAL);
        
        JTextField txtSearch = UIStyle.createTextField();
        
        JButton btnSearch = UIStyle.createButton("TÌM KIẾM", "🔍", UIStyle.COLOR_PRIMARY);
        btnSearch.setPreferredSize(new Dimension(150, 40)); // Nút to rõ
        
        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);

        // ============================
        // 2. CENTER: BẢNG DỮ LIỆU
        // ============================
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Tên Thú Cưng", "Loài", "Tuổi", "Chủ Sở Hữu"}, 0){
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        UIStyle.styleTable(table);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // ============================
        // 3. LOGIC
        // ============================
        // Load toàn bộ dữ liệu ban đầu
        DataManager.loadPetsToTable(model, "admin", "ADMIN");

        // Sự kiện nút Tìm kiếm
        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            if (keyword.isEmpty()) {
                // Nếu ô tìm kiếm rỗng, load lại tất cả
                DataManager.loadPetsToTable(model, "admin", "ADMIN");
            } else {
                // Nếu có từ khóa, gọi hàm tìm kiếm mới
                DataManager.searchPetsToTable(model, keyword);
            }
        }); 

        // Add vào container
        container.add(searchPanel, BorderLayout.NORTH);
        container.add(scrollPane, BorderLayout.CENTER);
        
        return container;
    }

    private void showInvoice(Booking b) {
        String txt = 
            "================================\n" +
            "       HÓA ĐƠN THANH TOÁN       \n" +
            "================================\n\n" +
            " Mã Đơn   : #" + b.id + "\n" +
            " Ngày     : " + b.date + "\n" +
            " Khách    : " + currentUser.fullName + "\n" +
            "--------------------------------\n" +
            " Dịch Vụ  : " + b.serviceName + "\n" +
            " Thú Cưng : " + b.petName + "\n" +
            "--------------------------------\n" +
            " TỔNG TIỀN: " + String.format("%,.0f VND", b.price) + "\n\n" +
            "================================\n" +
            "   Cảm ơn quý khách đã tin dùng!  ";
        JTextArea area = new JTextArea(txt); area.setFont(new Font("Monospaced", Font.BOLD, 14)); area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Hóa Đơn", JOptionPane.PLAIN_MESSAGE);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> {
            LoginDialog login = new LoginDialog(null);
            login.setVisible(true);
            if (login.isSucceeded()) new PetManagementApp(login.getAuthenticatedUser()).setVisible(true);
        });
    }
}