package base.service;

import base.game.Airbases;
import base.game.warehouse.WarehouseAmmoPack;
import base.game.warehouse.WarehouseAmmoPoints;
import static base.game.warehouse.WarehouseItemCategory.AMMO;
import base.game.warehouse.WarehouseItemCode;
import base.game.warehouse.WarehouseItemCost;
import com.github.apilab.rest.exceptions.NotFoundException;
import com.github.apilab.rest.exceptions.ServerException;
import static java.lang.String.format;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import static net.logstash.logback.argument.StructuredArguments.kv;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarehouseService {

  private static final Logger LOG = LoggerFactory.getLogger(WarehouseService.class);

  @Inject Jdbi jdbi;
  @Inject CampaignService campaignService;
  @Inject FactionService factionService;
  @Inject FlightLogService flightLog;

  @Inject
  public WarehouseService() {
    ////
  }

  public static int calculateCreditsForBuy(Map<WarehouseItemCode, Integer> items) {
    // Make a map of pack,points for each pack that is being bought
    // So in the end will have a map like so:
    //   A_A_NORMAL: 50pts
    //   A_G_NORMAL: 15pts
    // And later will divide them and calculate credits from that.
    Map<WarehouseAmmoPack, Integer> ammoPacksPoints = new EnumMap<>(WarehouseAmmoPack.class);
    items.entrySet().stream()
      .filter(e -> e.getKey().category() == AMMO)
      .forEach(e -> {
        var owap = WarehouseAmmoPoints.fromAmmo(e.getKey());
        // Map ammo points if it's ammo.
        owap.ifPresent(wap -> {
          var points = ammoPacksPoints.computeIfAbsent(wap.pack(), i -> 0);
          points += wap.points() * e.getValue();
          ammoPacksPoints.put(wap.pack(), points);
        });
      });
    // Now for each make division and add up the costs
    var ammoPackCredits = ammoPacksPoints.entrySet().stream()
      .mapToInt(e ->
        // total points for the pack, divided how much points the pack provides + 1 if mod greater
        // equals the amount of packs that are needed (with excess)
        // and multiplied by the cost of each pack
        (e.getValue() / e.getKey().points() + Math.min(1, e.getValue() % e.getKey().points()))
          * e.getKey().cost()
      )
      .sum();

    // now sum all the costs for the items bought
    var itemsCredits = items.entrySet().stream()
      .filter(e -> e.getKey().category() != AMMO)
      .mapToInt(e -> WarehouseItemCost.fromCategory(e.getKey().category())
        // value is the amount, divide it by the amount for the cost, and multiply to the cost
        // then sum all together
        .map(wic ->
          (e.getValue() / wic.amount() + Math.min(1, e.getValue() % wic.amount()))
            * wic.cost())
        .orElse(0))
      .sum();

    return ammoPackCredits + itemsCredits;
  }

  public void buyWarehouseItems(
      String campaignName, String factionName, Airbases airbase,
      Map<WarehouseItemCode, Integer> items) {

    var credits = calculateCreditsForBuy(items);
    LOG.info("{} is spending in {} for {}, using {} to get {}",
      kv("faction", factionName),
      kv("campaign", campaignName),
      kv("airbase", airbase),
      kv("credits", credits),
      kv("items", items));

    jdbi.useHandle(h -> {

      UUID id = campaignService.getCampaignFactionID(campaignName, factionName, airbase)
        .orElseThrow(() -> new NotFoundException("Faction is not registered to this campaign."));

      var creditsAvailable = h.select(
        "select credits from campaign_faction where id = ? for update", id)
        .mapTo(Integer.class).findFirst().orElse(0);
      if (creditsAvailable < credits) {
        throw new ServerException(422, "Not enough credits");
      }

      var whid = ensureWarehouseExists(h, campaignName, airbase);

      items.entrySet().forEach(e -> {
        var itemid = ensureWarehouseItemExists(h, whid, e.getKey());
        h.execute(
          "update campaign_airfield_warehouse_item set item_quantity = item_quantity + ? where id = ?",
          e.getValue(), itemid);

        flightLog.logForCampaignFaction(id, format("Bought %s %s for faction %s in campaign %s", e.getValue(), e.getKey().name(), factionName, campaignName), h);
      });

      h.execute("update campaign_faction set credits = greatest(0, credits - ?) where id = ?",
        credits, id);

    });
  }

  public Map<WarehouseItemCode, Integer> getWarehouseForFaction(String campaignName, String factionName, Airbases airbase) {

    Map<WarehouseItemCode, Integer> result = new EnumMap<>(WarehouseItemCode.class);
    jdbi.useHandle(h ->
      h.select("select"
        + " i.item_code code,"
        + " i.item_quantity qty"
        + " from campaign_airfield_warehouse_item i"
        + " left join campaign_airfield_warehouse w on i.warehouse_id = w.id"
        + " left join campaign_faction cf on w.campaign_name = cf.campaign_name and w.airbase = cf.airbase"
        + " where w.campaign_name = ? and cf.faction_name = ? and cf.airbase = ?",
        campaignName, factionName, airbase)
        .mapToMap()
        .forEach(m -> {
          var code = WarehouseItemCode.valueOf((String) m.get("code"));
          var qty = (Integer) m.get("qty");
          result.put(code, qty);
        })
    );
    return result;
  }

  public Map<Airbases, Map<WarehouseItemCode, Integer>> getWarehouses(String campaignName) {
    Map<Airbases, Map<WarehouseItemCode, Integer>> result = new EnumMap<>(Airbases.class);
    jdbi.useHandle(h ->
      h.select("select"
        + " w.airbase airbase,"
        + " i.item_code code,"
        + " i.item_quantity qty"
        + " from campaign_airfield_warehouse_item i"
        + " left join campaign_airfield_warehouse w on i.warehouse_id = w.id"
        + " where w.campaign_name = ?",
        campaignName)
        .mapToMap()
        .forEach(m -> {
          var airbase = Airbases.valueOf((String) m.get("airbase"));
          var code = WarehouseItemCode.valueOf((String) m.get("code"));
          var qty = (Integer) m.get("qty");
          result.computeIfAbsent(airbase, a -> new EnumMap<>(WarehouseItemCode.class))
            .put(code, qty);
        })
    );
    return result;
  }

  public void updateWarehouses(
    String campaignName,
    Map<Airbases, Map<WarehouseItemCode, Integer>> warehouseDelta) {

    // Use one handle = one transaction
    jdbi.useHandle(h ->
      warehouseDelta.entrySet().forEach(e -> {
        var airbase = e.getKey();
        var baseInventory = e.getValue();
        var warehouseUUID = getWarehouseId(h, campaignName, airbase);
        baseInventory.entrySet().forEach(inventoryItem -> {
          var itemCode = inventoryItem.getKey();
          var amount = Optional.ofNullable(inventoryItem.getValue()).orElse(0);
          var itemUUID = ensureWarehouseItemExists(h, warehouseUUID, itemCode);
          LOG.debug("Adding value for {} = {}", itemCode, amount);
          h.execute(
            "update campaign_airfield_warehouse_item"
              + " set item_quantity = greatest(item_quantity + ?, 0)"
              + " where id = ?",
            amount, itemUUID);

          flightLog.logForWarehouse(warehouseUUID, format("Consumed %s %s for airbase %s in campaign %s", -amount, itemCode.name(), airbase.name(), campaignName), h);
        });
      })
    );
  }

  public UUID getWarehouseId(Handle h, String campaignName, Airbases airbase) {
    return ensureWarehouseExists(h, campaignName, airbase);
  }

  private UUID ensureWarehouseExists(Handle h, String campaignName, Airbases airbase) {
    var uuid = h.select("select id from campaign_airfield_warehouse"
      + " where campaign_name = ? and airbase = ?", campaignName, airbase.name())
      .mapTo(UUID.class)
      .findFirst();
    return uuid.orElseGet(() -> {
      var generatedUUID = UUID.randomUUID();
      LOG.debug("Creating campaign_airfield_warehouse {}", generatedUUID);
      h.execute("insert into campaign_airfield_warehouse (id, campaign_name, airbase) values(?, ?, ?)",
        generatedUUID, campaignName, airbase.name());
      return generatedUUID;
    });
  }

  private UUID ensureWarehouseItemExists(Handle h, UUID whid, WarehouseItemCode itemCode) {
    var uuid = h.select("select id from campaign_airfield_warehouse_item"
      + " where warehouse_id = ? and item_code = ?", whid, itemCode.name())
      .mapTo(UUID.class)
      .findFirst();
    return uuid.orElseGet(() -> {
      var generatedUUID = UUID.randomUUID();
      LOG.debug("Creating campaign_airfield_warehouse_item {}", generatedUUID);
      h.execute("insert into campaign_airfield_warehouse_item (id, warehouse_id, item_code, item_quantity) values(?, ?, ?, 0)",
        generatedUUID, whid, itemCode.name());
      return generatedUUID;
    });
  }
}
