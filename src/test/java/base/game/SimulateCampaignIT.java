package base.game;

import base.Main;
import static base.api.EndpointCallerMethods.SAME_USER;
import static base.api.EndpointCallerMethods.delete;
import static base.api.EndpointCallerMethods.get;
import static base.api.EndpointCallerMethods.post;
import static base.api.EndpointCallerMethods.put;
import base.api.campaignmanager.data.CampaignSituation;
import base.api.campaignmanager.data.ImmutableCampaignSituation;
import base.api.factionmanager.data.FactionCampaignComboPayload;
import base.api.factionmanager.data.ImmutableInventoryWarehousePayload;
import base.api.factionmanager.data.ImmutableInventoryWarehousePayloadItem;
import base.api.factionmanager.data.InventoryWarehousePayload;
import static base.game.GameValues.DECREASE_ZONE_GAIN;
import static base.game.GameValues.INCREASE_ZONE_COST;
import static base.game.SimulateCampaignIT.client;
import static base.game.SimulateCampaignIT.gson;
import base.game.units.GroundUnit;
import static base.game.units.GroundUnit.BRADLEY;
import base.game.warehouse.WarehouseItemCode;
import static base.game.warehouse.WarehouseItemCode.AIM_120_C;
import static base.game.warehouse.WarehouseItemCode.AIM_9_X;
import static base.game.warehouse.WarehouseItemCode.UH_1H;
import base.service.data.FactionUnit;
import base.service.data.ImmutableFactionUnit;
import com.github.apilab.core.GSONModule;
import com.github.apilab.rest.exceptions.ServerException;
import com.google.gson.Gson;
import io.prometheus.client.CollectorRegistry;
import java.math.BigDecimal;
import static java.math.BigDecimal.ZERO;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static java.util.stream.Collectors.toSet;
import okhttp3.OkHttpClient;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SimulateCampaignIT {

  static OkHttpClient client = new OkHttpClient();
  static Main main = new Main();
  static Gson gson = new GSONModule().gson();
  static Session session = Session.mockRandomSession();

  @BeforeAll
  public static void prepare() throws Exception {
    main.startInstance();
  }

  @AfterAll
  public static void shutdown() {
    main.stopInstance();
  }

  @Test
  public void simulate() {
    var test = new TestHandler();
    test.flow1();
  }
}

// Helpers for testing user complex interaction
// All actions are also asserted inside

class TestHandler {
  Session session = Session.mockRandomSession();
  AdminActions admin = new AdminActions(session);
  CommonActions common = new CommonActions(session);
  FactionActions faction = new FactionActions(session);
  CampaignActions campaign = new CampaignActions(session);
  DaemonActions daemon = new DaemonActions(session);

  void setupNewCampaignWithFactions() {
    common.triggerCommonActions();
    faction.createFactions();
    campaign.createCampaign();
    campaign.registerFactionsToCampaign();
    campaign.giveCredits(session.blueFaction, 30);
    campaign.giveCredits(session.redFaction, 30);
    admin.serverAssignment();

    assertThrows(ServerException.class, () -> {
      faction.getServerInfo();
    });
    assertThrows(ServerException.class, () -> {
      campaign.getServerInfo();
    });
  }

