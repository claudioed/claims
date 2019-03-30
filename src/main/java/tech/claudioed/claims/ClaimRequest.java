package tech.claudioed.claims;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author claudioed on 2019-03-05.
 * Project claims
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClaimRequest {

  private String type;

  private String orderId;

  private Map<String, Object> data;

}
