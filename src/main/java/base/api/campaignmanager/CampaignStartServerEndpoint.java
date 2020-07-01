package base.api.campaignmanager;

import static base.api.auth.Roles.CAMPAIGN_MANAGER;
import base.game.FullMissionBuilder;
import base.service.CampaignService;
import base.service.ServerService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import javax.inject.Inject;

public class CampaignStartServerEndpoint implements Endpoint {

  @Inject FullMissionBuilder missionBuilder;
  @Inject ServerService server;
  @Inject CampaignService campaignService;

  @Inject
  public CampaignStartServerEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.put("/campaignmanager-api/campaigns/:campaign/servers/:server", this, roles(CAMPAIGN_MANAGER));
  }

  @OpenApi(
    description = "Campaign reserved: Start next available mission on server",
    responses = @OpenApiResponse(status = "200")
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var campaign = ctx.pathParam("campaign");

    server.pushNextMission(ctx.pathParam("server"), campaign, out ->
      missionBuilder.build(campaign, out)
    );

    ctx.result("{}");
    ctx.status(200);
  }

}
