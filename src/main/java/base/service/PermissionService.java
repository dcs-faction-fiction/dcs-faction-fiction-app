package base.service;

import com.github.apilab.rest.exceptions.NotAuthorizedException;
import static java.lang.String.format;
import java.util.UUID;
import javax.inject.Inject;
import org.jdbi.v3.core.Jdbi;

public class PermissionService {

  @Inject Jdbi jdbi;

  @Inject
  public PermissionService() {
    ////
  }

  public void factionManagerCanAccessFaction(UUID user, String faction) {
    var found = jdbi.withHandle(h -> h.select(
      "select name from faction "
        + "where name = ? and commander_user = ?",
      faction, user)
      .mapTo(String.class)
      .findFirst());

    if (!found.isPresent()) {
      throw new NotAuthorizedException(format(
        "User %s does not manage this faction: %s",
        user, faction));
    }
  }

  public void factionManagerCanAccessFactionAndCampaign(UUID user, String faction, String campaign) {
    var found = jdbi.withHandle(h -> h.select(
      "select cf.id from campaign_faction cf "
        + "left join faction f on cf.faction_name = f.name "
        + "where cf.faction_name = ? and cf.campaign_name = ? and f.commander_user = ?",
      faction, campaign, user)
      .mapTo(String.class)
      .findFirst());

    if (!found.isPresent()) {
      throw new NotAuthorizedException(format(
        "User %s does not manage this faction: %s",
        user, faction));
    }
  }

  public void campaignManagerCanAccessCampaign(UUID user, String campaign) {
    var found = jdbi.withHandle(h -> h.select(
      "select name from campaign "
      + "where name = ? and manager_user = ?",
      campaign, user)
      .mapTo(String.class)
      .findFirst());

    if (!found.isPresent()) {
      throw new NotAuthorizedException(format(
        "User %s does not manage this campaign: %s",
        user, campaign));
    }
  }

  public void userCanManageServer(UUID user, String server) {
    var found = jdbi.withHandle(h -> h.select(
      "select server_name from user_server "
      + "where user_id = ? and server_name = ?",
      user, server)
      .mapTo(String.class)
      .findFirst());

    if (!found.isPresent()) {
      throw new NotAuthorizedException(format(
        "User %s does not manage this server: %s",
        user, server));
    }
  }

}
