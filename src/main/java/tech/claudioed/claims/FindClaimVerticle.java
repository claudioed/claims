package tech.claudioed.claims;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import lombok.SneakyThrows;
import tech.claudioed.claims.data.ClaimId;
import tech.claudioed.claims.data.OrderId;

/**
 * @author claudioed on 2019-03-28.
 * Project claims
 */
public class FindClaimVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(FindClaimVerticle.class);

  @Override
  @SneakyThrows
  public void start() {
    final String mongoHost = System.getenv("MONGO_HOST");
    LOGGER.info(" MONGO HOST " + mongoHost);
    JsonObject mongoConfig = new JsonObject()
      .put("connection_string", mongoHost)
      .put("db_name", "CLAIMS");
    final MongoClient mongoClient = MongoClient.createShared(this.vertx, mongoConfig);
    this.vertx.eventBus().consumer("claim.find.id", handler ->{
      LOGGER.info(" Receiving request to find claim by ID ");
      final ClaimId claimId = Json.decodeValue(handler.body().toString(), ClaimId.class);
      final JsonObject query = new JsonObject().put("_id", claimId.getClaimId());
      mongoClient.findOne("claims",query,null,res ->{
        if(res.succeeded()){
          LOGGER.info("Claim id {} in DB ",claimId.getClaimId());
          final Claim claim = Json.decodeValue(res.result().toBuffer(), Claim.class);
          handler.reply(Json.encode(claim));
        }else{
          LOGGER.info("Claim id {} not found ",claimId.getClaimId());
          handler.fail(404,"Claim not found");
        }
      });
    });

  }

}
