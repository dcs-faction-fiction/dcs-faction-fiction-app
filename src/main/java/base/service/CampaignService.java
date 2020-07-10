package base.service;

import base.api.campaignmanager.data.CampaignFactionPayload;
import base.api.campaignmanager.data.CampaignSituation;
import base.api.campaignmanager.data.ImmutableCampaignFactionPayload;
import base.api.campaignmanager.data.ImmutableCampaignSituation;
import base.api.campaignmanager.data.ImmutableCampaignSituationFaction;
import base.api.factionmanager.data.FactionCampaignComboPayload;
import base.api.factionmanager.data.ImmutableFactionCampaignComboPayload;
import base.api.factionmanager.data.ImmutableInventoryWarehousePayload;
import base.api.factionmanager.data.ImmutableInventoryWarehousePayloadItem;
import base.game.Airbases;
import static base.game.CampaignCoalition.BLUE;
import base.game.CampaignMap;
import static base.game.CampaignMap.CAUCASUS;
import base.game.CampaignPhase;
import static base.game.CampaignPhase.DECLARING;
import base.game.CampaignState;
import static base.game.CampaignState.PREPARING;
import base.game.FactionUnit;
import base.game.ImmutableFactionUnit;
import base.game.ImmutableLocation;
import base.game.units.GroundUnit;
import base.game.units.GroundUnitCost;
import base.game.warehouse.WarehouseItemCode;
import static java.lang.String.format;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;

public class CampaignService {

  public static final int ZONE_SIZE_FT_START = 50_000;
  public static final int CREDITS_START = 0;

  @Inject Jdbi jdbi;
  @Inject FlightLogService flightLog;

  @Inject
  public CampaignService() {
    ////
  }

  public void addCreditsToFaction(String campaignName, String factionName, int credits) {
    getCampaignFactionID(campaignName, factionName).ifPresent(cfid ->
      jdbi.useHandle(h -> {
        h.execute("update campaign_faction set credits = greatest(0, credits + ?) "
          + "where campaign_name = ? and faction_name = ?",
          credits, campaignName, factionName);

        if (credits >= 0)
          flightLog.logForCampaignFaction(cfid, format("Adding %s credits for faction %s in campaign %s", credits, factionName, campaignName), h);
        else
          flightLog.logForCampaignFaction(cfid, format("Removing %s credits for faction %s in campaign %s", -credits, factionName, campaignName), h);
      })
    );
  }

  public Optional<UUID> getCampaignFactionID(
      String campaignName, String factionName) {
    return jdbi.withHandle(h ->
      h.select("select id from campaign_faction "
        + "where campaign_name = ? and faction_name = ? "
        + "for update",
        campaignName, factionName)
        .mapTo(UUID.class)
        .findFirst()
    );
  }

  public Optional<UUID> getCampaignFactionID(
      String campaignName, String factionName, Airbases airbase) {
    return jdbi.withHandle(h ->
      h.select("select id from campaign_faction "
        + "where campaign_name = ? and faction_name = ? and airbase = ? "
        + "for update",
        campaignName, factionName, airbase)
        .mapTo(UUID.class)
        .findFirst()
    );
  }

  public List<String> getCampaigns(UUID user) {
    return jdbi.withHandle(h ->
      h.select("select name from campaign where manager_user = ? limit 10", user)
        .mapTo(String.class)
        .list()
    );
  }

  public List<CampaignFactionPayload> getFactions(String campaignName) {
    return jdbi.withHandle(h ->
      h.select("select faction_name, airbase from campaign_faction "
        + "where campaign_name = ? limit 100", campaignName)
        .map((RowMapper<CampaignFactionPayload>) (r, c) ->
          ImmutableCampaignFactionPayload
            .builder()
            .faction(r.getString(1))
            .airbase(Airbases.valueOf(r.getString(2)))
            .build())
        .list()
    );
  }

  public void registerFaction(String campaignName, String factionName, Airbases airbase) {
    jdbi.useHandle(h ->
      h.execute("insert into campaign_faction"
        + " (id, campaign_name, faction_name, airbase, zone_size_ft, credits, is_blue)"
        + " values(?, ?, ?, ?, ?, ?, ?)",
        UUID.randomUUID(), campaignName, factionName, airbase, ZONE_SIZE_FT_START, CREDITS_START, airbase.coalition() == BLUE)
    );
  }

  public void startNewCampaign(String campaignName, CampaignMap map, UUID owner) {
    jdbi.useHandle(h -> {
      h.execute("insert into campaign"
        + " (name, map, manager_user, state, phase)"
        + " values(?, ?, ?, ?, ?)",
        campaignName, map, owner, PREPARING, DECLARING);
      setState(campaignName, CampaignState.PREPARING);
    });
  }

  public CampaignMap getCampaignMap(String campaignName) {
    return jdbi.withHandle(h ->
       h.select("select map from campaign where name = ?", campaignName)
        .mapTo(CampaignMap.class)
        .findFirst()
        .orElse(CAUCASUS)
    );
  }

 public CampaignState getState(String campaignName, Handle h) {
    return h.select("select state from campaign where name = ?", campaignName)
      .mapTo(CampaignState.class)
      .first();
  }

  public CampaignPhase getPhase(String campaignName, Handle h) {
    return h.select("select phase from campaign where name = ?", campaignName)
      .mapTo(CampaignPhase.class)
      .first();
  }

  public CampaignState getState(String campaignName) {
    return jdbi.withHandle(h -> getState(campaignName, h));
  }