  void flow1() {

    setupNewCampaignWithFactions();

    var loc10k = faction.generateLocationFromBaseDistance(session.blueFaction, 10_000);
    var loc60k = faction.generateLocationFromBaseDistance(session.blueFaction, 60_000);
    var loc12k = faction.generateLocationFromBaseDistance(session.blueFaction, 12_000);
    var loc15k = faction.generateLocationFromBaseDistance(session.blueFaction, 15_000);
    var loc200k = faction.generateLocationFromBaseDistance(session.blueFaction, 200_000);

    // Cannot decrease a zone at its minumum (new campaigns have it at minimum)
    assertThrows(ServerException.class, () -> {
      faction.decreaseZone(session.blueFaction);
    });

    // Zone increase at start of mission, credits should be enough
    // Factions start with 30 credits.
    faction.increaseZone(session.blueFaction);

    // Ensure the opposite, that given 0 credits (temporary situation in lambda)
    // cannot increase the zone.
    campaign.atTemporaryZeroCredits(session.blueFaction, () -> {
      assertThrows(ServerException.class, () -> {
        faction.increaseZone(session.blueFaction);
      });
    });

    // Cannot buy nothing with 0 credits
    campaign.atTemporaryZeroCredits(session.blueFaction, () -> {
      assertThrows(ServerException.class, () -> {
        faction.assertBoughtWarehouseItems(session.blueFaction, Map.of(UH_1H, 1), 2);
      });
    });
    campaign.atTemporaryZeroCredits(session.blueFaction, () -> {
      assertThrows(ServerException.class, () -> {
        faction.buyUnit(session.blueFaction, BRADLEY, loc10k, true);
      });
    });

    // Check estimation, 1 huey should cost 2c
    faction.assertBoughtWarehouseItems(session.blueFaction, Map.of(UH_1H, 1), 2);
    // Check some weapon packs
    faction.assertBoughtWarehouseItems(session.blueFaction, Map.of(AIM_120_C, 2, AIM_9_X, 2), 4);

    assertThat("Credits are spent", faction.checkCreditsSpent(session.blueFaction, () -> {
      var unit = faction.buyUnit(session.blueFaction, BRADLEY, loc10k, true);
      session.unitIds.put("bradleyid", unit.id().toString());

      unit = faction.buyUnit(session.blueFaction, BRADLEY, loc60k, true);
      session.unitIds.put("bradleyid2", unit.id().toString());

      unit = faction.buyUnit(session.blueFaction, BRADLEY, loc200k, false);
      session.unitIds.put("bradleyid3", unit.id().toString());
    }), is(3));
    var unit = faction.moveUnit(session.blueFaction, session.unitIds.get("bradleyid"), loc12k);
    assertThat("Moved unit", unit.location(), is(loc12k));
    unit = faction.moveUnit(session.blueFaction, session.unitIds.get("bradleyid"), loc200k);
    assertThat("Not moved unit", unit.location(), is(not(loc200k)));

    // Need now to decrease the zone, also to check a specific unit movement behavior
    // a unit cannot be moved by the user if it is outside the zone,
    // but first it had to be bought in the zone (previously bigger).
    faction.decreaseZone(session.blueFaction);
    unit = faction.moveUnit(session.blueFaction, session.unitIds.get("bradleyid2"), loc15k);
    assertThat("Not moved unit", unit.location(), is(not(loc60k)));

    campaign.checkDownloadedMission();

    var situation = campaign.getSituation();
    System.out.println(situation);

    daemon.simulateDaemon(() -> {
      faction.getServerInfo();
      campaign.getServerInfo();
    });

    var logs = faction.getLogs();
    assertThat(logs, is(not(emptyString())));
  }
}

class FactionActions {
  final Session session;

  FactionActions(Session session) {
    this.session = session;
  }

  void getServerInfo() {
    get(client, "/factionmanager-api/factions/"+session.blueFaction+"/campaigns/"+session.campaign+"/serverinfo", true, true, false);
  }

  void createFactions() {
    String resp;
    post(client, "/factionmanager-api/factions", "{\"name\":\""+session.blueFaction+"\"}", "application/json", true, true, false);
    resp = get(client, "/factionmanager-api/factions", true, true, false);
    assertThat("Has faction", resp, containsString(session.blueFaction));
    post(client, "/factionmanager-api/factions", "{\"name\":\""+session.redFaction+"\"}", "application/json", true, true, false);
    resp = get(client, "/factionmanager-api/factions", true, true, false);
    assertThat("Has faction", resp, containsString(session.redFaction));
  }

  String getLogs() {
    return get(client, "/factionmanager-api/factions/"+session.blueFaction+"/campaigns/"+session.campaign+"/flightlog", true, true, false);
  }

  void assertBoughtWarehouseItems(String faction, Map<WarehouseItemCode, Integer> amounts, int credits) {
    assertThat("Estimates are correct", estimateWarehouseAmountsCost(amounts), is(credits));
    assertThat("Credits are spent", checkCreditsSpent(faction, () -> {
      buyWarehouseItems(faction, amounts);
      assertThat("Items are in warehouse", isWarehouseContainingAtLeast(faction, amounts), is(true));
    }), is(credits));
  }

