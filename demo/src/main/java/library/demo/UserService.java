package library.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    public User authenticate(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, password);

        // Kullanıcının doğrulanmış olup olmadığını kontrol et
        if (user != null && user.isVerified()) {
            return user;
        }

        return null;
    }

    public void updateLoginTime(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
    }

    public void updateLogoutTime(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        user.setLastLogoutTime(LocalDateTime.now());
        userRepository.save(user);
    }

    public User registerUser(User user) {
        // Kullanıcı adının zaten kayıtlı olup olmadığını kontrol et
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Bu kullanıcı adı zaten kayıtlı.");
        }

        // Token ve doğrulama durumu ayarla
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerified(false);

        User newUser = userRepository.save(user); // Yeni kullanıcıyı kaydet

        // E-posta gönder
        sendVerificationEmail(user.getEmail(), verificationToken);

        return newUser;
    }

    public User verifyUser(String token) {
        User user = userRepository.findByVerificationToken(token);
        if (user != null && !user.isVerified()) {
            user.setVerified(true);
            user.setVerificationToken(null); // Token'ı null yaparak kullanılmış olduğunu işaretle
            return userRepository.save(user);
        }
        return null;
    }

    private void sendVerificationEmail(String to, String token) {
        String subject = "Hesap Doğrulama";
        String confirmationUrl = "http://localhost:8080/api/users/verify/" + token;
        String text = "Lütfen hesabınızı doğrulamak için aşağıdaki linke tıklayın:\n" + confirmationUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);

    }
}
