package ru.zhivoymir.launcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Окно первого запуска «Живого мира»: игрок вводит ник, мы записываем автономный аккаунт
 * в accounts.json PolyMC и запускаем сборку. Если аккаунт уже есть — окно не показывается.
 * Лежит в корне установки «Всё в одном» рядом с polymc.exe.
 */
public final class Main {
    static final String INSTANCE = "ZhivoyMir";
    static final Pattern NICK = Pattern.compile("[A-Za-z0-9_]{3,16}");
    static final List<String> SPLASHES = List.of(
            "Мир, который помнит!", "Бог всё видит!", "Поговори с жителями!",
            "Изумруды не растут на деревьях!", "Колония ждёт строителя!", "Сначала найди деревню!");

    public static void main(String[] args) throws Exception {
        Path root = installDir();
        Path accounts = root.resolve("accounts.json");
        if (args.length > 1 && args[0].equals("--render")) { // снимок окна в файл (для проверки без экрана)
            Menu m = new Menu(root, accounts);
            m.setSize(Menu.W, Menu.H);
            m.doLayout();
            if (args.length > 2) { m.field.setText(args[2]); m.submitCheck(); }
            BufferedImage img = new BufferedImage(Menu.W, Menu.H, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            m.printAll(g);
            g.dispose();
            ImageIO.write(img, "png", new File(args[1]));
            System.exit(0);
        }
        boolean setup = args.length > 0 && args[0].equals("--setup");
        if (!setup && hasAccount(accounts)) {
            launch(root);
            return;
        }
        System.setProperty("awt.useSystemAAFontSettings", "off");
        SwingUtilities.invokeLater(() -> new Window(root, accounts).setVisible(true));
    }

    /** Папка с jar-файлом (корень установки). */
    static Path installDir() throws Exception {
        Path jar = Path.of(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        return Files.isDirectory(jar) ? jar : jar.getParent();
    }

    static boolean hasAccount(Path accounts) {
        try {
            String s = Files.readString(accounts);
            return s.contains("\"profile\"") && (s.contains("\"active\": true") || s.contains("\"active\":true"));
        } catch (IOException e) {
            return false;
        }
    }

    /** Автономный аккаунт в формате PolyMC (accounts.json, formatVersion 3). */
    static void writeAccount(Path accounts, String nick) throws Exception {
        String id = offlineUuid(nick);
        String client = UUID.randomUUID().toString().replace("-", "");
        long now = System.currentTimeMillis() / 1000;
        String json = """
                {
                    "accounts": [
                        {
                            "active": true,
                            "entitlement": {
                                "canPlayMinecraft": true,
                                "ownsMinecraft": true
                            },
                            "profile": {
                                "capes": [
                                ],
                                "id": "%s",
                                "name": "%s",
                                "skin": {
                                    "id": "",
                                    "url": "",
                                    "variant": ""
                                }
                            },
                            "type": "Offline",
                            "ygg": {
                                "extra": {
                                    "clientToken": "%s",
                                    "userName": "%s"
                                },
                                "iat": %d,
                                "token": "offline"
                            }
                        }
                    ],
                    "formatVersion": 3
                }
                """.formatted(id, nick, client, nick, now);
        if (Files.exists(accounts)) Files.copy(accounts, accounts.resolveSibling("accounts.json.bak"),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        Files.writeString(accounts, json, StandardCharsets.UTF_8);
    }

    /** UUID v3 от «OfflinePlayer:ник» — так же его считает сервер в автономном режиме. */
    static String offlineUuid(String nick) throws Exception {
        byte[] md5 = MessageDigest.getInstance("MD5").digest(("OfflinePlayer:" + nick).getBytes(StandardCharsets.UTF_8));
        md5[6] = (byte) ((md5[6] & 0x0f) | 0x30);
        md5[8] = (byte) ((md5[8] & 0x3f) | 0x80);
        StringBuilder sb = new StringBuilder();
        for (byte b : md5) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    static void launch(Path root) throws IOException {
        File exe = root.resolve("polymc.exe").toFile();
        if (!exe.exists()) {
            JOptionPane.showMessageDialog(null, "Не найден лаунчер:\n" + exe, "Живой мир", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new ProcessBuilder(exe.getPath(), "--launch", INSTANCE).directory(root.toFile()).start();
    }

    static Font font(float size) {
        try (InputStream in = Main.class.getResourceAsStream("/font.ttf")) {
            return Font.createFont(Font.TRUETYPE_FONT, in).deriveFont(size);
        } catch (Exception e) {
            return new Font(Font.MONOSPACED, Font.BOLD, (int) size);
        }
    }

    static BufferedImage image(String name) {
        try (InputStream in = Main.class.getResourceAsStream(name)) {
            return in == null ? null : ImageIO.read(in);
        } catch (IOException e) {
            return null;
        }
    }

    /** Окно в духе главного меню Minecraft. */
    static final class Window extends JFrame {
        Window(Path root, Path accounts) {
            super("Живой мир");
            BufferedImage icon = image("/icon.png");
            if (icon != null) setIconImage(icon);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setResizable(false);
            Menu menu = new Menu(root, accounts);
            setContentPane(menu);
            pack();
            setLocationRelativeTo(null);
            addWindowListener(new WindowAdapter() {
                @Override public void windowOpened(WindowEvent e) { menu.field.requestFocusInWindow(); }
            });
        }
    }

    static final class Menu extends JPanel {
        static final int W = 854, H = 480;
        final BufferedImage bg = image("/background.jpg");
        final Font title = font(48f), splash = font(12f), text = font(16f), small = font(8f);
        final String splashText = SPLASHES.get(new Random().nextInt(SPLASHES.size()));
        final JTextField field = new JTextField(16);
        final McButton play = new McButton("Играть");
        final Path root, accounts;
        String hint = "3–16 символов: латинские буквы, цифры, _";
        boolean error;
        long start = System.currentTimeMillis();

        Menu(Path root, Path accounts) {
            this.root = root;
            this.accounts = accounts;
            setLayout(null);
            setPreferredSize(new Dimension(W, H));

            field.setFont(text);
            field.setForeground(new Color(0xE0E0E0));
            field.setCaretColor(new Color(0xE0E0E0));
            field.setBackground(Color.BLACK);
            field.setSelectionColor(new Color(0x0000C0));
            field.setSelectedTextColor(Color.WHITE);
            field.setHorizontalAlignment(JTextField.CENTER);
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xA0A0A0), 2),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)));
            field.setBounds(W / 2 - 150, 268, 300, 40);
            field.addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { recolor(true); }
                @Override public void focusLost(FocusEvent e) { recolor(false); }
            });
            ((javax.swing.text.AbstractDocument) field.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
                @Override public void insertString(FilterBypass fb, int off, String s, javax.swing.text.AttributeSet a)
                        throws javax.swing.text.BadLocationException { replace(fb, off, 0, s, a); }
                @Override public void replace(FilterBypass fb, int off, int len, String s, javax.swing.text.AttributeSet a)
                        throws javax.swing.text.BadLocationException {
                    String clean = s == null ? "" : s.replaceAll("[^A-Za-z0-9_]", "");
                    if (fb.getDocument().getLength() - len + clean.length() <= 16) super.replace(fb, off, len, clean, a);
                    if (!clean.equals(s)) { hint = "Только латинские буквы, цифры и _"; error = true; repaint(); }
                }
            });
            field.addActionListener(e -> submit());
            add(field);

