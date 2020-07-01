package base.api.campaignmanager;

import static base.api.auth.Roles.CAMPAIGN_MANAGER;
import static base.api.auth.Roles.FACTION_MANAGER;
import base.game.CampaignState;
import base.service.CampaignService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import javax.inject.Inject;

public class CampaignGetStateEndpoint implements Endpoint {

  @Inject CampaignService campaignService;

  @Inject
  public CampaignGetStateEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.get("/campaignmanager-api/campaigns/:campaign/state", this, roles(CAMPAIGN_MANAGER));
    // This endpoint is special, faction managers can read it too
    // Need this extra mapping because permissions are path based
    javalin.get("/factionmanager-api/factions/:faction/campaigns/:campaign/state", this, roles(FACTION_MANAGER));
  }

  @OpenApi(
    description = "get the campaign state",
    responses = {@OpenApiResponse(status = "200", content = @OpenApiContent(from = CampaignState.class))})
  @Override
  public void handle(Context ctx) throws Exception {
    var campaignName = ctx.pathParam("campaign");
    ctx.json(campaignService.getState(campaignName));
    ctx.status(200);
  }

}
