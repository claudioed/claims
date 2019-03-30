package tech.claudioed.claims;

import java.util.Map;
import lombok.Data;

/**
 * @author claudioed on 2019-03-05.
 * Project claims
 */
@Data
public class ClaimRequest {

  private String type;

  private String orderId;

  private Map<String, Object> data;

}
