package library.demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class MainPage extends JFrame {
    private JPanel mainPanel;
    private JLabel welcomeLabel;
    private JLabel lastLoginLabel;
    private JLabel lastLogoutLabel;
    private JButton addBookButton;
    private JButton myBooksButton;
    private JButton logoutButton;
    private Long userId;

    private static final Pattern ISBN_PATTERN = Pattern.compile("^(?:\\d{10}|\\d{13})$"); // ISBN-10 veya ISBN-13
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String DATE_FORMAT_STRING = "yyyy-MM-dd";

    public MainPage(Long userId, String name_last, String lastLoginTime, String lastLogoutTime) {
        this.userId = userId;

        setTitle("Ana Sayfa");
        setSize(700, 500); // Genişlik ve yükseklik artırıldı
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Ana panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Hoş geldin etiketi
        welcomeLabel = new JLabel("Hoş geldin, " + name_last);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.BLUE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(welcomeLabel, gbc);

        // Bilgi paneli
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setOpaque(false);
        lastLoginLabel = new JLabel("Son Giriş: " + lastLoginTime);
        lastLogoutLabel = new JLabel("Son Çıkış: " + lastLogoutTime);
        infoPanel.add(lastLoginLabel);
        infoPanel.add(lastLogoutLabel);
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        mainPanel.add(infoPanel, gbc);

        // Düğmeler paneli
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        buttonPanel.setOpaque(false);

        // İkonları yükleme
        ImageIcon addIcon = new ImageIcon(new ImageIcon("C:/mylibrary/src/New book.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        ImageIcon booksIcon = new ImageIcon(new ImageIcon("C:/mylibrary/src/issue.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        ImageIcon logoutIcon = new ImageIcon(new ImageIcon("C:/mylibrary/src/exit.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

        addBookButton = new JButton("Kitap Ekle", addIcon);
        myBooksButton = new JButton("Kitaplarım", booksIcon);
        logoutButton = new JButton("Çıkış", logoutIcon);

        // Düğme stilleri
        styleButton(addBookButton);
        styleButton(myBooksButton);
        styleButton(logoutButton);

        buttonPanel.add(addBookButton);
        buttonPanel.add(myBooksButton);
        buttonPanel.add(logoutButton);

        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);




        addBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String bookname = getValidBook("Kitap Adı:");
                if (bookname == null) return;

                String author = getValidAUTHOR("Yazar:");
                if (author == null) return;

                String date = getValidDate("Yayın Tarihi (yyyy-MM-dd):");
                if (date == null) return;

                String bookType = getValidType("Kitap Türü:");
                if (bookType == null) return;

                String pageCountStr = getValidInteger("Sayfa Sayısı:");
                if (pageCountStr == null) return;
                int pageCount = Integer.parseInt(pageCountStr);

                String isbn = getValidISBN("ISBN:");
                if (isbn == null) return;

                // Kitap ekleme
                try {
                    String url = "http://localhost:8080/api/books";
                    URL apiUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; utf-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);

                    String jsonInputString = String.format(
                            "{\"isbn\": \"%s\", \"bookname\": \"%s\", \"author\": \"%s\", \"date\": \"%s\", \"book_type\": \"%s\", \"page_count\": %d, \"user_id\": %d}",
                            isbn, bookname, author, date, bookType, pageCount, userId
                    );

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_CREATED) {
                        System.out.println("Kitap ekledi " + "ID= " + userId);

                        JOptionPane.showMessageDialog(MainPage.this, "Kitap başarıyla eklendi.");
                    } else {
                        try (BufferedReader br = new BufferedReader(
                                new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                            StringBuilder response = new StringBuilder();
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }
                            JOptionPane.showMessageDialog(MainPage.this, "Hata: " + response.toString(), "Hata", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MainPage.this, "REST API işlemleri sırasında bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        myBooksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Kitaplarımı görüntüleme işlemi burada yapılacak
                new MyBooksPage(userId, name_last, lastLoginTime, lastLogoutTime).setVisible(true);
                MainPage.this.setVisible(false);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(MainPage.this, "Çıkmak istediğinize emin misiniz?", "Çıkış", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    new SwingWorker<Boolean, Void>() {
                        @Override
                        protected Boolean doInBackground() {
                            return logout(userId);
                        }

                        @Override
                        protected void done() {
                            MainPage.this.dispose();
                        }
                    }.execute();
                }
            }
        });
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(135, 206, 250)); // Açık mavi arka plan
        button.setForeground(Color.WHITE); // Beyaz yazı rengi
        button.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1)); // Mavi kenarlık
        button.setFocusPainted(false); // Focus efekti kaldırıldı
    }

    private String getValidInput(String message, String errorMessage) {
        while (true) {
            String input = JOptionPane.showInputDialog(MainPage.this, message);
            if (input == null) { // İptal butonuna basıldıysa
                return null;
            }
            if (!input.trim().isEmpty()) {
                return input.trim();
            }
            JOptionPane.showMessageDialog(MainPage.this, errorMessage, "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String getValidDate(String message) {
        while (true) {
            String date = getValidInput(message, "Yayın tarihi boş bırakılamaz.");
            if (date == null) return null;
            if (isValidDate(date)) {
                return date;
            }
            JOptionPane.showMessageDialog(MainPage.this, "Tarih formatı geçersiz. Lütfen \"yyyy-MM-dd\" formatını kullanın.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean isValidDate(String date) {
        try {
            DATE_FORMAT.setLenient(false);
            DATE_FORMAT.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private String getValidInteger(String message) {
        while (true) {
            String input = getValidInput(message, "Sayfa sayısı boş bırakılamaz.");
            if (input == null) return null;
            try {
                Integer.parseInt(input);
                return input;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(MainPage.this, "Lütfen sayı giriniz.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private String getValidISBN(String message) {
        while (true) {
            String isbn = getValidInput(message, "ISBN boş bırakılamaz.");
            if (isbn == null) return null;
            if (ISBN_PATTERN.matcher(isbn).matches()) {
                return isbn;
            }
            JOptionPane.showMessageDialog(MainPage.this, "Geçersiz ISBN formatı. Lütfen geçerli bir ISBN girin(ISBN-10 veya ISBN-13).", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String getValidAUTHOR(String message) {
        while (true) {
            String author = getValidInput(message, "Yazar adı boş bırakılamaz.");
            if (author == null) return null;
            if (!author.trim().isEmpty() && !isInteger(author)) {
                return author;
            }
            JOptionPane.showMessageDialog(MainPage.this, "Lütfen geçerli bir yazar adı girin. (Sadece harfler içermelidir.)", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String getValidBook(String message) {
        while (true) {
            String bookname = getValidInput(message, "Kitap adı boş bırakılamaz.");
            if (bookname == null) return null;

            // Kitap adı boş olmamalı ve sadece harfler içermeli
            if (!bookname.trim().isEmpty() && !isInteger(bookname)) {
                return bookname;
            }

            JOptionPane.showMessageDialog(MainPage.this, "Lütfen geçerli bir kitap adı girin. (Sadece harfler içermelidir.)", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String getValidType(String message) {
        while (true) {
            String bookType = getValidInput(message, "Kitap türü boş bırakılamaz.");
            if (bookType == null) return null;


            if (!bookType.trim().isEmpty() && !isInteger(bookType)) {
                return bookType;
            }

            JOptionPane.showMessageDialog(MainPage.this, "Lütfen geçerli bir kitap türü girin. (Sadece harfler içermelidir.)", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }



    private boolean logout(Long userId) {
        try {
            String url = "http://localhost:8080/api/users/logout/" + userId;
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            System.out.println("Çıkış kayıt edildi " +  "ID= " + userId);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return true;

            } else {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());

                    }
                    JOptionPane.showMessageDialog(MainPage.this, "Hata: " + response.toString(), "Hata", JOptionPane.ERROR_MESSAGE);
                }
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(MainPage.this, "REST API işlemleri sırasında bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
