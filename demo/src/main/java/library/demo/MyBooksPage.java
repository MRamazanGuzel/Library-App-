package library.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class MyBooksPage extends JFrame {
    private JTable booksTable;
    private Long userId;
    private JButton deleteButton;
    private JButton returnButton;
    private JButton saveButton; // Yeni eklenen "Kaydet" butonu
    private JTextField searchBookName;
    private JTextField searchAuthor;
    private JTextField searchDate;
    private JTextField searchBookType;
    private JTextField searchPageCount;
    private JTextField searchISBN;

    public MyBooksPage(Long userId, String name_last, String lastLoginTime, String lastLogoutTime) { // Kullanıcı ID'si parametre olarak alır
        this.userId = userId;
        setTitle("Kitaplarım");
        setSize(1200, 768);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);


        booksTable = new JTable();
        loadBooks("", "", "", "", "", ""); // Başlangıçta boş kriterlerle kitapları yükle

        JScrollPane scrollPane = new JScrollPane(booksTable);
        add(scrollPane, BorderLayout.CENTER);


        deleteButton = new JButton("Sil");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedBook();
            }
        });


        returnButton = new JButton("Geri");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToMainPage(name_last, lastLoginTime, lastLogoutTime);
            }
        });

        saveButton = new JButton("Güncelle");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSelectedBook(); // Seçilen kitabı güncelle
            }
        });



        searchBookName = new JTextField(10);
        searchAuthor = new JTextField(10);
        searchDate = new JTextField(10);
        searchBookType = new JTextField(10);
        searchPageCount = new JTextField(10);
        searchISBN = new JTextField(10);


        DocumentListener searchListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchBooks();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchBooks();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchBooks();
            }
        };

        searchBookName.getDocument().addDocumentListener(searchListener);
        searchAuthor.getDocument().addDocumentListener(searchListener);
        searchDate.getDocument().addDocumentListener(searchListener);
        searchBookType.getDocument().addDocumentListener(searchListener);
        searchPageCount.getDocument().addDocumentListener(searchListener);
        searchISBN.getDocument().addDocumentListener(searchListener);

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Kitap Adı:"));
        searchPanel.add(searchBookName);
        searchPanel.add(new JLabel("Yazar:"));
        searchPanel.add(searchAuthor);
        searchPanel.add(new JLabel("Yayın Tarihi:"));
        searchPanel.add(searchDate);
        searchPanel.add(new JLabel("Kitap Türü:"));
        searchPanel.add(searchBookType);
        searchPanel.add(new JLabel("Sayfa Sayısı:"));
        searchPanel.add(searchPageCount);
        searchPanel.add(new JLabel("ISBN:"));
        searchPanel.add(searchISBN);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(saveButton); // "Kaydet" butonunu ekle
        add(searchPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void searchBooks() {
        String bookName = searchBookName.getText().trim();
        String author = searchAuthor.getText().trim();
        String date = searchDate.getText().trim();
        String bookType = searchBookType.getText().trim();
        String pageCount = searchPageCount.getText().trim();
        String isbn = searchISBN.getText().trim();
        loadBooks(bookName, author, date, bookType, pageCount, isbn);
    }

    private void loadBooks(String bookName, String author, String date, String bookType, String pageCount, String isbn) {
        String url = "http://localhost:8080/api/books/search";
        String params = String.format("?userId=%d&bookname=%s&author=%s&date=%s&bookType=%s&pageCount=%s&isbn=%s",
                userId, bookName, author, date, bookType, pageCount, isbn);

        try {
            // HTTP GET isteği gönderme
            String response = sendHttpGetRequest(url + params);

            // JSON verisini ayrıştırma
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> books = objectMapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});

            // Sonuçları tablo modeline ekleme
            booksTable.setModel(buildTableModel(books));

            // Kitap ID sütununu gizle
            booksTable.getColumnModel().getColumn(0).setMinWidth(0);
            booksTable.getColumnModel().getColumn(0).setMaxWidth(0);
            booksTable.getColumnModel().getColumn(0).setWidth(0);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "JSON verisi işlenirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }


    private String sendHttpGetRequest(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            return response.toString();
        } else {
            throw new IOException("HTTP GET isteği başarısız oldu, kod: " + responseCode);
        }
    }

    private void deleteSelectedBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Kitap ID'sini al
            String bookIdStr = (String) booksTable.getValueAt(selectedRow, 0);
            try {
                // Kitap ID'sini Long türüne dönüştür
                Long bookId = Long.parseLong(bookIdStr);


                String url = "http://localhost:8080/api/books/" + bookId;
                sendHttpDeleteRequest(url);
                loadBooks("", "", "", "", "", ""); // Kitap listesini yenile
                System.out.println("Kitap Sildi " +"ID= "+ userId);
                JOptionPane.showMessageDialog(this, "Kitap başarıyla silindi.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Geçersiz kitap ID'si.", "Hata", JOptionPane.ERROR_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Kitap silinirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } else {

            JOptionPane.showMessageDialog(this, "Lütfen bir kitap seçiniz.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void updateSelectedBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                // Kullanıcı girdilerini al
                String bookIdStr = (String) booksTable.getValueAt(selectedRow, 0);
                String bookName = (String) booksTable.getValueAt(selectedRow, 1);
                String author = (String) booksTable.getValueAt(selectedRow, 2);
                String date = (String) booksTable.getValueAt(selectedRow, 3);
                String bookType = (String) booksTable.getValueAt(selectedRow, 4);
                String pageCountStr = (String) booksTable.getValueAt(selectedRow, 5);
                String isbn = (String) booksTable.getValueAt(selectedRow, 6);

                // Kitap ID'sini kontrol et
                Long bookId;
                try {
                    bookId = Long.parseLong(bookIdStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Geçersiz kitap ID'si.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Boş alanları kontrol et
                if (bookName == null || bookName.trim().isEmpty() ||
                        author == null || author.trim().isEmpty() ||
                        date == null || date.trim().isEmpty() ||
                        bookType == null || bookType.trim().isEmpty() ||
                        pageCountStr == null || pageCountStr.trim().isEmpty() ||
                        isbn == null || isbn.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int pageCount;
                try {
                    pageCount = Integer.parseInt(pageCountStr);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Geçersiz sayfa sayısı. Sayı olmalıdır.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!isbn.matches("\\d{10}") && !isbn.matches("\\d{13}")) {
                    JOptionPane.showMessageDialog(this, "Geçersiz ISBN numarası. ISBN 10 veya 13 haneli bir sayı olmalıdır.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    JOptionPane.showMessageDialog(this, "Geçersiz tarih formatı. Tarih YYYY-MM-DD formatında olmalıdır.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (isNumeric(bookName)) {
                    JOptionPane.showMessageDialog(this, "Kitap adı sayısal değerler içermemelidir.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (isNumeric(author)) {
                    JOptionPane.showMessageDialog(this, "Yazar adı sayısal değerler içermemelidir.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (isNumeric(bookType)) {
                    JOptionPane.showMessageDialog(this, "Kitap türü sayısal değerler içermemelidir.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Kitap güncellemesini yap
                updateBook(bookId, bookName, author, date, bookType, pageCountStr, isbn);
                loadBooks("", "", "", "", "", "");
                JOptionPane.showMessageDialog(this, "Kitap başarıyla güncellendi.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Kitap güncellenirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen güncellemek istediğiniz kitabı seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Sayısal bir değeri kontrol eden yardımcı metod
    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void updateBook(Long bookId, String bookName, String author, String date, String bookType, String pageCount, String isbn) throws IOException {
        String url = "http://localhost:8080/api/books/update/" + bookId;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");

        String jsonInputString = String.format("{\"bookname\":\"%s\", \"author\":\"%s\", \"date\":\"%s\", \"book_type\":\"%s\", \"page_count\":%s, \"isbn\":\"%s\"}",
                bookName, author, date, bookType, pageCount, isbn);

        con.setDoOutput(true);
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = con.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Kitap güncellenemedi. HTTP kodu: " + responseCode);
        }
    }

    private void sendHttpDeleteRequest(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("DELETE");

        int responseCode = con.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
            throw new IOException("HTTP DELETE isteği başarısız oldu, kod: " + responseCode);
        }
    }

    private static javax.swing.table.TableModel buildTableModel(List<Map<String, Object>> books) {
        // Sütun isimlerini belirle
        String[] columnNames = {"Kitap ID", "Kitap Adı", "Yazar", "Yayın Tarihi", "Kitap Türü", "Sayfa Sayısı", "ISBN"};

        // Verileri al
        Vector<String[]> data = new Vector<>();
        for (Map<String, Object> book : books) {
            String[] row = new String[columnNames.length];
            row[0] = book.get("book_id").toString();
            row[1] = (String) book.get("bookname");
            row[2] = (String) book.get("author");
            row[3] = (String) book.get("date");
            row[4] = (String) book.get("book_type");
            row[5] = book.get("page_count").toString();
            row[6] = (String) book.get("isbn"); // ISBN değerini al
            data.add(row);
        }

        return new javax.swing.table.DefaultTableModel(data.toArray(new String[][]{}), columnNames);
    }

    private void returnToMainPage(String name_last, String lastLoginTime, String lastLogoutTime) {

        // MainPage'e userId ve fullName iletilir
        MainPage mainPage = new MainPage(userId, name_last, lastLoginTime, lastLogoutTime);
        mainPage.setVisible(true);
        this.dispose();
    }

    public static void main(String[] args, String name_last, String lastLoginTime, String lastLogoutTime) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MyBooksPage(1L, name_last, lastLoginTime, lastLogoutTime).setVisible(true); // Test için userId 1 (Long olarak)
            }
        });
    }
}