  void buyWarehouseItems(String faction, Map<WarehouseItemCode, Integer> amounts) {
    post(client, "/factionmanager-api/factions/"+faction+"/campaigns/"+session.campaign+"/warehouse", wareHouseItemsToJson(amounts), "application/json", true, true, false);
  }

  String wareHouseItemsToJson(Map<WarehouseItemCode, Integer> amounts) {
    var items = "";
    var c = "";
    for (var e: amounts.entrySet()) {
      var item = "{\"name\":\""+e.getKey().name()+"\", \"amount\":"+e.getValue()+"}";
      items += c + item;
      c = ",";
    }
    return "{\"items\": [ "+items+" ]}";
  }

  int estimateWarehouseAmountsCost(Map<WarehouseItemCode, Integer> amounts) {
    return Integer.valueOf(post(client,
      "/common-api/warehouseestimate",
      wareHouseItemsToJson(amounts),
      "application/json", true, true, false));
  }

  Map<WarehouseItemCode, Integer> getWarehouseAmounts(String faction) {
    var resp = get(client, "/factionmanager-api/factions/"+faction+"/campaigns/"+session.campaign+"/warehouse", true, true, false);
    var wh = gson.fromJson(resp, ImmutableInventoryWarehousePayload.class);
    Map<WarehouseItemCode, Integer> result = new EnumMap<>(WarehouseItemCode.class);
    for (var e: wh.items()) {
      var key = WarehouseItemCode.valueOf(e.name());
      var currentAmount = result.computeIfAbsent(key, k -> 0);
      result.put(key, currentAmount + e.amount());
    }
    return result;
  }

  boolean isWarehouseContainingAtLeast(String faction, Map<WarehouseItemCode, Integer> amounts) {
    var wh = getWarehouseAmounts(faction);
    for (var e: amounts.entrySet()) {
      var amount = wh.get(e.getKey());
      if (amount < e.getValue()) {
        return false;
      }
    }
    return true;
  }

  Location generateLocationFromBaseDistance(String faction, int distanceFt) {
    var resp = get(client, "/factionmanager-api/factions/"+faction+"/campaigns/"+session.campaign, true, true, false);
    var fc = gson.fromJson(resp, FactionCampaignComboPayload.class);
    var abLocation = fc.airbaseLocation();
    var meters = (double) distanceFt * (double) 0.3048;
    var degrees = meters / 111_111;
    var diff = new BigDecimal(degrees);
    return ImmutableLocation.builder()
      .latitude(abLocation.latitude().add(diff))
      .longitude(abLocation.longitude())
      .altitude(ZERO)
      .angle(ZERO)
      .build();
  }

  FactionUnit buyUnit(String faction, GroundUnit unit, Location location, boolean checkLocation) {
    var resp = get(client, "/factionmanager-api/factions/"+faction+"/campaigns/"+session.campaign+"/units", true, true, false);
    var originalUnitIds = Arrays.asList(gson.fromJson(resp, ImmutableFactionUnit[].class))
      .stream().map(ImmutableFactionUnit::id).collect(toSet());

    post(client, "/factionmanager-api/factions/"+faction+"/campaigns/"+session.campaign+"/units", unitToJson(unit, location), "application/json", true, true, false);

    resp = get(client, "/factionmanager-api/factions/"+faction+"/campaigns/"+session.campaign+"/units", true, true, false);
    var units = Arrays.asList(gson.fromJson(resp, ImmutableFactionUnit[].class));
    var newUnit = units.stream().filter(e -> !originalUnitIds.contains(e.id())).findFirst().get();

    assertThat("Unit type", newUnit.type(), is(unit));
    if (checkLocation) {
      assertThat("Unit is positioned", newUnit.location(), is(ImmutableLocation.builder()
        .latitude(location.latitude())
        .longitude(location.longitude())
        .altitude(location.altitude())
        .angle(location.angle())
        .build()));
    }

    return newUnit;
  }

