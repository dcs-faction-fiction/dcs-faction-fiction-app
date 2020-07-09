package base.service;

import static java.util.Collections.emptyList;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

public class FlightLogService {

  @Inject Jdbi jdbi;

  @Inject
  public FlightLogService() {
    ///
  }

  public List<String> getLog(String campaignName, String factionName) {
    return jdbi.withHandle(h -> h
      .select("select id from campaign_faction where campaign_name = ? and faction_name = ?", campaignName, factionName)
      .mapTo(UUID.class)
      .findFirst()
      .map(campaignFactionId ->
        h.select("select description from campaign_faction_flight_log where campaign_faction_id = ? order by created_by", campaignFactionId)
          .mapTo(String.class)
          .list()
      )
      .orElse(emptyList())
    );
  }

  public void logForWarehouse(UUID warehouseId, String description, Handle h) {
    h.select("select cf.id from campaign_faction cf"
      + " left join campaign_airfield_warehouse wh"
      + " on wh.campaign_name = cf.campaign_name and wh.airbase = cf.airbase"
      + " where wh.id = ?"
      + " limit 1", warehouseId)
      .mapTo(UUID.class)
      .findFirst()
      .ifPresent(campaignFactionId -> {
        h.execute("insert into campaign_faction_flight_log"
          + " (id, campaign_faction_id, description)"
          + " values(?, ?, ?)",
          UUID.randomUUID(), campaignFactionId, description);
      });
  }

  public void logForCampaignFaction(UUID campaignFactionId, String description, Handle h) {
    h.execute("insert into campaign_faction_flight_log"
      + " (id, campaign_faction_id, description)"
      + " values(?, ?, ?)",
      UUID.randomUUID(), campaignFactionId, description);
  }

  public void logForUnit(UUID unitId, String description, Handle h) {
    h.select("select campaign_faction_id from campaign_faction_units where id = ?", unitId)
      .mapTo(UUID.class)
      .findFirst()
      .ifPresent(campaignFactionId -> {
        h.execute("insert into campaign_faction_flight_log"
          + " (id, campaign_faction_id, description)"
          + " values(?, ?, ?)",
          UUID.randomUUID(), campaignFactionId, description);
      });
  }

}
