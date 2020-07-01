package base.api.factionmanager;

import static base.api.auth.Roles.FACTION_MANAGER;
import base.api.factionmanager.data.ImmutableInventoryWarehousePayload;
import base.api.factionmanager.data.ImmutableInventoryWarehousePayloadItem;
import base.api.factionmanager.data.InventoryWarehousePayload;
import base.api.factionmanager.data.InventoryWarehousePayloadItem;
import base.service.FactionService;
import base.service.WarehouseService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import static java.util.stream.Collectors.toList;
import javax.inject.Inject;

public class FactionWarehouseGetEndpoint implements Endpoint {

  @Inject FactionService factionService;
  @Inject WarehouseService warehouseService;

  @Inject
  public FactionWarehouseGetEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.get("/factionmanager-api/factions/:faction/campaigns/:campaign/warehouse", this, roles(FACTION_MANAGER));
  }

  @OpenApi(
    description = "Gets the warehouse items",
    pathParams = {
      @OpenApiParam(name = "faction"),
      @OpenApiParam(name = "campaign")
    },
    responses = {
      @OpenApiResponse(status = "200",
        content = @OpenApiContent(from = InventoryWarehousePayload.class))
    }
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var faction = ctx.pathParam("faction");
    var campaign = ctx.pathParam("campaign");

    var airbase = factionService.assertGetAirbase(faction, campaign);

    var resp = ImmutableInventoryWarehousePayload.builder()
      .items(
        warehouseService
          .getWarehouseForFaction(campaign, faction, airbase)
          .entrySet()
          .stream()
          .map(e -> (InventoryWarehousePayloadItem)
              ImmutableInventoryWarehousePayloadItem.builder()
                .name(e.getKey().name())
                .amount(e.getValue())
                .build())
          .collect(toList())
      )
      .build();

    ctx.json(resp);
    ctx.status(200);
  }

}
