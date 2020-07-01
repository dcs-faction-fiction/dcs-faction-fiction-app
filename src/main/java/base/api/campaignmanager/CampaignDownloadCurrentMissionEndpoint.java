package base.api.campaignmanager;

import static base.api.auth.Roles.CAMPAIGN_MANAGER;
import base.game.FullMissionBuilder;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.inject.Inject;

public class CampaignDownloadCurrentMissionEndpoint implements Endpoint {

  @Inject FullMissionBuilder missionBuilder;

  @Inject
  public CampaignDownloadCurrentMissionEndpoint() {
    ///
  }

  @Override
  public void register(Javalin javalin) {
    javalin.get("/campaignmanager-api/campaigns/:campaign/download-mission", this, roles(CAMPAIGN_MANAGER));
  }

  @OpenApi(
    description = "downloads the current mission for the current status of things",
    responses = {@OpenApiResponse(status = "200")}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var campaign = ctx.pathParam("campaign");

    var bytes = new ByteArrayOutputStream();
    missionBuilder.build(campaign, bytes);

    ctx.result(new ByteArrayInputStream(bytes.toByteArray()));
    ctx.status(200);
  }
}
