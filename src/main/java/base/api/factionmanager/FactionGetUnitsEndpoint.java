package base.api.factionmanager;

import static base.api.auth.Roles.FACTION_MANAGER;
import base.service.FactionService;
import base.service.UnitService;
import base.game.FactionUnit;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import javax.inject.Inject;

public class FactionGetUnitsEndpoint implements Endpoint {

  @Inject UnitService unitService;
  @Inject FactionService factionService;

  @Inject
  public FactionGetUnitsEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.get("/factionmanager-api/factions/:faction/campaigns/:campaign/units", this, roles(FACTION_MANAGER));
  }

  @OpenApi(
    description = "Get all your units",
    responses = {@OpenApiResponse(
      status  = "200",
      content = @OpenApiContent(from = FactionUnit.class, isArray = true))}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var faction = ctx.pathParam("faction");
    var campaign = ctx.pathParam("campaign");
    var airbase = factionService.assertGetAirbase(faction, campaign);

    var resp = unitService.getUnits(campaign, faction, airbase);
    ctx.json(resp);
    ctx.status(200);
  }

}
