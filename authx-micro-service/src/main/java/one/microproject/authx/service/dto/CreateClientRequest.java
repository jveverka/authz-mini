package one.microproject.authx.service.dto;

import java.util.Map;

public record CreateClientRequest(String id, String description, Boolean authEnabled, String secret, Map<String, String> labels) {
}
