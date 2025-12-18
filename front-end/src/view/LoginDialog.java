package view;

import controller.DataManager;
import model.User;
import utils.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Random;

public class LoginDialog extends JDialog {
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
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; 

        JLabel lblIcon = new JLabel(new UIStyle.EmojiIcon("🐾", 60));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblIcon, gbc);

        gbc.gridy++;
        JLabel lblTitle = new JLabel("PET SHOP LOGIN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(UIStyle.COLOR_PRIMARY);
        card.add(lblTitle, gbc);

        gbc.gridy++; card.add(new JLabel("Tài Khoản:"), gbc);
        gbc.gridy++; txtUser = UIStyle.createTextField(); card.add(txtUser, gbc);

        gbc.gridy++; card.add(new JLabel("Mật Khẩu:"), gbc);
        gbc.gridy++; txtPass = new JPasswordField(20);
        txtPass.setBorder(txtUser.getBorder());
        card.add(txtPass, gbc);

        // [MỚI] Nút Quên mật khẩu nằm ngay dưới ô Mật khẩu
        gbc.gridy++; 
        JLabel lblForgot = new JLabel("Quên mật khẩu?");
        lblForgot.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblForgot.setForeground(UIStyle.COLOR_ACCENT);
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblForgot.setHorizontalAlignment(SwingConstants.RIGHT);
        // Sự kiện click Quên Mật Khẩu
        lblForgot.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                processForgotPassword();
            }
        });
        card.add(lblForgot, gbc);

        gbc.gridy++; gbc.insets = new Insets(20, 0, 10, 0);
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
            // (Giữ nguyên logic đăng ký cũ của bạn ở đây)
            registerProcess();
        });

        getContentPane().add(mainPanel);
        pack(); setLocationRelativeTo(null);
    }

    // --- LOGIC XỬ LÝ QUÊN MẬT KHẨU ---
    private void processForgotPassword() {
        // 1. Nhập Username
        String username = JOptionPane.showInputDialog(this, "Nhập tên tài khoản của bạn:");
        if (username == null || username.trim().isEmpty()) return;

        // 2. Nhập Email
        String email = JOptionPane.showInputDialog(this, "Nhập Email đăng ký:");
        if (email == null || email.trim().isEmpty()) return;

        // 3. Kiểm tra trong DB
        if (DataManager.verifyUserEmail(username, email)) {
            // 4. Sinh mã OTP ngẫu nhiên (4 số)
            int otpCode = new Random().nextInt(9000) + 1000;
            
            // 5. GIẢ LẬP GỬI EMAIL: Hiện mã OTP lên màn hình
            JOptionPane.showMessageDialog(this, 
                "Hệ thống đã gửi mã OTP về email: " + email + "\n" +
                "------------------------------------------------\n" +
                "MÃ OTP CỦA BẠN LÀ: " + otpCode + "\n" +
                "------------------------------------------------", 
                "Giả lập Email", JOptionPane.INFORMATION_MESSAGE);

            // 6. Yêu cầu người dùng nhập lại OTP
            String inputOtp = JOptionPane.showInputDialog(this, "Vui lòng nhập mã OTP vừa nhận:");
            
            if (inputOtp != null && inputOtp.equals(String.valueOf(otpCode))) {
                // 7. OTP đúng -> Nhập mật khẩu mới
                String newPass = JOptionPane.showInputDialog(this, "Nhập mật khẩu mới:");
                if (newPass != null && !newPass.isEmpty()) {
                    DataManager.updatePassword(username, newPass);
                    JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công! Hãy đăng nhập lại.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Mã OTP không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Tài khoản hoặc Email không chính xác!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Hàm phụ để code đỡ rối (Copy logic cũ của nút Register vào đây)
    private void registerProcess() {
        JTextField txtTenThat = new JTextField();
        JTextField txtTaiKhoan = new JTextField();
        JTextField txtSDT = new JTextField();
        JTextField txtEmail = new JTextField();
        JPasswordField txtMatKhau = new JPasswordField();
        JPasswordField txtMatKhau2 = new JPasswordField();

        Object[] message = { "Họ và tên:", txtTenThat, "Số điện thoại:", txtSDT, "Email:", txtEmail, "Tên tài khoản:", txtTaiKhoan, "Mật khẩu:", txtMatKhau, "Nhập lại mật khẩu:", txtMatKhau2 };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Đăng ký tài khoản mới", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String name = txtTenThat.getText().trim();
            String phone = txtSDT.getText().trim();
            String email = txtEmail.getText().trim();
            String user = txtTaiKhoan.getText().trim();
            String pass = new String(txtMatKhau.getPassword());
            String rePass = new String(txtMatKhau2.getPassword());

            if (name.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } else if (!pass.equals(rePass)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } else {
                DataManager.registerCustomer(user, pass, name, phone, email); 
                JOptionPane.showMessageDialog(this, "Đăng ký thành công!");
            }
        }
    }

    public User getAuthenticatedUser() { return authenticatedUser; }
    public boolean isSucceeded() { return succeeded; }
}