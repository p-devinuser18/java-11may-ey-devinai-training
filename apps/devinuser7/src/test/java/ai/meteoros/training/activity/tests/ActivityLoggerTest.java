package ai.meteoros.training.activity.tests;

import ai.meteoros.training.activity.services.ActivityLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActivityLoggerTest {

    private ActivityLogger activityLogger;
    private static final Path LOG_DIR = Paths.get("src/data");
    private static final Path LOG_FILE = LOG_DIR.resolve("activity.log");

    @BeforeEach
    void setUp() throws Exception {
        activityLogger = new ActivityLogger();
        if (Files.exists(LOG_FILE)) {
            Files.delete(LOG_FILE);
        }
    }

    @AfterEach
    void tearDown() throws Exception {
        if (Files.exists(LOG_FILE)) {
            Files.delete(LOG_FILE);
        }
    }

    // --- init() tests ---

    @Test
    void initShouldCreateLogFileWithSampleEntries() throws Exception {
        activityLogger.init();

        assertThat(Files.exists(LOG_FILE)).isTrue();
        List<String> lines = Files.readAllLines(LOG_FILE);
        assertThat(lines).hasSize(10);
    }

    @Test
    void initShouldNotOverwriteExistingLogFile() throws Exception {
        Files.createDirectories(LOG_DIR);
        Files.writeString(LOG_FILE, "{\"userId\":\"U999\",\"action\":\"TEST\",\"timestamp\":\"2026-01-01T00:00:00\"}\n");

        activityLogger.init();

        List<String> lines = Files.readAllLines(LOG_FILE);
        assertThat(lines).hasSize(1);
        assertThat(lines.get(0)).contains("U999");
    }

    // --- logAction() tests ---

    @Test
    void logActionShouldAppendEntryToFile() throws Exception {
        activityLogger.init();
        long linesBefore = Files.readAllLines(LOG_FILE).size();

        activityLogger.logAction("U500", "LOGIN", LocalDateTime.of(2026, 5, 17, 10, 0));

        List<String> linesAfter = Files.readAllLines(LOG_FILE);
        assertThat(linesAfter).hasSize((int) linesBefore + 1);
        assertThat(linesAfter.get(linesAfter.size() - 1)).contains("U500");
        assertThat(linesAfter.get(linesAfter.size() - 1)).contains("LOGIN");
    }

    @Test
    void logActionWithNullUserIdShouldThrowException() throws Exception {
        activityLogger.init();

        assertThatThrownBy(() -> activityLogger.logAction(null, "LOGIN", LocalDateTime.now()))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void logActionWithNullActionShouldThrowException() throws Exception {
        activityLogger.init();

        assertThatThrownBy(() -> activityLogger.logAction("U100", null, LocalDateTime.now()))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void logActionWithNullTimestampShouldThrowException() throws Exception {
        activityLogger.init();

        assertThatThrownBy(() -> activityLogger.logAction("U100", "LOGIN", null))
                .isInstanceOf(Exception.class);
    }

    @Test
    void logActionWithEmptyUserIdShouldWriteEntry() throws Exception {
        activityLogger.init();

        activityLogger.logAction("", "LOGIN", LocalDateTime.now());

        List<Map<String, String>> logs = activityLogger.readLogs("", null);
        assertThat(logs).isNotEmpty();
        assertThat(logs.get(0).get("userId")).isEmpty();
    }

    @Test
    void logActionWithEmptyActionShouldWriteEntry() throws Exception {
        activityLogger.init();

        activityLogger.logAction("U100", "", LocalDateTime.now());

        List<Map<String, String>> logs = activityLogger.readLogs("U100", "");
        assertThat(logs).isNotEmpty();
    }

    // --- readLogs() tests ---

    @Test
    void readLogsWithBothNullFiltersShouldReturnAllLogs() throws Exception {
        activityLogger.init();

        List<Map<String, String>> logs = activityLogger.readLogs(null, null);
        assertThat(logs).hasSize(10);
    }

    @Test
    void readLogsWithNullUserIdShouldFilterByActionOnly() throws Exception {
        activityLogger.init();

        List<Map<String, String>> logs = activityLogger.readLogs(null, "LOGIN");
        assertThat(logs).isNotEmpty();
        assertThat(logs).allSatisfy(entry ->
                assertThat(entry.get("action")).isEqualToIgnoringCase("LOGIN"));
    }

    @Test
    void readLogsWithNullActionShouldFilterByUserIdOnly() throws Exception {
        activityLogger.init();

        List<Map<String, String>> logs = activityLogger.readLogs("U101", null);
        assertThat(logs).isNotEmpty();
        assertThat(logs).allSatisfy(entry ->
                assertThat(entry.get("userId")).isEqualTo("U101"));
    }

    @Test
    void readLogsShouldReturnEmptyListWhenFileDoesNotExist() throws Exception {
        List<Map<String, String>> logs = activityLogger.readLogs(null, null);
        assertThat(logs).isEmpty();
    }

    @Test
    void readLogsShouldReturnEmptyListForNonExistentUserId() throws Exception {
        activityLogger.init();

        List<Map<String, String>> logs = activityLogger.readLogs("NONEXISTENT", null);
        assertThat(logs).isEmpty();
    }

    @Test
    void readLogsShouldReturnEmptyListForNonExistentAction() throws Exception {
        activityLogger.init();

        List<Map<String, String>> logs = activityLogger.readLogs(null, "NONEXISTENT");
        assertThat(logs).isEmpty();
    }

    @Test
    void readLogsActionFilterShouldBeCaseInsensitive() throws Exception {
        activityLogger.init();

        List<Map<String, String>> logsLower = activityLogger.readLogs(null, "login");
        List<Map<String, String>> logsUpper = activityLogger.readLogs(null, "LOGIN");
        assertThat(logsLower).hasSameSizeAs(logsUpper);
    }

    @Test
    void readLogsShouldHandleEmptyFile() throws Exception {
        Files.createDirectories(LOG_DIR);
        Files.createFile(LOG_FILE);

        List<Map<String, String>> logs = activityLogger.readLogs(null, null);
        assertThat(logs).isEmpty();
    }

    @Test
    void readLogsShouldHandleFileWithBlankLines() throws Exception {
        Files.createDirectories(LOG_DIR);
        Files.writeString(LOG_FILE,
                "{\"userId\":\"U101\",\"action\":\"LOGIN\",\"timestamp\":\"2026-05-17T08:00:00\"}\n\n\n");

        List<Map<String, String>> logs = activityLogger.readLogs(null, null);
        assertThat(logs).hasSize(1);
    }

    @Test
    void readLogsShouldFilterByBothUserIdAndAction() throws Exception {
        activityLogger.init();

        List<Map<String, String>> logs = activityLogger.readLogs("U101", "LOGIN");
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).get("userId")).isEqualTo("U101");
        assertThat(logs.get(0).get("action")).isEqualTo("LOGIN");
    }

    @Test
    void readLogsShouldReturnEmptyWhenUserIdMatchesButActionDoesNot() throws Exception {
        activityLogger.init();

        List<Map<String, String>> logs = activityLogger.readLogs("U104", "CREATE");
        assertThat(logs).isEmpty();
    }

    // --- Multiple writes ---

    @Test
    void multipleLogActionCallsShouldAllBeReadBack() throws Exception {
        activityLogger.init();

        activityLogger.logAction("U600", "LOGIN", LocalDateTime.of(2026, 1, 1, 0, 0));
        activityLogger.logAction("U600", "CREATE", LocalDateTime.of(2026, 1, 1, 0, 1));
        activityLogger.logAction("U600", "LOGOUT", LocalDateTime.of(2026, 1, 1, 0, 2));

        List<Map<String, String>> logs = activityLogger.readLogs("U600", null);
        assertThat(logs).hasSize(3);
    }

    @Test
    void logActionWithSpecialCharactersInUserIdShouldWork() throws Exception {
        activityLogger.init();

        activityLogger.logAction("user@domain.com", "LOGIN", LocalDateTime.now());

        List<Map<String, String>> logs = activityLogger.readLogs("user@domain.com", null);
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).get("userId")).isEqualTo("user@domain.com");
    }

    @Test
    void logActionWithWhitespaceUserIdShouldWork() throws Exception {
        activityLogger.init();

        activityLogger.logAction("  ", "LOGIN", LocalDateTime.now());

        List<Map<String, String>> logs = activityLogger.readLogs("  ", null);
        assertThat(logs).hasSize(1);
    }
}
