package ai.meteoros.training.expenses.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MetricsService {

    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final ConcurrentHashMap<Integer, AtomicInteger> byStatusCode = new ConcurrentHashMap<>();

    public void recordRequest(int statusCode) {
        totalRequests.incrementAndGet();
        if (statusCode >= 200 && statusCode < 400) {
            successCount.incrementAndGet();
        } else {
            failureCount.incrementAndGet();
        }
        byStatusCode.computeIfAbsent(statusCode, k -> new AtomicInteger(0)).incrementAndGet();
    }

    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalRequests", totalRequests.get());
        metrics.put("successCount", successCount.get());
        metrics.put("failureCount", failureCount.get());
        Map<String, Integer> statusCodes = new HashMap<>();
        byStatusCode.forEach((code, count) -> statusCodes.put(String.valueOf(code), count.get()));
        metrics.put("byStatusCode", statusCodes);
        return metrics;
    }

    public void reset() {
        totalRequests.set(0);
        successCount.set(0);
        failureCount.set(0);
        byStatusCode.clear();
    }
}
