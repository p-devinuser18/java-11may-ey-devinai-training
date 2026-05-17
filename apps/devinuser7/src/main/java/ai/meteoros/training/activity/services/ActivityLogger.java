package ai.meteoros.training.activity.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ActivityLogger {

    private static final Path LOG_DIR = Paths.get("src/data");
    private static final Path LOG_FILE = LOG_DIR.resolve("activity.log");
    private final ObjectMapper objectMapper;

    public ActivityLogger() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @PostConstruct
    public void init() throws IOException {
        if (!Files.exists(LOG_FILE)) {
            Files.createDirectories(LOG_DIR);
            Files.createFile(LOG_FILE);
            writeSampleEntries();
        }
    }

    public void logAction(String userId, String action, LocalDateTime timestamp) {
        try {
            Map<String, String> entry = Map.of(
                    "userId", userId,
                    "action", action,
                    "timestamp", timestamp.toString()
            );
            String json = objectMapper.writeValueAsString(entry);
            Files.writeString(LOG_FILE, json + System.lineSeparator(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write activity log", e);
        }
    }

    public List<Map<String, String>> readLogs(String userId, String action) {
        try {
            if (!Files.exists(LOG_FILE)) {
                return List.of();
            }
            List<String> lines = Files.readAllLines(LOG_FILE);
            List<Map<String, String>> logs = new ArrayList<>();
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                Map<String, String> entry = objectMapper.readValue(line, new TypeReference<>() {});
                logs.add(entry);
            }

            return logs.stream()
                    .filter(entry -> userId == null || userId.equals(entry.get("userId")))
                    .filter(entry -> action == null || action.equalsIgnoreCase(entry.get("action")))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read activity log", e);
        }
    }

    private void writeSampleEntries() throws IOException {
        List<Map<String, String>> samples = List.of(
                Map.of("userId", "U101", "action", "LOGIN", "timestamp", "2026-05-17T08:00:00"),
                Map.of("userId", "U102", "action", "LOGIN", "timestamp", "2026-05-17T08:05:00"),
                Map.of("userId", "U101", "action", "CREATE", "timestamp", "2026-05-17T08:10:00"),
                Map.of("userId", "U103", "action", "LOGIN", "timestamp", "2026-05-17T08:15:00"),
                Map.of("userId", "U101", "action", "UPDATE", "timestamp", "2026-05-17T08:20:00"),
                Map.of("userId", "U102", "action", "DELETE", "timestamp", "2026-05-17T08:25:00"),
                Map.of("userId", "U103", "action", "LOGOUT", "timestamp", "2026-05-17T08:30:00"),
                Map.of("userId", "U101", "action", "LOGOUT", "timestamp", "2026-05-17T08:35:00"),
                Map.of("userId", "U104", "action", "LOGIN", "timestamp", "2026-05-17T08:40:00"),
                Map.of("userId", "U104", "action", "UPDATE", "timestamp", "2026-05-17T08:45:00")
        );

        StringBuilder sb = new StringBuilder();
        for (Map<String, String> sample : samples) {
            sb.append(objectMapper.writeValueAsString(sample)).append(System.lineSeparator());
        }
        Files.writeString(LOG_FILE, sb.toString(), StandardOpenOption.APPEND);
    }
}
