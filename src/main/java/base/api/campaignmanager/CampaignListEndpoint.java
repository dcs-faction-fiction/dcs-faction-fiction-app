
package base.api.campaignmanager;

import static base.api.auth.Roles.CAMPAIGN_MANAGER;
import static base.config.EndpointLoginFetcher.requireLoggedInUserUUID;
import base.service.CampaignService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import java.util.UUID;
import javax.inject.Inject;

public class CampaignListEndpoint implements Endpoint {

  @Inject CampaignService campaignService;

  @Inject
  public CampaignListEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.get("/campaignmanager-api/campaigns", this, roles(CAMPAIGN_MANAGER));
  }

  @OpenApi(
    description = "Get your campaigns",
    responses = {@OpenApiResponse(
      status  = "200",
      content = @OpenApiContent(from = String.class, isArray = true))}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    UUID user = requireLoggedInUserUUID(ctx);

    ctx.json(campaignService.getCampaigns(user));
    ctx.status(200);
  }



}
