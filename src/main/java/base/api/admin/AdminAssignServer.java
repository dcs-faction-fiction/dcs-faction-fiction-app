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

public class AdminAssignServer implements Endpoint {

  @Inject AdminService service;

  @Inject
  public AdminAssignServer() {
    ///
  }

  @Override
  public void register(Javalin javalin) {
    javalin.post("/admin-api/users/:user/servers/:server", this, roles(ADMIN));
  }

  @OpenApi(
    description = "Assigns a server to a user, must own a campaign too.",
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

    service.assignServerToUser(user, server);
    ctx.status(200);
  }

}
