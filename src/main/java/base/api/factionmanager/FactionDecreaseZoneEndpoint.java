package base.api.factionmanager;

import static base.api.auth.Roles.FACTION_MANAGER;
import static base.game.GameValues.DECREASE_ZONE_GAIN;
import static base.game.GameValues.DECREASE_ZONE_SIZE_DECREASE;
import base.service.UnitService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import javax.inject.Inject;

public class FactionDecreaseZoneEndpoint implements Endpoint {

  @Inject UnitService unitService;

  @Inject
  public FactionDecreaseZoneEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.delete("/factionmanager-api/factions/:faction/campaigns/:campaign/zone", this, roles(FACTION_MANAGER));
  }

  @OpenApi(
    description = "Decrease a zone by 1 slot, "+DECREASE_ZONE_SIZE_DECREASE+"ft gaining back "+DECREASE_ZONE_GAIN+" credits",
    responses = {@OpenApiResponse(status = "200")}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var faction = ctx.pathParam("faction");
    var campaign = ctx.pathParam("campaign");
    unitService.shrinkZone(faction, campaign);
    ctx.result("{}");
    ctx.status(200);
  }

}
