package base.api.factionmanager;

import static base.api.auth.Roles.FACTION_MANAGER;
import base.api.factionmanager.data.ImmutableInventoryWarehousePayload;
import base.api.factionmanager.data.InventoryWarehousePayload;
import base.game.warehouse.WarehouseItemCode;
import base.service.FactionService;
import base.service.WarehouseService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import java.util.EnumMap;
import java.util.Map;
import javax.inject.Inject;

public class FactionWarehouseBuyEndpoint implements Endpoint {

  @Inject FactionService factionService;
  @Inject WarehouseService warehouseService;

  @Inject
  public FactionWarehouseBuyEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.post("/factionmanager-api/factions/:faction/campaigns/:campaign/warehouse", this, roles(FACTION_MANAGER));
  }

  @OpenApi(
    description = "Buy a new set of items for the warehouse",
    requestBody = @OpenApiRequestBody(
      content = @OpenApiContent(from = InventoryWarehousePayload.class)),
    responses = @OpenApiResponse(status = "201")
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var faction = ctx.pathParam("faction");
    var campaign = ctx.pathParam("campaign");
    var req = ctx.bodyAsClass(ImmutableInventoryWarehousePayload.class);
    Map<WarehouseItemCode, Integer> items = new EnumMap<>(WarehouseItemCode.class);
    req.items().stream().forEach(i ->
      items.put(WarehouseItemCode.valueOf(i.name()), i.amount())
    );

    var airbase = factionService.assertGetAirbase(faction, campaign);

    warehouseService.buyWarehouseItems(campaign, faction, airbase, items);

    ctx.result("{}");
    ctx.status(201);
  }
}
