package base.api.daemonintegration;

import static base.api.auth.Roles.DAEMON;
import base.service.ServerService;
import base.service.data.ServerAction;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import javax.inject.Inject;

public class DaemonNextActionEndpoint implements Endpoint {

  @Inject ServerService server;

  @Inject
  public DaemonNextActionEndpoint() {
    // Constructor injector
  }

  @Override
  public void register(Javalin javalin) {
    javalin.get("/daemon-api/:serverid/actions", this, roles(DAEMON));
  }

  @OpenApi(
    description = "Daemon reserved: checks next action for server",
    pathParams = {
      @OpenApiParam(name = "serverid", type = String.class)
    },
    responses = {
      @OpenApiResponse(
        content = @OpenApiContent(from = ServerAction.class),
        status = "200")
    })
  @Override
  public void handle(Context ctx) throws Exception {
    var serverId = ctx.pathParam("serverid");
    ctx.json(server.pullNextAction(serverId));
    ctx.status(200);
  }

}
