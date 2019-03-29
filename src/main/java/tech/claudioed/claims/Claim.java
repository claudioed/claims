package tech.claudioed.claims;

import io.vertx.core.json.JsonObject;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @author claudioed on 2019-03-28. Project claims */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Claim {

  private String id;

  private LocalDateTime registeredAt;

  private Map<String, Object> data;

  public JsonObject json() {
    final JsonObject json = new JsonObject();
    return json.put("_id", this.id)
        .put("registeredAt", this.registeredAt.toString())
        .put("data", this.data);
  }

  public static Claim from(ClaimRequest request) {
    return Claim.builder()
        .id(UUID.randomUUID().toString())
        .registeredAt(LocalDateTime.now())
        .data(request.getData())
        .build();
  }
}
