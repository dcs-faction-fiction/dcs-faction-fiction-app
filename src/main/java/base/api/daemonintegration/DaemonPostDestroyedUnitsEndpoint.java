package base.api.daemonintegration;

import static base.api.auth.Roles.DAEMON;
import base.service.ServerService;
import base.service.UnitService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import java.util.Arrays;
import java.util.UUID;
import static java.util.stream.Collectors.toList;
import javax.inject.Inject;
import static net.logstash.logback.argument.StructuredArguments.kv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaemonPostDestroyedUnitsEndpoint implements Endpoint {

  private static final Logger LOG = LoggerFactory.getLogger(DaemonPostDestroyedUnitsEndpoint.class);

  @Inject ServerService serverService;
  @Inject UnitService unitService;

  @Inject
  public DaemonPostDestroyedUnitsEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.post("/daemon-api/:serverid/deadunits", this, roles(DAEMON));
  }

  @OpenApi(
    description = "Receives dead units from the mission",
    requestBody = @OpenApiRequestBody(
      content = @OpenApiContent(from = String.class, isArray = true)),
    responses = {@OpenApiResponse(status = "201")}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var serverId = ctx.pathParam("serverid");
    var deadUnits = ctx.bodyAsClass(String[].class);
    var uuids = Arrays.asList(deadUnits).stream().map(UUID::fromString).collect(toList());

    var campaignName = serverService.getCampaign(serverId);

    LOG.info("Dead units from campaign: {} {}",
      kv("campaign", campaignName),
      kv("uuids", uuids));

    unitService.registerDeadUnits(uuids);

    ctx.result("{}");
    ctx.status(201);
  }

}
