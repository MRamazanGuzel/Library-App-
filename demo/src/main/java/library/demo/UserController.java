package library.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        User user = userService.authenticate(username, password);
        if (user != null) {
            userService.updateLoginTime(user.getId()); // Giriş zamanını güncelle

            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getId());
            response.put("fullName", user.getFullName());
            response.put("lastLoginTime", user.getLastLoginTime());
            response.put("lastLogoutTime", user.getLastLogoutTime());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Geçersiz kullanıcı adı, şifre veya e-posta doğrulanmamış.");
        }
    }

    @PostMapping("/logout/{userId}")
    public ResponseEntity<?> logout(@PathVariable Long userId) {
        userService.updateLogoutTime(userId); // Çıkış zamanını güncelle
        return ResponseEntity.ok("Çıkış zamanı güncellendi");
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User newUser = userService.registerUser(user);
            return ResponseEntity.ok("Kullanıcı başarıyla kaydedildi. ID: " + newUser.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<?> verifyUser(@PathVariable String token) {
        User user = userService.verifyUser(token);
        if (user != null) {
            return ResponseEntity.ok("Kullanıcı başarıyla doğrulandı.");
        } else {
            return ResponseEntity.status(400).body("Geçersiz veya süresi dolmuş doğrulama token'ı.");
        }
    }
}

