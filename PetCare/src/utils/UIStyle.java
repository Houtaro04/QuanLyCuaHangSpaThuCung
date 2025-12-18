package utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UIStyle {
    public static final Color COLOR_PRIMARY = new Color(44, 62, 80);    
    public static final Color COLOR_ACCENT = new Color(52, 152, 219);   
    public static final Color COLOR_SUCCESS = new Color(39, 174, 96);   
    public static final Color COLOR_WARNING = new Color(243, 156, 18);  
    public static final Color COLOR_DANGER = new Color(192, 57, 43);    
    public static final Color COLOR_BG = new Color(236, 240, 241);      
    public static final Color COLOR_TEXT = new Color(44, 62, 80);

    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    // Class EmojiIcon nội bộ
    public static class EmojiIcon implements Icon {
        private String emoji;
        private Font font;
        private int size;

        public EmojiIcon(String emoji, int size) {
            this.emoji = emoji;
            this.size = size;
            this.font = new Font("Segoe UI Emoji", Font.PLAIN, size);
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(font);
            g2.setColor(c.getForeground());
            g2.drawString(emoji, x, y + size - 2);
            g2.dispose();
        }

        @Override public int getIconWidth() { return size + 4; }
        @Override public int getIconHeight() { return size; }
    }

    public static JButton createButton(String text, String icon, Color bg) {
        JButton btn = new JButton(text.toUpperCase());
        if (icon != null && !icon.isEmpty()) {
            btn.setIcon(new EmojiIcon(icon, 18));
            btn.setIconTextGap(8);
        }
        btn.setFont(FONT_BUTTON); 
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK);
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
        JTextField txt = new JTextField(20);
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