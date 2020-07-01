package base.api.factionmanager;

import static base.api.auth.Roles.FACTION_MANAGER;
import base.api.factionmanager.data.FactionCampaignComboPayload;
import static base.config.EndpointLoginFetcher.requireLoggedInUserUUID;
import base.service.FactionService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import  io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import java.util.UUID;
import javax.inject.Inject;

public class FactionCampaignComboGetEndpoint implements Endpoint {

  @Inject FactionService factionService;

  @Inject
  public FactionCampaignComboGetEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.get("/factionmanager-api/factions/:faction/campaigns/:campaign", this, roles(FACTION_MANAGER));
  }

  @OpenApi(
    description = "gets the info details of this campaign's faction",
    responses = {@OpenApiResponse(
      status = "200",
      content = @OpenApiContent(from = FactionCampaignComboPayload.class)
    )}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    UUID user = requireLoggedInUserUUID(ctx);
    var faction = ctx.pathParam("faction");
    var campaign = ctx.pathParam("campaign");

    ctx.json(factionService.getFactionCampaignCombo(user, faction, campaign));
    ctx.status(200);
  }

}
