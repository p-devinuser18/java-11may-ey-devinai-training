package ai.meteoros.training.activity.controllers;

import ai.meteoros.training.activity.services.ActivityLogger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ActivityController {

    private final ActivityLogger activityLogger;

    public ActivityController(ActivityLogger activityLogger) {
        this.activityLogger = activityLogger;
    }

    @GetMapping("/activity")
    public ResponseEntity<?> getActivityLogs(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String action) {

        List<Map<String, String>> logs = activityLogger.readLogs(userId, action);

        if (logs.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "No logs found"));
        }

        return ResponseEntity.ok(logs);
    }
}
