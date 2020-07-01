package base.api.factionmanager;

import static base.api.auth.Roles.FACTION_MANAGER;
import base.service.FactionService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import javax.inject.Inject;

public class FactionListCampaignsEndpoint implements Endpoint {

  @Inject FactionService factionService;

  @Inject
  public FactionListCampaignsEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.get("/factionmanager-api/factions/:faction/campaigns", this, roles(FACTION_MANAGER));
  }

  @OpenApi(
    description = "get the campaigns that this faction is participating on",
    responses = {@OpenApiResponse(
      status = "200",
      content = @OpenApiContent(from = String.class, isArray = true)
    )}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var faction = ctx.pathParam("faction");

    ctx.json(factionService.getCampaigns(faction));
    ctx.status(200);
  }

}
