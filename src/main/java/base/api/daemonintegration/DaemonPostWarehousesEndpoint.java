package base.api.daemonintegration;

import static base.api.auth.Roles.DAEMON;
import base.api.daemonintegration.data.ImmutableWarehousesSpent;
import base.api.daemonintegration.data.WarehousesSpent;
import base.service.ServerService;
import base.service.WarehouseService;
import static base.service.data.ServerAction.STOP_MISSION;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import static java.util.Optional.empty;
import javax.inject.Inject;
import static net.logstash.logback.argument.StructuredArguments.kv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaemonPostWarehousesEndpoint implements Endpoint {

  private static final Logger LOG = LoggerFactory.getLogger(DaemonPostWarehousesEndpoint.class);

  @Inject ServerService server;
  @Inject WarehouseService warehouseService;

  @Inject
  public DaemonPostWarehousesEndpoint() {
    // Constructor injector
  }

  @Override
  public void register(Javalin javalin) {
    javalin.post("/daemon-api/:serverid/warehouses", this, roles(DAEMON));
  }

  @OpenApi(
    description = "Daemon reserved: submits spent ammo from mission",
    pathParams = {
      @OpenApiParam(name = "serverid", type = String.class)
    },
    requestBody = @OpenApiRequestBody(
      content = @OpenApiContent(from = WarehousesSpent.class)),
    responses = {@OpenApiResponse(status = "201")})
  @Override
  public void handle(Context ctx) throws Exception {
    var serverId = ctx.pathParam("serverid");
    var spent = ctx.bodyAsClass(ImmutableWarehousesSpent.class);
    var wareshouseDelta = spent.toWarehouseDelta();

    var campaignName = server.getCampaign(serverId);
    LOG.info("Warehouse delta from campaign: {} {}",
      kv("campaign", campaignName),
      kv("warehouse", wareshouseDelta));

    campaignName.ifPresent(campaign -> {
      warehouseService.updateWarehouses(campaign, wareshouseDelta);
      server.setNextAction(serverId, STOP_MISSION, empty());
    });

    ctx.result("{}");
    ctx.status(201);
  }

}