  public CampaignPhase getPhase(String campaignName) {
    return jdbi.withHandle(h -> getPhase(campaignName, h));
  }

  public void setState(String campaignName, CampaignState state) {
    jdbi.useHandle(h ->
      h.execute("update campaign "
        + "set state = ?"
        + "where name = ?",
        state, campaignName)
    );
  }

  public void setPhase(String campaignName, CampaignPhase phase) {
    jdbi.useHandle(h ->
      h.execute("update campaign "
        + "set phase = ?"
        + "where name = ?",
        phase, campaignName)
    );
  }

  // These queries are repeated here because they belong to the campaign manager
  // capability of viewing the user's values.
  // This may mean that they need special filtering permission allowance, or any other
  // check that is relative to the campaign manager role,
  // making it a differeny business requirement, and hence repeated here instead using
  // faction service.
  // Also that would create bad cyclic dependencies if one depended on another.
  // They also have a handler parameter to be executed in the same session.
  // They are also private because of this.

  private List<FactionCampaignComboPayload> getAllFactionsInCampaign(String campaignName, Handle h) {
    return h.select("select "
      + "cf.faction_name, "
      + "cf.campaign_name, "
      + "cf.airbase, "
      + "cf.zone_size_ft "
      + "from campaign_faction cf "
      + "left join faction f on cf.faction_name = f.name "
      + "where cf.campaign_name = ? ", campaignName)
      .map((RowMapper<FactionCampaignComboPayload>) (r, c) ->
        ImmutableFactionCampaignComboPayload
          .builder()
          .factionName(r.getString(1))
          .campaignName(r.getString(2))
          .airbase(Airbases.valueOf(r.getString(3)))
          .zoneSizeFt(r.getInt(4))
          .build())
      .list();
  }

  private int getFactionCredits(String factionName, String campaignName, Handle h) {
    return h.select("select credits from campaign_faction "
                  + "where campaign_name = ? and faction_name = ?",
                     campaignName, factionName)
      .mapTo(Integer.class)
      .findFirst()
      .orElse(0);
  }

  private List<FactionUnit> getUnits(String campaignName, String factionName, Airbases airbase, Handle h) {
    return h.select("select u.id, u.type, u.x, u.y, u.z, u.angle "
      + "from campaign_faction_units u "
      + "left join campaign_faction cf on u.campaign_faction_id = cf.id "
      + "where cf.campaign_name = ? and cf.faction_name = ?",
      campaignName, factionName)
      .map((RowMapper<FactionUnit>) (r, c) ->
        ImmutableFactionUnit
          .builder()
          .id(UUID.fromString(r.getString(1)))
          .type(GroundUnit.valueOf(r.getString(2)))
          .location(ImmutableLocation.builder()
            .longitude(new BigDecimal(r.getString(3)))
            .latitude(new BigDecimal(r.getString(4)))
            .altitude(new BigDecimal(r.getString(5)))
            .angle(new BigDecimal(r.getString(6)))
            .build())
          .build())
      .list();
  }

  private Map<WarehouseItemCode, Integer> getWarehouseForFaction(String campaignName, String factionName, Handle h) {
    Map<WarehouseItemCode, Integer> result = new EnumMap<>(WarehouseItemCode.class);
    h.select("select"
      + " i.item_code code,"
      + " i.item_quantity qty"
      + " from campaign_airfield_warehouse_item i"
      + " left join campaign_airfield_warehouse w on i.warehouse_id = w.id"
      + " left join campaign_faction cf on w.campaign_name = cf.campaign_name"
      + " left join faction f on cf.faction_name = f.name"
      + " where w.campaign_name = ? and cf.faction_name = ?",
      campaignName, factionName)
      .mapToMap()
      .forEach(m -> {
        var code = WarehouseItemCode.valueOf((String) m.get("code"));
        var qty = (Integer) m.get("qty");
        result.put(code, qty);
      });
    return result;
  }

  /**
   * Returns the full situation of a campaign visible to a campaign manager.
   * This method is EXPENSIVE and N+1 on all the factions plus N+1 on all their sub-items.
   * Use it sparingly.
   * Consider caching the endpoint that returns this.
   *
   * @param campaignName the name of the campaign
   * @return a full result containing all factions, units, warehouses of a campaign.
   */
  public CampaignSituation getSituation(String campaignName) {
    return jdbi.withHandle(h -> {
      var builder = ImmutableCampaignSituation.builder();

      builder.state(getState(campaignName, h));
      builder.phase(getPhase(campaignName, h));

      getAllFactionsInCampaign(campaignName, h).forEach(faction -> {

        var innerBuilder = ImmutableCampaignSituationFaction.builder();
        innerBuilder.faction(faction);

        innerBuilder.credits(getFactionCredits(faction.factionName(), campaignName, h));
        innerBuilder.units(getUnits(campaignName, faction.factionName(), faction.airbase(), h));

        var warehouse = ImmutableInventoryWarehousePayload.builder();
        getWarehouseForFaction(campaignName, faction.factionName(), h).entrySet().stream()
          .forEach(e ->
            warehouse.addItems(ImmutableInventoryWarehousePayloadItem.builder()
              .name(e.getKey().name())
              .amount(e.getValue())
              .build())
          );
        innerBuilder.warehouse(warehouse.build());

        builder.putFactions(faction.factionName(), innerBuilder.build());
      });

      return builder.build();
    });
  }

}