  FactionUnit moveUnit(String faction, String id, Location location) {
    put(client, "/factionmanager-api/factions/"+faction+"/campaigns/"+session.campaign+"/units/"+id, locationToJson(location), "application/json", true, true, false);
    var resp = get(client, "/factionmanager-api/factions/"+faction+"/campaigns/"+session.campaign+"/units", true, true, false);
    var units = Arrays.asList(gson.fromJson(resp, ImmutableFactionUnit[].class));
    var newUnit = units.stream().filter(e -> e.id().toString().equals(id)).findFirst().get();
    return newUnit;
  }

  String unitToJson(GroundUnit unit, Location location) {
    return "{\"type\":\""+unit.name()+"\", \"location\":"+locationToJson(location)+"}";
  }

  String locationToJson(Location location) {
    return "{"
        + "\"latitude\":"+location.latitude().toString()+","
        + "\"longitude\":"+location.longitude().toString()+","
        + "\"altitude\":"+location.altitude().toString()+","
        + "\"angle\":"+location.angle().toString()+""
        + "}";
  }

  int checkCreditsSpent(String faction, Runnable fn) {
    var initialCredits = getCredits(faction);
    fn.run();
    return initialCredits - getCredits(faction);
  }

  int getCredits(String faction) {
    return Integer.valueOf(get(client,
      "/factionmanager-api/factions/"+faction+"/campaigns/"+session.campaign+"/credits",
      true, true, false));
  }

  int getBlueCredits() {
    return getCredits(session.blueFaction);
  }

  void increaseZone(String faction) {
    var credits = getCredits(faction);
    post(client, "/factionmanager-api/factions/"+faction+"/campaigns/"+session.campaign+"/zone", "{}", "application/json", true, true, false);
    credits = credits - getCredits(faction);
    assertThat("I spent "+INCREASE_ZONE_COST+" credits", credits, is(INCREASE_ZONE_COST));
  }

  void decreaseZone(String faction) {
    var credits = getCredits(faction);
    delete(client, "/factionmanager-api/factions/"+faction+"/campaigns/"+session.campaign+"/zone", true, true, false);
    credits = getCredits(faction) - credits;
    assertThat("I gained "+DECREASE_ZONE_GAIN+" credits", credits, is(DECREASE_ZONE_GAIN));
  }

}

class DaemonActions {
  final Session session;

  DaemonActions(Session session) {
    this.session = session;
  }

