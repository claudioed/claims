package tech.claudioed.claims;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() {
    final DeploymentOptions deploymentOptions = new DeploymentOptions();
    deploymentOptions.setWorker(true);
    this.vertx.deployVerticle(new RegisterClaimVerticle(),deploymentOptions);
    HealthCheckHandler healthChecks = HealthCheckHandler.create(vertx);
    Router router = Router.router(vertx);
    router.get("/health").handler(healthChecks);
    healthChecks.register("data", 2000, future -> future.complete(Status.OK()));
    vertx.createHttpServer().requestHandler(router).listen(8080);
  }

}
