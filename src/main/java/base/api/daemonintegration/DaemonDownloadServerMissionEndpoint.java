package base.api.daemonintegration;

import static base.api.auth.Roles.DAEMON;
import base.service.ServerService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import javax.inject.Inject;

public class DaemonDownloadServerMissionEndpoint implements Endpoint {

  @Inject ServerService server;

  @Inject
  public DaemonDownloadServerMissionEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.get("/daemon-api/:serverid/missions", this, roles(DAEMON));
  }

  @OpenApi(
    description = "Daemon reserved: download next mission file for campaign",
    pathParams = {
      @OpenApiParam(name = "serverid", type = String.class)
    },
    responses = {
      @OpenApiResponse(status = "200", content = @OpenApiContent(type = "application/zip", from = byte[].class))
    })
  @Override
  public void handle(Context ctx) throws Exception {
    var serverId = ctx.pathParam("serverid");
    ctx.status(200);
    ctx.contentType("application/zip");
    server.downloadMission(serverId, ctx::result);
  }

}
