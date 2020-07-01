package base.api;

import static base.api.auth.Roles.CAMPAIGN_MANAGER;
import static base.api.auth.Roles.FACTION_MANAGER;
import base.api.data.EnumsPayload;
import static base.api.data.EnumsPayload.ENUMS;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import javax.inject.Inject;

public class FetchEnumsEndpoint implements Endpoint {

  @Inject
  public FetchEnumsEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.get("/common-api/enums", this, roles(FACTION_MANAGER, CAMPAIGN_MANAGER));
  }

  @OpenApi(
    description = "returns all enums",
    responses = {@OpenApiResponse(status = "200",
      content = @OpenApiContent(from = EnumsPayload.class))}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    ctx.json(ENUMS);
    ctx.status(200);
  }

}
