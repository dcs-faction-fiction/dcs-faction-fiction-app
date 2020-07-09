package base.api.factionmanager;

import static base.api.auth.Roles.FACTION_MANAGER;
import base.service.FlightLogService;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import javax.inject.Inject;

public class FactionViewLogEndpoint implements Endpoint {

  @Inject FlightLogService service;

  @Inject
  public FactionViewLogEndpoint() {
    ///
  }

  @Override
  public void register(Javalin javalin) {
    javalin.get("/factionmanager-api/factions/:faction/campaigns/:campaign/flightlog", this, roles(FACTION_MANAGER));
  }

  @Override
  public void handle(Context ctx) throws Exception {
    var faction = ctx.pathParam("faction");
    var campaign = ctx.pathParam("campaign");

    ctx.json(service.getLog(campaign, faction));
    ctx.status(200);
  }

}
