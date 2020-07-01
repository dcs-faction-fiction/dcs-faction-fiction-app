
package base.api.factionmanager;

import static base.api.auth.Roles.FACTION_MANAGER;
import static base.config.EndpointLoginFetcher.requireLoggedInUserUUID;
import base.service.FactionService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import java.util.UUID;
import javax.inject.Inject;

public class FactionListEndpoint implements Endpoint {

  @Inject FactionService factionService;

  @Inject
  public FactionListEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.get("/factionmanager-api/factions", this, roles(FACTION_MANAGER));
  }

  @OpenApi(
    description = "Get your factions",
    responses = {@OpenApiResponse(
      status  = "200",
      content = @OpenApiContent(from = String.class, isArray = true))}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    UUID user = requireLoggedInUserUUID(ctx);

    ctx.json(factionService.getFactions(user));
    ctx.status(200);
  }

}
