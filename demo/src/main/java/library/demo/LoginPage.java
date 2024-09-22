package library.demo;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class LoginPage extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton btnLogin;
    private JButton btnRegister;
    private JButton btnExit;
    private JPanel loginPanel;
    private Long userId;
    private String fullName;
    private String lastLoginTime;
    private String lastLogoutTime;

    public LoginPage(JFrame parent) {
        super(parent);
        setTitle("Giriş Ekranı");
        setMinimumSize(new Dimension(600, 400));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        // Username field
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        loginPanel.add(new JLabel("Kullanıcı Adı:"), constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        usernameField = new JTextField(20);
        usernameField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        loginPanel.add(usernameField, constraints);

        // Password field
        constraints.gridx = 0;
        constraints.gridy = 1;
        loginPanel.add(new JLabel("Şifre:"), constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        passwordField = new JPasswordField(20);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        loginPanel.add(passwordField, constraints);

        // Buttons
        btnLogin = new JButton("Giriş");
        btnLogin.setPreferredSize(new Dimension(100, 40));
        btnLogin.setBackground(new Color(0, 123, 255));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder());

        btnRegister = new JButton("Kayıt Ol");
        btnRegister.setPreferredSize(new Dimension(100, 40));
        btnRegister.setBackground(new Color(40, 167, 69));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setBorder(BorderFactory.createEmptyBorder());

        btnExit = new JButton("İptal");
        btnExit.setPreferredSize(new Dimension(100, 40));
        btnExit.setBackground(new Color(220, 53, 69));
        btnExit.setForeground(Color.WHITE);
        btnExit.setFocusPainted(false);
        btnExit.setBorder(BorderFactory.createEmptyBorder());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnExit);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        loginPanel.add(buttonPanel, constraints);

        setContentPane(loginPanel);

        // Button listeners
        btnLogin.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    return authenticate(username, password);
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            dispose();
                            new MainPage(userId, fullName, lastLoginTime, lastLogoutTime).setVisible(true);
                            System.out.println("Giriş kayıt edildi " +  "ID= " + userId);
                        } else {
                            JOptionPane.showMessageDialog(LoginPage.this, "Kullanıcı bulunamadı", "Hata", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(LoginPage.this, "Giriş işlemi sırasında bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        btnRegister.addActionListener(e -> {
            dispose(); // Mevcut pencereyi kapat
            new RegisterPage(parent); // Kayıt sayfasını aç
        });

        btnExit.addActionListener(e -> dispose());

        setVisible(true);
    }

    private boolean authenticate(String username, String password) {
        boolean isValidUser = false;
        try {
            HttpClient client = HttpClient.newHttpClient();
            Map<String, String> loginData = new HashMap<>();
            loginData.put("username", username);
            loginData.put("password", password);
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(loginData);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/users/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Map<String, Object> responseBody = objectMapper.readValue(response.body(), HashMap.class);
                if (responseBody.get("userId") != null) {
                    userId = ((Number) responseBody.get("userId")).longValue();
                    fullName = responseBody.get("fullName") != null ? responseBody.get("fullName").toString() : "";
                    lastLoginTime = responseBody.get("lastLoginTime") != null ? responseBody.get("lastLoginTime").toString() : "";
                    lastLogoutTime = responseBody.get("lastLogoutTime") != null ? responseBody.get("lastLogoutTime").toString() : "";
                    isValidUser = true;
                }
            } else {
                System.out.println("Giriş başarısız: " + response.body());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return isValidUser;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage(null));
    }
}
