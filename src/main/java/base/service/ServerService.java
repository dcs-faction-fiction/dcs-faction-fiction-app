package base.service;

import base.game.CampaignPhase;
import base.service.data.ImmutableServerInfo;
import base.service.data.ServerAction;
import static base.service.data.ServerAction.MISSION_STARTED;
import static base.service.data.ServerAction.START_NEW_MISSION;
import static base.service.data.ServerAction.STOP_MISSION;
import base.service.data.ServerInfo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Clock;
import java.util.Optional;
import java.util.function.Consumer;
import javax.inject.Inject;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

public class ServerService {

  @Inject Clock clock;
  @Inject Jdbi jdbi;
  @Inject CampaignService campaignService;

  @Inject
  public ServerService() {
    ////
  }

  public Optional<ImmutableServerInfo> getInfoFromCampaign(String campaign) {
    return jdbi.withHandle(h ->
      h.select("select address, port, password from server where campaign_name = ? and running = true", campaign)
        .mapTo(ImmutableServerInfo.class)
        .findOne()
    );
  }

  /**
   * Gets which campaign is currently running in this server.
   * Only when one is running a mission, otherwise after the warehouses are synced, it will be empty.
   * @param serverId the id of the server affected
   * @return the campaign name, it's a primary key for campaign
   */
  public Optional<String> getCampaign(String serverId) {
    return jdbi.withHandle(h -> {
      ensureServerExists(h, serverId);
      return h.select("select campaign_name from server where name = ?", serverId)
        .mapTo(String.class)
        .findOne();
    });
  }

  /**
   * Pulls the next action for the server.
   * This is for the daemon interface.
   * This method will reset the next action if called! So you will receive the action only ONCE.
   * @param serverId the id of the server affected
   * @return the next action to do.
   */
  public Optional<ServerAction> pullNextAction(String serverId) {
    return jdbi.withHandle(h -> {
      ensureServerExists(h, serverId);
      var action = h.select("select next_action from server where name = ?", serverId)
        .mapTo(ServerAction.class)
        .findOne();
      action.ifPresent(a ->
        h.execute(
          "update server "
            + "set next_action = null, current_action = ? "
            + "where name = ?",
          a, serverId)
      );
      return action;
    });
  }

  /**
   * Sets next action.It actually queues the action in the db, the daemon will take care of it.
   * @param serverid the id of the server affected
   * @param action
   * @param info
   */
  public void setNextAction(String serverid, ServerAction action, Optional<ServerInfo> info) {
    jdbi.useHandle((Handle h) -> {
      ensureServerExists(h, serverid);
      h.execute("update server"
        + " set next_action = ?"
        + " where name = ?",
        action,
        serverid);
      info.ifPresent(i ->
        h.execute("update server"
          + " set address = ?, port = ?, password = ?"
          + " where name = ?",
          i.address(), i.port(), i.password(),
          serverid));
      if (action == MISSION_STARTED) {
        h.execute("update server"
          + " set running = true, started_at = CURRENT_TIMESTAMP"
          + " where name = ?", serverid);
        getCampaign(serverid).ifPresent(c ->
          campaignService.setPhase(c, CampaignPhase.RUNNING)
        );
      }
      if (action == STOP_MISSION) {
        h.execute("update server"
          + " set running = false, started_at = null"
          + " where name = ?",
          serverid);
        // At mission stop, need to change campaign status
        getCampaign(serverid).ifPresent(c ->
          campaignService.setPhase(c, CampaignPhase.DECLARING)
        );
        // Need to clear out the mission data
        h.execute("update server"
          + " set mission_zip = null, campaign_name = null"
          + " where name = ?",
          serverid);
      }
    });
  }

  /**
   * Starts a new mission on the server.
   * It actually queues the action in the db, the daemon will take care of it.
   * @param serverid the id of the server affected
   * @param missionZipConsumer a consumer for writing the miz file to an output stream
   */
  public void pushNextMission(String serverid, String campaignName, Consumer<OutputStream> missionZipConsumer) {
    jdbi.useHandle((Handle h) -> {
      ensureServerExists(h, serverid);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      missionZipConsumer.accept(bos);
      h.execute("update server"
        + " set next_action = ?, mission_zip = ?, campaign_name = ?"
        + " where name = ?",
        START_NEW_MISSION,
        bos.toByteArray(),
        campaignName,
        serverid);
   });
  }

  /**
   * Downloads the current mission saved for the server.
   * @param serverid the id of the server affected
   * @param consumer a consumer from an input stream to read the miz file.
   */
  public void downloadMission(String serverid, Consumer<InputStream> consumer) {
    jdbi.useHandle(h -> {
      ensureServerExists(h, serverid);
      h.select("select mission_zip from server where name = ?", serverid)
        .mapTo(byte[].class)
        .findFirst()
        .ifPresent(b ->
          consumer.accept(new ByteArrayInputStream(b))
        );
    });
  }

  // Creates a server record in case it does not exist.
  // Since most save operations are updates which are safer, the insert would be
  // checked and done with this pre-method instead.
  private void ensureServerExists(Handle h, String serverid) {
    boolean exists = h.select("select name from server where name = ?", serverid)
      .mapTo(String.class)
      .findFirst()
      .isPresent();
    if (!exists) {
      h.execute("insert into server (name) values(?)", serverid);
    }
  }
}
