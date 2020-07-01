package base.api;

import base.api.data.ImmutableNewJWTPayload;
import base.api.data.NewJWTPayload;
import base.service.UserService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import javax.inject.Inject;

public class NewJWTEndpoint implements Endpoint {

  @Inject UserService userService;

  @Inject
  public NewJWTEndpoint() {
    ////
  }

  @Override
  public void register(Javalin javalin) {
    javalin.post("/common-api/newjwt", this);
  }

  @OpenApi(
    description = "Creates new token and sends link to email.",
    requestBody = @OpenApiRequestBody(
      content = @OpenApiContent(from = NewJWTPayload.class)),
    responses = {@OpenApiResponse(status = "201")}
  )
  @Override
  public void handle(Context ctx) throws Exception {
    var req = ctx.bodyAsClass(ImmutableNewJWTPayload.class);
    userService.newJWTTokenForEmail(req.url(), req.email());
    ctx.status(201);
  }

}
