package base.api.factionmanager;

import static base.api.auth.Roles.FACTION_MANAGER;
import base.service.ServerService;
import base.service.data.ServerInfo;
import com.github.apilab.rest.Endpoint;
import com.github.apilab.rest.exceptions.NotFoundException;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import javax.inject.Inject;

public class FactionGetServerInfoEndpoint implements Endpoint {

  @Inject ServerService service;

  @Inject
  public FactionGetServerInfoEndpoint() {
    ///
  }
  
  @Override
  public void register(Javalin javalin) {
    javalin.get("/factionmanager-api/factions/:faction/campaigns/:campaign/serverinfo", this, roles(FACTION_MANAGER));
  }

  @OpenApi(
    description = "",
    pathParams = {@OpenApiParam(name = "campaign", type = String.class)},
    responses = {@OpenApiResponse(status = "200", content = @OpenApiContent(from = ServerInfo.class))}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var campaign = ctx.pathParam("campaign");
    var info = service.getInfoFromCampaign(campaign);

    ctx.json(info.orElseThrow(() -> new NotFoundException("Campaign is not running on any server")));
    ctx.status(200);
  }

}
