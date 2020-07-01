package base.api.auth;

import static base.config.EndpointLoginFetcher.requireLoggedInUser;
import static base.config.EndpointLoginFetcher.requireLoggedInUserUUID;
import base.service.PermissionService;
import com.github.apilab.rest.Endpoint;
import com.github.apilab.rest.exceptions.NotAuthorizedException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import static java.lang.String.format;
import java.util.UUID;
import javax.inject.Inject;

public class EndpointPermissionFilter implements Endpoint {

  @Inject PermissionService service;

  @Inject
  public EndpointPermissionFilter() {
    ////
  }

  @Override
  public void register(Javalin javalin) {

    javalin.before("/factionmanager-api/factions/:faction", this::checkFaction);
    javalin.before("/factionmanager-api/factions/:faction/*", this::checkFaction);

    javalin.before("/factionmanager-api/factions/:faction/campaigns/:campaign", this::checkFactionCampaign);
    javalin.before("/factionmanager-api/factions/:faction/campaigns/:campaign/*", this::checkFactionCampaign);

    javalin.before("/campaignmanager-api/campaigns/:campaign", this::checkCampaign);
    javalin.before("/campaignmanager-api/campaigns/:campaign/*", this::checkCampaign);

    javalin.before("/campaignmanager-api/campaigns/:campaign/servers/:server", this::checkServer);
    javalin.before("/campaignmanager-api/campaigns/:campaign/servers/:server/*", this::checkServer);

    javalin.before("/daemon-api/:serverid", this::checkDaemon);
    javalin.before("/daemon-api/:serverid/*", this::checkDaemon);

  }

  public void checkFaction(Context c) {
    UUID user = requireLoggedInUserUUID(c);
    var faction = c.pathParam("faction");
    service.factionManagerCanAccessFaction(user, faction);
  }

  public void checkFactionCampaign(Context c) {
    UUID user = requireLoggedInUserUUID(c);
    var faction = c.pathParam("faction");
    var campaign = c.pathParam("campaign");
    service.factionManagerCanAccessFactionAndCampaign(user, faction, campaign);
  }

  public void checkCampaign(Context c) {
    UUID user = requireLoggedInUserUUID(c);
    var campaign = c.pathParam("campaign");
    service.campaignManagerCanAccessCampaign(user, campaign);
  }

  public void checkServer(Context c) {
    UUID user = requireLoggedInUserUUID(c);
    var server = c.pathParam("server");
    service.userCanManageServer(user, server);
  }

  public void checkDaemon(Context c) {
    var user = requireLoggedInUser(c);
    var server = c.pathParam("serverid");
    if (!user.equals(server)) {
      throw new NotAuthorizedException(
        format(
          "Daemon server %s is not allowed for this endpoint %s.",
          user,
          server
        ));
    }
  }

  @Override
  public void handle(Context ctx) throws Exception {
    ////
  }

}