  void simulateDaemon(Runnable fn) {
    var bradleyid = session.unitIds.get("bradleyid");
    var bradleyid2 = session.unitIds.get("bradleyid2");
    // Without a user matching server, this should not be allowed
    assertThrows(ServerException.class, () -> {
      get(client, "/daemon-api/"+session.server+"/missions", true, true, true);
    });
    String phase, state;
    phase = get(client, "/campaignmanager-api/campaigns/"+session.campaign+"/phase", true, true, false);
    state = get(client, "/campaignmanager-api/campaigns/"+session.campaign+"/state", true, true, false);
    String action;
    //Try another server, server1 will throw exception
    assertThrows(ServerException.class, () -> {
      put(client,"/campaignmanager-api/campaigns/"+session.campaign+"/servers/server1", "{}", "application/json", true, true, false);
    });
    // Now with the server that has permissions
    put(client,"/campaignmanager-api/campaigns/"+session.campaign+"/servers/"+session.server, "{}", "application/json", true, true, false);
    action = get(client,
      "/daemon-api/"+session.server+"/actions",
      true, true, false);
    assertThat("Next action is to start", action, is("\"START_NEW_MISSION\""));
    put(client,
      "/daemon-api/"+session.server+"/actions/MISSION_STARTED",
      "{\"address\":\"localhost\",\"port\":10308,\"password\":\"dcs\"}", "application/json",
      true, true, false);
    action = get(client,
      "/daemon-api/"+session.server+"/actions",
      true, true, false);
    assertThat("Next action is started", action, is("\"MISSION_STARTED\""));
    var mission = get(client,
      "/daemon-api/"+session.server+"/missions",
      true, true, false);
    assertThat("Miz not empty", mission, is(not(emptyString())));

    // Call the function while running, to test some UI api endpoints
    // ex. fetching ip/password
    fn.run();

    // Mission is flew, data is sent now over
    post(client,
      "/daemon-api/"+session.server+"/warehouses",
      "{\"mission\":\"mission\","
        + "\"data\": [ {\"airbase\": \"Kutaisi\", \"type\":\"AIM_120C\", \"amount\":2},"
        + "{\"airbase\": \"Kutaisi\", \"type\":\"AIM_9X\", \"amount\":2} ]}",
      "application/json", true, true, false);
    action = get(client,
      "/daemon-api/"+session.server+"/actions",
      true, true, false);
    assertThat("Next action is to stop", action, is("\"STOP_MISSION\""));
    var resp = get(client,
      "/factionmanager-api/factions/"+session.blueFaction+"/campaigns/"+session.campaign+"/warehouse",
      true, true, false);
    var wh = gson.fromJson(resp, InventoryWarehousePayload.class);
    var a120c = ImmutableInventoryWarehousePayloadItem.builder()
      .name(AIM_120_C.name())
      .amount(2)
      .build();
    var a9x = ImmutableInventoryWarehousePayloadItem.builder()
      .name(AIM_9_X.name())
      .amount(2)
      .build();
    assertThat("I used all my aim-120-c", wh.items(), not(hasItem(a120c)));
    assertThat("I used all my aim-9-x", wh.items(), not(hasItem(a9x)));
    post(client,
      "/daemon-api/"+session.server+"/deadunits",
      "[\""+bradleyid+"\"]",
      "application/json", true, true, false);
    resp = get(client,
      "/factionmanager-api/factions/"+session.blueFaction+"/campaigns/"+session.campaign+"/units",
      true, true, false);
    var units = Arrays.asList(gson.fromJson(resp, ImmutableFactionUnit[].class));
    var dead = units.stream().filter(e -> e.id().toString().equals(bradleyid)).findFirst().isEmpty();
    assertThat("Bradley is dead",dead, is(true));
    post(client,
      "/daemon-api/"+session.server+"/movedunits",
      "[{\"id\": \""+bradleyid2+"\", \"location\":{"
        + "\"latitude\":5,"
        + "\"longitude\":6,"
        + "\"altitude\":3.1,"
        + "\"angle\":0.1"
        + "}}]",
      "application/json", true, true, false);
    resp = get(client,
      "/factionmanager-api/factions/"+session.blueFaction+"/campaigns/"+session.campaign+"/units",
      true, true, false);
    units = Arrays.asList(gson.fromJson(resp, ImmutableFactionUnit[].class));
    var bradley2 = units.stream()
      .filter(e -> e.id().toString().equals(bradleyid2))
      .findFirst().get();
    assertThat("Bradley2 is moved", bradley2.location().latitude(), is(new BigDecimal("5")));
    assertThat("Bradley2 is moved", bradley2.location().longitude(), is(new BigDecimal("6")));
  }
}

// Intentionally mutable for testing the integration cases
class Session {
  String blueFaction;
  String redFaction;
  String campaign;
  String server;
  Map<String, String> unitIds;

  static Session mockRandomSession() {
    var s = new Session();
    s.blueFaction = UUID.randomUUID().toString();
    s.redFaction = UUID.randomUUID().toString();
    s.campaign = UUID.randomUUID().toString();
    s.server = UUID.randomUUID().toString();
    s.unitIds = new HashMap<>();
    return s;
  }
}

class AdminActions {
  final Session session;

  AdminActions(Session session) {
    this.session = session;
  }

  void serverAssignment() {
    post(client, "/admin-api/users/"+SAME_USER+"/servers/server1", "{}", "application/json", true, true, false);
    delete(client, "/admin-api/users/"+SAME_USER+"/servers/server1", true, true, false);
    // Idempotent, test twice, same result and no exceptions
    post(client, "/admin-api/users/"+SAME_USER+"/servers/"+session.server, "{}", "application/json", true, true, false);
    post(client, "/admin-api/users/"+SAME_USER+"/servers/"+session.server, "{}", "application/json", true, true, false);
  }
}

