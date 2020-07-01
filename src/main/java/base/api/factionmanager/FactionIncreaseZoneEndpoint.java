package base.api.factionmanager;

import static base.api.auth.Roles.FACTION_MANAGER;
import static base.game.GameValues.INCREASE_ZONE_COST;
import static base.game.GameValues.INCREASE_ZONE_SIZE_INCREASE;
import base.service.UnitService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import javax.inject.Inject;

public class FactionIncreaseZoneEndpoint implements Endpoint {

  @Inject UnitService unitService;

  @Inject
  public FactionIncreaseZoneEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.post("/factionmanager-api/factions/:faction/campaigns/:campaign/zone", this, roles(FACTION_MANAGER));
  }

  @OpenApi(
    description = "Increase a zone by 1 slot, "+INCREASE_ZONE_SIZE_INCREASE+"ft costing "+INCREASE_ZONE_COST+" credits",
    responses = {@OpenApiResponse(status = "200")}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var faction = ctx.pathParam("faction");
    var campaign = ctx.pathParam("campaign");
    unitService.increaseZone(faction, campaign);
    ctx.result("{}");
    ctx.status(200);
  }

}
