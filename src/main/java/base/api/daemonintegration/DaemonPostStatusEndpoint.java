package base.api.daemonintegration;

import static base.api.auth.Roles.DAEMON;
import base.service.ServerService;
import base.service.data.ServerAction;
import base.service.data.ServerInfo;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import static java.util.Optional.of;
import javax.inject.Inject;

public class DaemonPostStatusEndpoint implements Endpoint{

  @Inject ServerService service;

  @Inject
  public DaemonPostStatusEndpoint() {
    ///
  }

  @Override
  public void register(Javalin javalin) {
    javalin.put("/daemon-api/:serverid/actions/:action", this, roles(DAEMON));
  }

  @OpenApi(
    description = "Sets the next action/state",
    pathParams = {@OpenApiParam(name = "action", type = ServerAction.class)},
    requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = ServerInfo.class)),
    responses = {@OpenApiResponse(status = "200")}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var server = ctx.pathParam("serverid");
    var action = ServerAction.valueOf(ctx.pathParam("action"));
    var info = ctx.bodyAsClass(ServerInfo.class);

    service.setNextAction(server, action, of(info));
    ctx.status(200);
  }

}
