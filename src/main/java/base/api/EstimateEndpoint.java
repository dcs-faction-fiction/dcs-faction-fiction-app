package base.api;

import static base.api.auth.Roles.CAMPAIGN_MANAGER;
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

public class EstimateEndpoint implements Endpoint {

  @Inject FactionService factionService;
  @Inject WarehouseService warehouseService;

  @Inject
  public EstimateEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.post("/common-api/warehouseestimate", this, roles(FACTION_MANAGER, CAMPAIGN_MANAGER));
  }

  @OpenApi(
    description = "Estimate the cost of the payload",
    requestBody = @OpenApiRequestBody(
      content = @OpenApiContent(from = InventoryWarehousePayload.class)),
    responses = @OpenApiResponse(status = "200",
      content = @OpenApiContent(from = Integer.class))
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var req = ctx.bodyAsClass(ImmutableInventoryWarehousePayload.class);
    Map<WarehouseItemCode, Integer> items = new EnumMap<>(WarehouseItemCode.class);
    req.items().stream().forEach(i ->
      items.put(WarehouseItemCode.valueOf(i.name()), i.amount())
    );

    ctx.json(WarehouseService.calculateCreditsForBuy(items));
    ctx.status(200);
  }
}