class CommonActions {
  final Session session;

  CommonActions(Session session) {
    this.session = session;
  }

  void triggerCommonActions() {
    get (client, "/common-api/enums", true, true, false);
    post(client, "/common-api/newjwt", "{\"url\":\"\", \"email\":\"random@at\"}", "application/json", true, true, false);
  }
}

class CampaignActions {
  final Session session;

  CampaignActions(Session session) {
    this.session = session;
  }

  void getServerInfo() {
    get(client, "/campaignmanager-api/campaigns/"+session.campaign+"/serverinfo", true, true, false);
  }

  void createCampaign() {
    post(client, "/campaignmanager-api/campaigns", "{\"name\":\""+session.campaign+"\"}", "application/json", true, true, false);
    var resp = get(client, "/campaignmanager-api/campaigns", true, true, false);
    assertThat("Campaign exists", resp, containsString(session.campaign));
  }

  void checkDownloadedMission() {
    var mission = get(client, "/campaignmanager-api/campaigns/"+session.campaign+"/download-mission", true, true, false);
    assertThat("Miz not empty", mission, is(not(emptyString())));
  }

  void registerFactionsToCampaign() {
    String resp;

    // Association not created yet, expect to not be found
    assertThrows(ServerException.class, () -> {
      get(client, "/factionmanager-api/factions/"+session.blueFaction+"/campaigns/"+session.campaign+"/units", true, true, false);
    });

    post(client, "/campaignmanager-api/campaigns/"+session.campaign+"/factions", "{\"faction\":\""+session.blueFaction+"\",\"airbase\":\"KUTAISI\"}", "application/json", true, true, false);
    resp = get(client, "/factionmanager-api/factions/"+session.blueFaction+"/campaigns", true, true, false);
    assertThat("Has faction registered", resp, containsString(session.campaign));
    resp = get(client, "/factionmanager-api/factions/"+session.blueFaction+"/campaigns/"+session.campaign, true, true, false);
    assertThat("Faction sees the campaign combo", resp, containsString(session.blueFaction));

    post(client, "/campaignmanager-api/campaigns/"+session.campaign+"/factions", "{\"faction\":\""+session.redFaction+"\",\"airbase\":\"MAYKOP\"}", "application/json", true, true, false);
    resp = get(client, "/factionmanager-api/factions/"+session.redFaction+"/campaigns", true, true, false);
    assertThat("Has faction registered", resp, containsString(session.campaign));

    resp = get(client, "/campaignmanager-api/campaigns/"+session.campaign+"/factions", true, true, false);
    assertThat("Campaign has faction 1", resp, containsString(session.blueFaction));
    assertThat("Campaign has faction 2", resp, containsString(session.redFaction));
  }

  // Runs with the faction at 0 credits to make some edge case tests but also brings
  // immediately afterwards the previous credits to the faction
  // So that the runnable is being ran in 0 credits mode.
  void atTemporaryZeroCredits(String faction, Runnable fn) {
    var initialCredits = getCredits(faction);
    giveCredits(faction, -initialCredits);
    fn.run();
    giveCredits(faction, initialCredits);
  }

  // Returns the total after being given
  int giveCredits(String faction, int toGive) {
    var credits = getCredits(faction);
    post(client,
      "/campaignmanager-api/campaigns/"+session.campaign+"/add-credits-to-faction/"+faction,
      String.valueOf(toGive),
      "application/json", true, true, false);
    credits = getCredits(faction) - credits;
    assertThat("Credits given", credits, is(toGive));
    return getCredits(faction);
  }

  // Gets credits for faction
  int getCredits(String faction) {
    return Integer.valueOf(get(client,
      "/factionmanager-api/factions/"+faction+"/campaigns/"+session.campaign+"/credits",
      true, true, false));
  }

  CampaignSituation getSituation() {
    var resp = get(client, "/campaignmanager-api/campaigns/"+session.campaign+"/situation", true, true, false);
    return gson.fromJson(resp, ImmutableCampaignSituation.class);
  }
}
