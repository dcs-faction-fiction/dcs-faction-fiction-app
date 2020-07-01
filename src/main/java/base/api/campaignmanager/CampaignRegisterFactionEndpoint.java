package base.api.campaignmanager;

import static base.api.auth.Roles.CAMPAIGN_MANAGER;
import base.api.campaignmanager.data.CampaignFactionPayload;
import base.api.campaignmanager.data.ImmutableCampaignFactionPayload;
import base.service.CampaignService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import javax.inject.Inject;

public class CampaignRegisterFactionEndpoint implements Endpoint {

  @Inject CampaignService service;

  @Inject
  public CampaignRegisterFactionEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.post("/campaignmanager-api/campaigns/:campaign/factions", this, roles(CAMPAIGN_MANAGER));
  }

  @OpenApi(
    description = "Campaign reserved: Invite a new faction to the game",
    requestBody = @OpenApiRequestBody(
      content = @OpenApiContent(from = CampaignFactionPayload.class)),
    responses = {
      @OpenApiResponse(status = "201")
    }
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var campaign = ctx.pathParam("campaign");
    var req = ctx.bodyAsClass(ImmutableCampaignFactionPayload.class);

    service.registerFaction(campaign, req.faction(), req.airbase());

    ctx.result("{}");
    ctx.status(201);
  }

}
