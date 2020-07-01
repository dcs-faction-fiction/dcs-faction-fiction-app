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

public class FactionGetCreditsEndpoints implements Endpoint {

  @Inject FactionService factionService;

  @Inject
  public FactionGetCreditsEndpoints() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.get("/factionmanager-api/factions/:faction/campaigns/:campaign/credits", this, roles(FACTION_MANAGER));
  }

  @OpenApi(
    description = "Returns credits for the airbase of this faction to this campaign.",
    responses = {@OpenApiResponse(
      status = "200",
      content = @OpenApiContent(from = Integer.class))}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var faction = ctx.pathParam("faction");
    var campaign = ctx.pathParam("campaign");

    var airbase = factionService.assertGetAirbase(faction, campaign);
    var credits = factionService.getCredits(faction, campaign, airbase);

    ctx.json(credits);
    ctx.status(200);
  }

}
