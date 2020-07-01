package base.api.factionmanager;

import static base.api.auth.Roles.FACTION_MANAGER;
import base.api.factionmanager.data.ImmutableNewFactionPayload;
import base.api.factionmanager.data.NewFactionPayload;
import static base.config.EndpointLoginFetcher.requireLoggedInUserUUID;
import base.service.FactionService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import java.util.UUID;
import javax.inject.Inject;

public class FactionCreateEndpoint implements Endpoint {

  @Inject FactionService factionService;

  @Inject
  public FactionCreateEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.post("/factionmanager-api/factions", this, roles(FACTION_MANAGER));
  }

  @OpenApi(
    description = "Creates a new faction for the current user.",
    requestBody = @OpenApiRequestBody(
      content = @OpenApiContent(from = NewFactionPayload.class)),
    responses = {@OpenApiResponse(status = "201")}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    UUID user = requireLoggedInUserUUID(ctx);
    var req = ctx.bodyAsClass(ImmutableNewFactionPayload.class);

    factionService.createFaction(user, req.name());

    ctx.result("{}");
    ctx.status(201);
  }



}
