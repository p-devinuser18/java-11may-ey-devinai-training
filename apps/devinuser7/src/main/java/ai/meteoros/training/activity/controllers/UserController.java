package ai.meteoros.training.activity.controllers;

import ai.meteoros.training.activity.services.ActivityLogger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final ActivityLogger activityLogger;

    public UserController(ActivityLogger activityLogger) {
        this.activityLogger = activityLogger;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        activityLogger.logAction(userId, "LOGIN", LocalDateTime.now());
        return ResponseEntity.ok(Map.of("message", "User " + userId + " logged in"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        activityLogger.logAction(userId, "LOGOUT", LocalDateTime.now());
        return ResponseEntity.ok(Map.of("message", "User " + userId + " logged out"));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createUser(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        activityLogger.logAction(userId, "CREATE", LocalDateTime.now());
        return ResponseEntity.ok(Map.of("message", "User " + userId + " created"));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable String userId, @RequestBody Map<String, String> body) {
        activityLogger.logAction(userId, "UPDATE", LocalDateTime.now());
        return ResponseEntity.ok(Map.of("message", "User " + userId + " updated"));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String userId) {
        activityLogger.logAction(userId, "DELETE", LocalDateTime.now());
        return ResponseEntity.ok(Map.of("message", "User " + userId + " deleted"));
    }
}
