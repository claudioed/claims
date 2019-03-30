package tech.claudioed.claims.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author claudioed on 2019-03-30.
 * Project claims
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimId {

  private String claimId;

}
