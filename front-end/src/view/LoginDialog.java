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
        gbc.insets = new Insets(10, 0, 10, 0);
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
            JTextField txtTenThat = new JTextField();
            JTextField txtTaiKhoan = new JTextField();
            JPasswordField txtMatKhau = new JPasswordField();

            Object[] message = { "Họ và tên:", txtTenThat, "Tên tài khoản:", txtTaiKhoan, "Mật khẩu:", txtMatKhau };
            int option = JOptionPane.showConfirmDialog(this, message, "Đăng ký tài khoản mới", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String name = txtTenThat.getText().trim();
                String user = txtTaiKhoan.getText().trim();
                String pass = new String(txtMatKhau.getPassword());
                if (!name.isEmpty() && !user.isEmpty() && !pass.isEmpty()) {
                    DataManager.registerCustomer(user, pass, name); 
                    JOptionPane.showMessageDialog(this, "Đăng ký thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        getContentPane().add(mainPanel);
        pack(); setLocationRelativeTo(null);
    }
    public User getAuthenticatedUser() { return authenticatedUser; }
    public boolean isSucceeded() { return succeeded; }
}