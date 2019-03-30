package tech.claudioed.claims;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import tech.claudioed.claims.data.OrderId;

/**
 * @author claudioed on 2019-03-28.
 * Project claims
 */
public class FindClaimByOrderIdVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(FindClaimByOrderIdVerticle.class);

  @Override
  @SneakyThrows
  public void start() {
    final String mongoHost = System.getenv("MONGO_HOST");
    LOGGER.info(" MONGO HOST " + mongoHost);
    JsonObject mongoConfig = new JsonObject()
      .put("connection_string", mongoHost)
      .put("db_name", "CLAIMS");
    final MongoClient mongoClient = MongoClient.createShared(this.vertx, mongoConfig);
    this.vertx.eventBus().consumer("claim.find.order.id", handler ->{
      LOGGER.info(" Receiving request to find claim by Order ID ");
      final OrderId orderId = Json.decodeValue(handler.body().toString(), OrderId.class);
      final JsonObject query = new JsonObject().put("orderId", orderId.getOrderId());
      mongoClient.find("claims", query, res -> {
        if (res.succeeded()) {
          LOGGER.info(" Query executed successfully");
          final List<Claim> claims = res.result().stream().map(json ->
            Json.decodeValue(json.toBuffer(), Claim.class)).collect(Collectors.toList());
          LOGGER.info(" {} claims found ",claims.size());
          handler.reply(Json.encode(claims));
        } else {
          handler.fail(204,"Any claims found");
        }
      });
    });

  }

}
