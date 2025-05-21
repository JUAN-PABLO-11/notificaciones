package co.edu.uniquindio.poo.notification.event;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa un evento del sistema con datos adicionales.
 */
public class SystemEventData {
    private SystemEvent eventType;
    private LocalDateTime timestamp;
    private Map<String, String> data;
    
    public SystemEventData(SystemEvent eventType) {
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
        this.data = new HashMap<>();
    }
    
    public SystemEvent getEventType() {
        return eventType;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public Map<String, String> getData() {
        return data;
    }
    
    public void addData(String key, String value) {
        data.put(key, value);
    }
    
    public String getData(String key) {
        return data.get(key);
    }
    
    public boolean hasData(String key) {
        return data.containsKey(key);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(timestamp).append("] ");
        sb.append(eventType.getDescription());
        
        if (!data.isEmpty()) {
            sb.append(": ");
            boolean first = true;
            for (Map.Entry<String, String> entry : data.entrySet()) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
        }
        
        return sb.toString();
    }
}
