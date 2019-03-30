package tech.claudioed.claims;

import com.fasterxml.jackson.core.type.TypeReference;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.Json;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import java.util.List;
import tech.claudioed.claims.data.ClaimId;
import tech.claudioed.claims.data.OrderId;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() {
    final DeploymentOptions deploymentOptions = new DeploymentOptions();
    deploymentOptions.setWorker(true);
    this.vertx.deployVerticle(new RegisterClaimVerticle(),deploymentOptions);
    this.vertx.deployVerticle(new FindClaimByOrderIdVerticle(),deploymentOptions);
    this.vertx.deployVerticle(new FindClaimVerticle(),deploymentOptions);
    this.vertx.deployVerticle(new CreateDatabaseClaimsVerticle(),deploymentOptions);
    HealthCheckHandler healthChecks = HealthCheckHandler.create(vertx);
    Router router = Router.router(vertx);
    router.get("/health").handler(healthChecks);

    router.get("/api/claims/:id").handler(ctx ->{
      final String id = ctx.request().getParam("id");
      final ClaimId claimId = ClaimId.builder().claimId(id).build();

      this.vertx.eventBus().send("claim.find.id", Json.encode(claimId),handler ->{
        if(handler.succeeded()){
          final Claim claim = Json.decodeValue(handler.result().body().toString(), Claim.class);
          ctx.response().putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(claim));
        }else {
          ctx.response().setStatusCode(404).end();
        }
      });
    });
    router.get("/api/claims?order-id=:orderId").handler(ctx ->{
      final String orderId = ctx.request().getParam("orderId");
      final OrderId id = OrderId.builder().orderId(orderId).build();
      this.vertx.eventBus().send("claim.find.order.id", Json.encode(id),handler ->{
        if(handler.succeeded()){
          final List<Claim> claims = Json
            .decodeValue(handler.result().body().toString(), new TypeReference<List<Claim>>() {
            });
          ctx.response().putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(claims));
        }else {
          ctx.response().setStatusCode(204).end();
        }
      });
    });
    healthChecks.register("data", 2000, future -> future.complete(Status.OK()));
    vertx.createHttpServer().requestHandler(router).listen(8080);
  }

}
