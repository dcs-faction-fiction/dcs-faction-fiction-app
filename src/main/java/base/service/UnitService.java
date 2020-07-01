package base.service;

import base.game.Airbases;
import base.game.CampaignCoalition;
import static base.game.CampaignCoalition.BLUE;
import static base.game.GameValues.DECREASE_ZONE_GAIN;
import static base.game.GameValues.DECREASE_ZONE_SIZE_DECREASE;
import static base.game.GameValues.INCREASE_ZONE_COST;
import static base.game.GameValues.INCREASE_ZONE_SIZE_INCREASE;
import static base.game.GameValues.ZONE_MIN_SIZE;
import base.game.ImmutableLocation;
import base.game.Location;
import base.game.units.GroundUnit;
import base.service.data.FactionUnit;
import base.service.data.ImmutableFactionUnit;
import base.service.data.ImmutableFactionUnitPosition;
import com.github.apilab.rest.exceptions.NotFoundException;
import com.github.apilab.rest.exceptions.ServerException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import static net.logstash.logback.argument.StructuredArguments.kv;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitService {

  private static final Logger LOG = LoggerFactory.getLogger(UnitService.class);

  @Inject Jdbi jdbi;
  @Inject FactionService factionService;
  @Inject CampaignService campaignService;

  @Inject
  public UnitService() {
    ////
  }

  public void registerDeadUnits(List<UUID> uuids) {
    jdbi.useHandle(h ->
      uuids.forEach(uuid ->
        h.execute("delete from campaign_faction_units where id = ?", uuid)
      )
    );
  }

  public void registerMovedUnits(List<ImmutableFactionUnitPosition> units) {
    jdbi.useHandle(h ->
      units.stream().forEach(u ->
        h.execute("update campaign_faction_units set "
          + "x = ?, "
          + "y = ?, "
          + "z = ?, "
          + "angle = ?"
          + "where id = ?",
          u.location().longitude().toString(),
          u.location().latitude().toString(),
          u.location().altitude().toString(),
          u.location().angle().toString(),
          u.id())
      )
    );
  }

  public void increaseZone(String factionName, String campaignName) {
    jdbi.useHandle(h -> {
      UUID cfid = h.select("select id from campaign_faction "
        + "where campaign_name = ? and faction_name = ? "
        + "for update",
        campaignName, factionName)
        .mapTo(UUID.class)
        .first();

      var creditsAvailable = h.select(
        "select credits from campaign_faction where id = ? for update", cfid)
        .mapTo(Integer.class).findFirst().orElse(0);
      if (creditsAvailable < INCREASE_ZONE_COST) {
        throw new ServerException(422, "Not enough credits");
      }

      h.execute("update campaign_faction set "
        + "zone_size_ft = zone_size_ft + ?, "
        + "credits = greatest(0, credits - ?) "
        + "where id = ?",
        INCREASE_ZONE_SIZE_INCREASE, INCREASE_ZONE_COST, cfid);
    });
  }

  public void shrinkZone(String factionName, String campaignName) {
    jdbi.useHandle(h -> {
      UUID cfid = h.select("select id from campaign_faction "
        + "where campaign_name = ? and faction_name = ? "
        + "for update",
        campaignName, factionName)
        .mapTo(UUID.class)
        .first();

      var sizeAvailable = h.select(
        "select zone_size_ft from campaign_faction where id = ? for update", cfid)
        .mapTo(Integer.class).findFirst().orElse(0);
      if (sizeAvailable <= ZONE_MIN_SIZE) {
        throw new ServerException(422, "Not enough zone to decrease");
      }

      h.execute("update campaign_faction set "
        + "zone_size_ft = zone_size_ft - ?, "
        + "credits = greatest(0, credits + ?) "
        + "where id = ?",
        DECREASE_ZONE_SIZE_DECREASE, DECREASE_ZONE_GAIN, cfid);
    });
  }

  public List<FactionUnit> getUnits(
    String campaignName, String factionName, Airbases airbase) {

    return jdbi.withHandle(h -> {
      UUID cfid = campaignService.getCampaignFactionID(campaignName, factionName, airbase)
        .orElseThrow(() -> new NotFoundException("Faction is not registered to this campaign."));

      return h.select("select id, type, x, y, z, angle from campaign_faction_units "
        + "where campaign_faction_id = ? "
        + "limit 1000",
        cfid)
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
    });
  }

  public UUID buyUnit(
    String campaignName, String factionName, Airbases airbase,
    GroundUnit unit, Location location) {

    LOG.info("{} is spending in {}, using {} to get {} {} at {}",
      kv("faction", factionName),
      kv("campaign", campaignName),
      kv("credits", unit.cost()),
      kv("item", unit),
      kv("amount", unit.amount()),
      kv("location", location));

    return jdbi.withHandle(h -> {
      UUID uid = UUID.randomUUID();

      UUID cfid = campaignService.getCampaignFactionID(campaignName, factionName, airbase)
        .orElseThrow(() -> new NotFoundException("Faction is not registered to this campaign."));

      var creditsAvailable = h.select(
        "select credits from campaign_faction where id = ? for update", cfid)
        .mapTo(Integer.class).findFirst().orElse(0);
      if (creditsAvailable < unit.cost()) {
        throw new ServerException(422, "Not enough credits");
      }

      h.execute("insert into campaign_faction_units "
        + "(id, campaign_faction_id, type, x, y, z, angle) "
        + "values(?, ?, ?, ?, ?, ?, ?)",
        uid, cfid, unit, location.longitude(), location.latitude(), location.altitude(), location.angle());

      h.execute("update campaign_faction set credits = greatest(0, credits - ?) where id = ?",
        unit.cost(), cfid);

      enforceUnitRadiusFromAirbase(h, campaignName, factionName, airbase, uid, location);

      return uid;
    });
  }

  public void placeUnit(
    String campaignName, String factionName, Airbases airbase,
    UUID uid, Location location) {

    jdbi.useHandle(h -> {

      UUID cfid = campaignService.getCampaignFactionID(campaignName, factionName, airbase)
        .orElseThrow(() -> new NotFoundException("Faction is not registered to this campaign."));

      var abloc = airbase.location();
      var radiusft = h.select("select "
        + "cf.zone_size_ft "
        + "from campaign_faction cf "
        + "where cf.campaign_name = ? and cf.faction_name = ? "
        + "limit 1", campaignName, factionName)
        .mapTo(Integer.class)
        .first();
      var newloc = MathService.shrinkToCircle(abloc, radiusft, location);
      // Can move a unit only if under the control zone
      if (newloc.equals(location)) {
        h.execute("update campaign_faction_units "
          + "set x = ?, y = ?, z = ?, angle = ? "
          + "where id = ? and campaign_faction_id = ?",
          location.longitude(), location.latitude(), location.altitude(), location.angle(),
          uid, cfid);
      }
    });
  }

  private void enforceUnitRadiusFromAirbase(
    Handle h,
    String campaignName,
    String factionName,
    Airbases airbase,
    UUID uid,
    Location location) {

    // Control if the point is inside the airbase given the radius in ft
    var abloc = airbase.location();
    var radiusft = h.select("select "
        + "cf.zone_size_ft "
        + "from campaign_faction cf "
        + "where cf.campaign_name = ? and cf.faction_name = ? "
        + "limit 1", campaignName, factionName)
        .mapTo(Integer.class)
        .first();
    var uloc = location;

    var newloc = MathService.shrinkToCircle(abloc, radiusft, uloc);
    if (!newloc.equals(uloc)) {
      h.execute("update campaign_faction_units "
        + "set x = ?, y = ?, z = ?, angle = ? "
        + "where id = ?",
        newloc.longitude(), newloc.latitude(), newloc.altitude(), newloc.angle(),
        uid);
    }
  }

  public List<FactionUnit> getCoalitionUnitsForCampaign(String campaignName, CampaignCoalition coa) {
    return jdbi.withHandle(h ->
      h.select("select u.id, u.type, u.x, u.y, u.z, angle from campaign_faction_units u "
        + "left join campaign_faction cf on u.campaign_faction_id = cf.id "
        + "where cf.campaign_name = ? and cf.is_blue = ?"
        + "limit 10000", // 10k units per coalition max
        campaignName, coa == BLUE)
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
        .list()
    );
  }

}
