package base.api.factionmanager;

import static base.api.auth.Roles.FACTION_MANAGER;
import base.api.factionmanager.data.BuyUnitPayload;
import base.api.factionmanager.data.ImmutableBuyUnitPayload;
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
import javax.inject.Inject;

public class FactionBuyUnitEndpoint implements Endpoint {

  @Inject UnitService unitService;
  @Inject FactionService factionService;

  @Inject
  public FactionBuyUnitEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.post("/factionmanager-api/factions/:faction/campaigns/:campaign/units", this, roles(FACTION_MANAGER));
  }

  @OpenApi(
    description = "Buy a new unit set and place them",
    requestBody = @OpenApiRequestBody(
      content = @OpenApiContent(from = BuyUnitPayload.class)),
    responses = {@OpenApiResponse(status = "201")}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var faction = ctx.pathParam("faction");
    var campaign = ctx.pathParam("campaign");
    var airbase = factionService.assertGetAirbase(faction, campaign);
    var buyUnit = ctx.bodyAsClass(ImmutableBuyUnitPayload.class);

    unitService.buyUnit(campaign, faction, airbase, buyUnit.type(), buyUnit.location());

    ctx.result("{}");
    ctx.status(201);
  }

}
