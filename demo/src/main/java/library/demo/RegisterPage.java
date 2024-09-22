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

public class RegisterPage extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JButton btnRegister;
    private JButton btnCancel;

    public RegisterPage(JFrame parent) {
        super(parent);
        setTitle("Kayıt Ol");
        setMinimumSize(new Dimension(400, 300));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;


        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        registerPanel.add(new JLabel("Kullanıcı Adı:"), constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        usernameField = new JTextField(20);
        usernameField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        registerPanel.add(usernameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        registerPanel.add(new JLabel("Şifre:"), constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        passwordField = new JPasswordField(20);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        registerPanel.add(passwordField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        registerPanel.add(new JLabel("Ad Soyad:"), constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        fullNameField = new JTextField(20);
        fullNameField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        registerPanel.add(fullNameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        registerPanel.add(new JLabel("E-posta(Gmail):"), constraints);

        constraints.gridx = 1;
        constraints.gridy = 3;
        emailField = new JTextField(20);
        emailField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        registerPanel.add(emailField, constraints);

        btnRegister = new JButton("Kayıt Ol");
        btnRegister.setPreferredSize(new Dimension(100, 40));
        btnRegister.setBackground(new Color(0, 123, 255));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setBorder(BorderFactory.createEmptyBorder());

        btnCancel = new JButton("Geri");
        btnCancel.setPreferredSize(new Dimension(100, 40));
        btnCancel.setBackground(new Color(220, 53, 69));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(BorderFactory.createEmptyBorder());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnCancel);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        registerPanel.add(buttonPanel, constraints);

        setContentPane(registerPanel);

        btnRegister.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String fullName = fullNameField.getText();
            String email = emailField.getText();

            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(RegisterPage.this, "Lütfen tüm alanları doldurun.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    return registerUser(username, password, fullName, email);
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(RegisterPage.this, "Kayıt başarılı! Lütfen e-posta adresinize gönderilen doğrulama linkine tıklayın.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);

                        } else {
                            JOptionPane.showMessageDialog(RegisterPage.this, "Kayıt başarısız. Lütfen tekrar deneyin.", "Hata", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(RegisterPage.this, "Kayıt işlemi sırasında bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        btnCancel.addActionListener(e -> {
            dispose();
           new LoginPage(parent);
        });

        setVisible(true);
    }

    private boolean registerUser(String username, String password, String fullName, String email) {
        boolean isRegistered = false;
        try {
            // Eğer email, @gmail.com ile bitmiyorsa, kayıt işlemini durdur.
            if (!email.endsWith("@gmail.com")) {
                System.out.println("Gmail dışındaki bir e-posta adresi kullanıldı: " + email);
                return false;
            }

            HttpClient client = HttpClient.newHttpClient();
            Map<String, String> userData = new HashMap<>();
            userData.put("username", username);
            userData.put("password", password);
            userData.put("fullName", fullName);
            userData.put("email", email);
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(userData);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/users/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                isRegistered = true;
                System.out.println(""+ response.body());
            } else {
                System.out.println("Kayıt başarısız: " + response.body());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return isRegistered;
    }

}
