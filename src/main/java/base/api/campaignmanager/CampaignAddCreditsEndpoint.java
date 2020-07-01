package base.api.campaignmanager;

import static base.api.auth.Roles.CAMPAIGN_MANAGER;
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

public class CampaignAddCreditsEndpoint implements Endpoint {

  @Inject CampaignService campaignService;

  @Inject
  public CampaignAddCreditsEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.post("/campaignmanager-api/campaigns/:campaign/add-credits-to-faction/:faction", this, roles(CAMPAIGN_MANAGER));
  }

  @OpenApi(
    description = "Adds credits to faction (negative values also supported)",
    requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Integer.class)),
    responses = {@OpenApiResponse(status = "200")}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var campaign = ctx.pathParam("campaign");
    var faction = ctx.pathParam("faction");
    var credits = ctx.bodyAsClass(Integer.class);
    campaignService.addCreditsToFaction(campaign, faction, credits);
    ctx.status(200);
  }

}
