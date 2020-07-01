package base.api.campaignmanager;

import static base.api.auth.Roles.CAMPAIGN_MANAGER;
import base.api.campaignmanager.data.CampaignFactionPayload;
import base.service.CampaignService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import javax.inject.Inject;

public class CampaignListFactionsEndpoint implements Endpoint {

  @Inject CampaignService campaignService;

  @Inject
  public CampaignListFactionsEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.get("/campaignmanager-api/campaigns/:campaign/factions", this, roles(CAMPAIGN_MANAGER));
  }

  @OpenApi(
    description = "Get factions for this campaign",
    responses = {@OpenApiResponse(
      status = "200",
      content = @OpenApiContent(from = CampaignFactionPayload.class, isArray = true))}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var campaign = ctx.pathParam("campaign");

    var resp = campaignService.getFactions(campaign);
    ctx.json(resp);
    ctx.status(200);
  }

}
