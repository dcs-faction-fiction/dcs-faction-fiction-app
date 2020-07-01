package base.api.campaignmanager;

import static base.api.auth.Roles.CAMPAIGN_MANAGER;
import base.api.campaignmanager.data.CampaignSituation;
import base.service.CampaignService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import javax.inject.Inject;

public class CampaignGetSituationEndpoint implements Endpoint {

  @Inject CampaignService campaignService;

  @Inject
  public CampaignGetSituationEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.get("/campaignmanager-api/campaigns/:campaign/situation", this, roles(CAMPAIGN_MANAGER));
  }

  @OpenApi(
    description = "returns all the info for the current state of the campaign",
    responses = @OpenApiResponse(status = "200", content =
      @OpenApiContent(from = CampaignSituation.class))
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var campaignName = ctx.pathParam("campaign");
    var situation = campaignService.getSituation(campaignName);

    ctx.status(200);
    // Cache this result because getSituation is an expensive method.
    ctx.header("Cache-Control", "private,max-age=60");
    ctx.json(situation);
  }

}
