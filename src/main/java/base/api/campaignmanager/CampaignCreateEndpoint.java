package base.api.campaignmanager;

import static base.api.auth.Roles.CAMPAIGN_MANAGER;
import base.api.campaignmanager.data.ImmutableNewCampaignPayload;
import base.api.campaignmanager.data.NewCampaignPayload;
import static base.config.EndpointLoginFetcher.requireLoggedInUserUUID;
import base.game.CampaignMap;
import base.service.CampaignService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import java.util.UUID;
import javax.inject.Inject;

public class CampaignCreateEndpoint implements Endpoint {

  @Inject CampaignService service;

  @Inject
  public CampaignCreateEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.post("/campaignmanager-api/campaigns", this, roles(CAMPAIGN_MANAGER));
  }

  @OpenApi(
    description = "Campaign reserved: Create a new campaign",
    requestBody = @OpenApiRequestBody(content = @OpenApiContent(
      from = NewCampaignPayload.class
    )),
    responses = @OpenApiResponse(status = "201")
  )
  @Override
  public void handle(Context ctx) throws Exception {
    UUID user = requireLoggedInUserUUID(ctx);
    var req = ctx.bodyAsClass(ImmutableNewCampaignPayload.class);
    // no need to assert, user creates his own campaigns.
    // Also for now only caucasus available.
    service.startNewCampaign(req.name(), CampaignMap.CAUCASUS, user);

    ctx.result("{}");
    ctx.status(201);
  }

}
