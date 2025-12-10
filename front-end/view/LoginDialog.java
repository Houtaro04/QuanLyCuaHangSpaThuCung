package view;

import controller.DataManager;
import model.User;
import utils.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

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
        gbc.insets = new Insets(5, 0, 5, 0); // Giảm khoảng cách chút cho gọn
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

        gbc.gridy++; gbc.insets = new Insets(20, 0, 10, 0);
        JButton btnLogin = UIStyle.createButton("Đăng nhập", "🔓", UIStyle.COLOR_ACCENT);
        card.add(btnLogin, gbc);

        gbc.gridy++; gbc.insets = new Insets(0, 0, 0, 0);
        JButton btnRegister = UIStyle.createButton("Đăng ký tài khoản", "📝", UIStyle.COLOR_SUCCESS);
        card.add(btnRegister, gbc);

        mainPanel.add(card);
        
        btnLogin.addActionListener(e -> {
            User user = DataManager.checkLogin(txtUser.getText(), new String(txtPass.getPassword()));
            if (user != null) { authenticatedUser = user; succeeded = true; dispose(); } 
            else JOptionPane.showMessageDialog(this, "Sai thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        });
        
        // --- XỬ LÝ ĐĂNG KÝ (CẬP NHẬT MỚI) ---
        btnRegister.addActionListener(e -> {
            JTextField txtTenThat = new JTextField();
            JTextField txtTaiKhoan = new JTextField();
            JTextField txtSDT = new JTextField();  // MỚI
            JTextField txtEmail = new JTextField(); // MỚI
            JPasswordField txtMatKhau = new JPasswordField();
            JPasswordField txtMatKhau2 = new JPasswordField(); // MỚI: Nhập lại MK

            Object[] message = { 
                "Họ và tên:", txtTenThat, 
                "Số điện thoại:", txtSDT,
                "Email:", txtEmail,
                "Tên tài khoản:", txtTaiKhoan, 
                "Mật khẩu:", txtMatKhau,
                "Nhập lại mật khẩu:", txtMatKhau2
            };
            
            int option = JOptionPane.showConfirmDialog(this, message, "Đăng ký tài khoản mới", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String name = txtTenThat.getText().trim();
                String phone = txtSDT.getText().trim();
                String email = txtEmail.getText().trim();
                String user = txtTaiKhoan.getText().trim();
                String pass = new String(txtMatKhau.getPassword());
                String rePass = new String(txtMatKhau2.getPassword());

                if (name.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập các thông tin bắt buộc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                } else if (!pass.equals(rePass)) {
                    JOptionPane.showMessageDialog(this, "Mật khẩu nhập lại không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Gọi hàm đăng ký mới có thêm phone, email
                    DataManager.registerCustomer(user, pass, name, phone, email); 
                    JOptionPane.showMessageDialog(this, "Đăng ký thành công!");
                }
            }
        });

        getContentPane().add(mainPanel);
        pack(); setLocationRelativeTo(null);
    }
    public User getAuthenticatedUser() { return authenticatedUser; }
    public boolean isSucceeded() { return succeeded; }
}