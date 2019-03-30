package tech.claudioed.claims;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import lombok.SneakyThrows;

/**
 * @author claudioed on 2019-03-28.
 * Project claims
 */
public class CreateDatabaseClaimsVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateDatabaseClaimsVerticle.class);

  @Override
  @SneakyThrows
  public void start() {
    final String mongoHost = System.getenv("MONGO_HOST");

    LOGGER.info(" MONGO HOST " + mongoHost);

    final MongoClient mongoClient = MongoClient.createShared(this.vertx, new JsonObject()
      .put("connection_string", mongoHost)
      .put("db_name", "CLAIMS"));

    mongoClient.createCollection("claims",handler ->{
      if(handler.succeeded()){
        LOGGER.info("Collection claims created successfully");
      }else{
        LOGGER.error(handler.cause().getMessage());
      }
    });

  }

}