            play.setFont(text);
            play.setBounds(W / 2 - 150, 322, 300, 40);
            play.addActionListener(e -> submit());
            add(play);

            new Timer(50, e -> repaint(0, 0, W, 200)).start();
        }

        void recolor(boolean focus) {
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(focus ? Color.WHITE : new Color(0xA0A0A0), 2),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        }

        /** Проверка ника; при ошибке — красная подсказка и null. */
        String submitCheck() {
            String nick = field.getText().trim();
            if (NICK.matcher(nick).matches()) return nick;
            hint = nick.length() < 3 ? "Ник слишком короткий: нужно от 3 символов" : "3–16 символов: латинские буквы, цифры, _";
            error = true;
            repaint();
            return null;
        }

        void submit() {
            String nick = submitCheck();
            if (nick == null) {
                field.requestFocusInWindow();
                return;
            }
            try {
                writeAccount(accounts, nick);
                launch(root);
                System.exit(0);
            } catch (Exception ex) {
                hint = "Не удалось сохранить ник: " + ex.getMessage();
                error = true;
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            if (bg != null) {
                double s = Math.max((double) W / bg.getWidth(), (double) H / bg.getHeight());
                int w = (int) (bg.getWidth() * s), h = (int) (bg.getHeight() * s);
                g.drawImage(bg, (W - w) / 2, (H - h) / 2, w, h, null);
            } else {
                g.setColor(new Color(0x2B1E14));
                g.fillRect(0, 0, W, H);
            }
            // затемнение снизу, чтобы поле и кнопка читались
            g.setPaint(new GradientPaint(0, 180, new Color(0, 0, 0, 0), 0, H, new Color(0, 0, 0, 170)));
            g.fillRect(0, 180, W, H - 180);

            // заголовок с тенью, как у надписей Minecraft
            g.setFont(title);
            String t = "ЖИВОЙ МИР";
            int tw = g.getFontMetrics().stringWidth(t), tx = (W - tw) / 2, ty = 110;
            g.setColor(new Color(0x3F2A10));
            g.drawString(t, tx + 4, ty + 4);
            g.setColor(new Color(0xFFF2D0));
            g.drawString(t, tx, ty);

            // жёлтая надпись-заставка, наклонённая и пульсирующая
            double pulse = 1.0 + 0.06 * Math.abs(Math.sin((System.currentTimeMillis() - start) / 300.0));
            AffineTransform old = g.getTransform();
            g.translate(tx + tw - 10, ty + 14);
            g.rotate(Math.toRadians(-18));
            g.scale(pulse, pulse);
            g.setFont(splash);
            int sw = g.getFontMetrics().stringWidth(splashText);
            g.setColor(new Color(0x3F3F00));
            g.drawString(splashText, -sw / 2 + 2, 2);
            g.setColor(new Color(0xFFFF00));
            g.drawString(splashText, -sw / 2, 0);
            g.setTransform(old);

            g.setFont(text);
            shadowed(g, "Как тебя зовут, странник?", W / 2, 254, new Color(0xFFFFFF), true);
            g.setFont(small);
            shadowed(g, hint, W / 2, 386, error ? new Color(0xFF5555) : new Color(0xA0A0A0), true);
            shadowed(g, "Minecraft 1.21.1 · NeoForge", 6, H - 8, new Color(0xFFFFFF), false);
            shadowed(g, "Ник запоминается — дальше игра запускается сразу", W - 6, H - 8, new Color(0xFFFFFF), false);
        }

        static void shadowed(Graphics2D g, String s, int x, int y, Color c, boolean center) {
            int w = g.getFontMetrics().stringWidth(s);
            int px = center ? x - w / 2 : (x > W / 2 ? x - w : x);
            g.setColor(new Color(c.getRed() / 4, c.getGreen() / 4, c.getBlue() / 4));
            g.drawString(s, px + 2, y + 2);
            g.setColor(c);
            g.drawString(s, px, y);
        }
    }

    /** Кнопка в виде каменной кнопки Minecraft. */
    static final class McButton extends JButton {
        McButton(String label) {
            super(label);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setRolloverEnabled(true);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0;
            int w = getWidth(), h = getHeight();
            boolean hover = getModel().isRollover() || isFocusOwner();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, w, h);
            g.setColor(hover ? Color.WHITE : new Color(0x2A2A2A));
            g.fillRect(0, 0, w, h);
            g.setColor(hover ? new Color(0x8A8FC8) : new Color(0x707070));
            g.fillRect(2, 2, w - 4, h - 4);
            g.setColor(hover ? new Color(0xA8ADE8) : new Color(0x8C8C8C));
            g.fillRect(2, 2, w - 4, 3);
            g.setColor(hover ? new Color(0x5A5F98) : new Color(0x4C4C4C));
            g.fillRect(2, h - 5, w - 4, 3);
            g.setFont(getFont());
            FontMetrics fm = g.getFontMetrics();
            int x = (w - fm.stringWidth(getText())) / 2, y = (h + fm.getAscent() - fm.getDescent()) / 2;
            g.setColor(new Color(0x3F3F3F));
            g.drawString(getText(), x + 2, y + 2);
            g.setColor(hover ? new Color(0xFFFFA0) : new Color(0xE0E0E0));
            g.drawString(getText(), x, y);
        }
    }
}
