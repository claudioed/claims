package tech.claudioed.claims;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import io.nats.client.Options;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import lombok.SneakyThrows;

/**
 * @author claudioed on 2019-03-28.
 * Project claims
 */
public class RegisterClaimVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(RegisterClaimVerticle.class);

  @Override
  @SneakyThrows
  public void start() {
    final String natsHost = System.getenv("NATS_HOST");
    final String natsUser = System.getenv("NATS_USER");
    final String natsPass = System.getenv("NATS_PASS");
    final String mongoHost = System.getenv("MONGO_HOST");

    LOGGER.info(" MONGO HOST " + mongoHost);
    LOGGER.info(" NATS HOST " + natsHost);

    JsonObject mongoConfig = new JsonObject()
      .put("connection_string", mongoHost)
      .put("db_name", "CLAIMS");

    final MongoClient mongoClient = MongoClient.createShared(this.vertx, mongoConfig);

    Connection natsConnection = Nats.connect(new Options.Builder()
      .connectionTimeout(Duration.ofSeconds(2))
      .pingInterval(Duration.ofSeconds(10))
      .reconnectWait(Duration.ofSeconds(1))
      .userInfo(natsUser,natsPass)
      .maxReconnects(-1)
      .reconnectBufferSize(-1)
      .server(natsHost)
      .connectionName(System.getenv("HOSTNAME"))
      .connectionListener((conn, type) -> LOGGER.info("Status change " + type))
    .build());

    final Dispatcher dispatcher = natsConnection.createDispatcher((message) -> {
      final String response = new String(message.getData(), StandardCharsets.UTF_8);
      LOGGER.info(" Receiving message " + response );
      final ClaimRequest claimRequest = Json.mapper.convertValue(response, ClaimRequest.class);
      LOGGER.info(" Decoding executed successfully " + claimRequest.toString());
      final Claim claim = Claim.from(claimRequest);
      mongoClient.insert("claims", claim.json(), res -> {
        if (res.succeeded()) {
          LOGGER.info("claims registered successfully !!!");
        } else {
          LOGGER.error("Error to insert in database !!");
        }
      });
    });
    dispatcher.subscribe("request-claims");
  }

}
