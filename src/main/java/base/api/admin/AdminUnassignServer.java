package base.api.admin;

import static base.api.auth.Roles.ADMIN;
import static base.config.EndpointLoginFetcher.requireLoggedInUserUUID;
import base.service.AdminService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import java.util.UUID;
import javax.inject.Inject;

public class AdminUnassignServer implements Endpoint {

  @Inject AdminService service;

  @Inject
  public AdminUnassignServer() {
    ///
  }

  @Override
  public void register(Javalin javalin) {
    javalin.delete("/admin-api/users/:user/servers/:server", this, roles(ADMIN));
  }

  @OpenApi(
    description = "Unassign a server from a user.",
    pathParams = {
      @OpenApiParam(name = "user", type = UUID.class),
      @OpenApiParam(name = "server", type = String.class)
    },
    responses = {@OpenApiResponse(status = "200")}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    UUID user = requireLoggedInUserUUID(ctx);
    var server = ctx.pathParam("server");

    service.unassignServerFromUser(user, server);
    ctx.status(200);
  }

}
