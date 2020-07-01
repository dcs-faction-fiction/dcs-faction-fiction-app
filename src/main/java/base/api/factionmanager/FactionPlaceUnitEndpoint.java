package base.api.factionmanager;

import static base.api.auth.Roles.FACTION_MANAGER;
import base.game.ImmutableLocation;
import base.game.Location;
import base.service.FactionService;
import base.service.UnitService;
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

public class FactionPlaceUnitEndpoint implements Endpoint {

  @Inject UnitService unitService;
  @Inject FactionService factionService;

  @Inject
  public FactionPlaceUnitEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.put("/factionmanager-api/factions/:faction/campaigns/:campaign/units/:id", this, roles(FACTION_MANAGER));
  }

  @OpenApi(
    description = "Moves a unit in a different position",
    requestBody = @OpenApiRequestBody(
      content = @OpenApiContent(from = Location.class)),
    responses = {@OpenApiResponse(status = "200")}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var faction = ctx.pathParam("faction");
    var campaign = ctx.pathParam("campaign");
    var airbase = factionService.assertGetAirbase(faction, campaign);
    var id = UUID.fromString(ctx.pathParam("id"));
    var location = ctx.bodyAsClass(ImmutableLocation.class);

    unitService.placeUnit(campaign, faction, airbase, id, location);

    ctx.result("{}");
    ctx.status(200);
  }

}
