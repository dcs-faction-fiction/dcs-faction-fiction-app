package base.api;

import base.api.admin.AdminAssignServer;
import base.api.admin.AdminUnassignServer;
import base.api.auth.EndpointPermissionFilter;
import static base.api.auth.Roles.ADMIN;
import static base.api.auth.Roles.CAMPAIGN_MANAGER;
import static base.api.auth.Roles.DAEMON;
import static base.api.auth.Roles.FACTION_MANAGER;
import base.api.campaignmanager.CampaignAddCreditsEndpoint;
import base.api.campaignmanager.CampaignCreateEndpoint;
import base.api.campaignmanager.CampaignDownloadCurrentMissionEndpoint;
import base.api.campaignmanager.CampaignGetPhaseEndpoint;
import base.api.campaignmanager.CampaignGetServerInfoEndpoint;
import base.api.campaignmanager.CampaignGetSituationEndpoint;
import base.api.campaignmanager.CampaignGetStateEndpoint;
import base.api.campaignmanager.CampaignListEndpoint;
import base.api.campaignmanager.CampaignListFactionsEndpoint;
import base.api.campaignmanager.CampaignRegisterFactionEndpoint;
import base.api.campaignmanager.CampaignStartServerEndpoint;
import base.api.daemonintegration.DaemonDownloadServerMissionEndpoint;
import base.api.daemonintegration.DaemonNextActionEndpoint;
import base.api.daemonintegration.DaemonPostDestroyedUnitsEndpoint;
import base.api.daemonintegration.DaemonPostMovedUnitsEndpoint;
import base.api.daemonintegration.DaemonPostStatusEndpoint;
import base.api.daemonintegration.DaemonPostWarehousesEndpoint;
import base.api.factionmanager.FactionBuyUnitEndpoint;
import base.api.factionmanager.FactionCampaignComboGetEndpoint;
import base.api.factionmanager.FactionCreateEndpoint;
import base.api.factionmanager.FactionDecreaseZoneEndpoint;
import base.api.factionmanager.FactionGetCreditsEndpoints;
import base.api.factionmanager.FactionGetServerInfoEndpoint;
import base.api.factionmanager.FactionGetUnitsEndpoint;
import base.api.factionmanager.FactionIncreaseZoneEndpoint;
import base.api.factionmanager.FactionListCampaignsEndpoint;
import base.api.factionmanager.FactionListEndpoint;
import base.api.factionmanager.FactionPlaceUnitEndpoint;
import base.api.factionmanager.FactionViewLogEndpoint;
import base.api.factionmanager.FactionWarehouseBuyEndpoint;
import base.api.factionmanager.FactionWarehouseGetEndpoint;
import com.github.apilab.rest.Endpoint;
import io.javalin.Javalin;
import static io.javalin.core.security.SecurityUtil.roles;
import io.javalin.http.Context;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EndpointMappingTest {

  @Test
  public void testPermission() throws Exception {
    EndpointPermissionFilter ep;
    var javalin = mock(Javalin.class);

    ep = new EndpointPermissionFilter();
    ep.handle(mock(Context.class));
    ep.register(javalin);
    verify(javalin).before(eq("/factionmanager-api/factions/:faction/campaigns/:campaign"), any());
    verify(javalin).before(eq("/factionmanager-api/factions/:faction/campaigns/:campaign/*"), any());
    verify(javalin).before(eq("/factionmanager-api/factions/:faction"), any());
    verify(javalin).before(eq("/factionmanager-api/factions/:faction/*"), any());
    verify(javalin).before(eq("/campaignmanager-api/campaigns/:campaign"), any());
    verify(javalin).before(eq("/campaignmanager-api/campaigns/:campaign/*"), any());
  }

  @Test
  public void test() throws Exception {
    Endpoint ep;
    var javalin = mock(Javalin.class);

    // C O M M O N

    ep = new EstimateEndpoint();
    ep.register(javalin);
    verify(javalin).post("/common-api/warehouseestimate", ep, roles(FACTION_MANAGER, CAMPAIGN_MANAGER));

    ep = new FetchEnumsEndpoint();
    ep.register(javalin);
    verify(javalin).get("/common-api/enums", ep, roles(FACTION_MANAGER, CAMPAIGN_MANAGER));

    ep = new NewJWTEndpoint();
    ep.register(javalin);
    verify(javalin).post("/common-api/newjwt", ep);

    // A D M I N

    ep = new AdminAssignServer();
    ep.register(javalin);
    verify(javalin).post("/admin-api/users/:user/servers/:server", ep, roles(ADMIN));

    ep = new AdminUnassignServer();
    ep.register(javalin);
    verify(javalin).delete("/admin-api/users/:user/servers/:server", ep, roles(ADMIN));

    // F A C T I O N

    ep = new FactionListEndpoint();
    ep.register(javalin);
    verify(javalin).get("/factionmanager-api/factions", ep, Set.of(FACTION_MANAGER));

    ep = new FactionCreateEndpoint();
    ep.register(javalin);
    verify(javalin).post("/factionmanager-api/factions", ep, Set.of(FACTION_MANAGER));

    ep = new FactionListCampaignsEndpoint();
    ep.register(javalin);
    verify(javalin).get("/factionmanager-api/factions/:faction/campaigns", ep, Set.of(FACTION_MANAGER));

    ep = new FactionCampaignComboGetEndpoint();
    ep.register(javalin);
    verify(javalin).get("/factionmanager-api/factions/:faction/campaigns/:campaign", ep, Set.of(FACTION_MANAGER));

    ep = new FactionGetServerInfoEndpoint();
    ep.register(javalin);
    verify(javalin).get("/factionmanager-api/factions/:faction/campaigns/:campaign/serverinfo", ep, Set.of(FACTION_MANAGER));

    ep = new FactionIncreaseZoneEndpoint();
    ep.register(javalin);
    verify(javalin).post("/factionmanager-api/factions/:faction/campaigns/:campaign/zone", ep, Set.of(FACTION_MANAGER));

    ep = new FactionDecreaseZoneEndpoint();
    ep.register(javalin);
    verify(javalin).delete("/factionmanager-api/factions/:faction/campaigns/:campaign/zone", ep, Set.of(FACTION_MANAGER));

    ep = new FactionBuyUnitEndpoint();
    ep.register(javalin);
    verify(javalin).post("/factionmanager-api/factions/:faction/campaigns/:campaign/units", ep, Set.of(FACTION_MANAGER));

    ep = new FactionGetUnitsEndpoint();
    ep.register(javalin);
    verify(javalin).get("/factionmanager-api/factions/:faction/campaigns/:campaign/units", ep, Set.of(FACTION_MANAGER));

    ep = new FactionPlaceUnitEndpoint();
    ep.register(javalin);
    verify(javalin).put("/factionmanager-api/factions/:faction/campaigns/:campaign/units/:id", ep, Set.of(FACTION_MANAGER));

    ep = new FactionWarehouseBuyEndpoint();
    ep.register(javalin);
    verify(javalin).post("/factionmanager-api/factions/:faction/campaigns/:campaign/warehouse", ep, Set.of(FACTION_MANAGER));

    ep = new FactionWarehouseGetEndpoint();
    ep.register(javalin);
    verify(javalin).get("/factionmanager-api/factions/:faction/campaigns/:campaign/warehouse", ep, Set.of(FACTION_MANAGER));

    ep = new FactionGetCreditsEndpoints();
    ep.register(javalin);
    verify(javalin).get("/factionmanager-api/factions/:faction/campaigns/:campaign/credits", ep, Set.of(FACTION_MANAGER));

    ep = new FactionViewLogEndpoint();
    ep.register(javalin);
    verify(javalin).get("/factionmanager-api/factions/:faction/campaigns/:campaign/flightlog", ep, Set.of(FACTION_MANAGER));

    // C A M P A I G N

    ep = new CampaignListEndpoint();
    ep.register(javalin);
    verify(javalin).get("/campaignmanager-api/campaigns", ep, Set.of(CAMPAIGN_MANAGER));

    ep = new CampaignCreateEndpoint();
    ep.register(javalin);
    verify(javalin).post("/campaignmanager-api/campaigns", ep, Set.of(CAMPAIGN_MANAGER));

    ep = new CampaignRegisterFactionEndpoint();
    ep.register(javalin);
    verify(javalin).post("/campaignmanager-api/campaigns/:campaign/factions", ep, Set.of(CAMPAIGN_MANAGER));

    ep = new CampaignGetServerInfoEndpoint();
    ep.register(javalin);
    verify(javalin).get("/campaignmanager-api/campaigns/:campaign/serverinfo", ep, Set.of(CAMPAIGN_MANAGER));

    ep = new CampaignListFactionsEndpoint();
    ep.register(javalin);
    verify(javalin).get("/campaignmanager-api/campaigns/:campaign/factions", ep, Set.of(CAMPAIGN_MANAGER));

    ep = new CampaignDownloadCurrentMissionEndpoint();
    ep.register(javalin);
    verify(javalin).get("/campaignmanager-api/campaigns/:campaign/download-mission", ep, Set.of(CAMPAIGN_MANAGER));

    ep = new CampaignAddCreditsEndpoint();
    ep.register(javalin);
    verify(javalin).post("/campaignmanager-api/campaigns/:campaign/add-credits-to-faction/:faction", ep, Set.of(CAMPAIGN_MANAGER));

    ep = new CampaignStartServerEndpoint();
    ep.register(javalin);
    verify(javalin).put("/campaignmanager-api/campaigns/:campaign/servers/:server", ep, Set.of(CAMPAIGN_MANAGER));

    ep = new CampaignGetSituationEndpoint();
    ep.register(javalin);
    verify(javalin).get("/campaignmanager-api/campaigns/:campaign/situation", ep, Set.of(CAMPAIGN_MANAGER));

    // M I X E D

    ep = new CampaignGetStateEndpoint();
    ep.register(javalin);
    verify(javalin).get("/campaignmanager-api/campaigns/:campaign/state", ep, Set.of(CAMPAIGN_MANAGER));
    verify(javalin).get("/factionmanager-api/factions/:faction/campaigns/:campaign/state", ep, Set.of(FACTION_MANAGER));

    ep = new CampaignGetPhaseEndpoint();
    ep.register(javalin);
    verify(javalin).get("/campaignmanager-api/campaigns/:campaign/phase", ep, Set.of(CAMPAIGN_MANAGER));
    verify(javalin).get("/factionmanager-api/factions/:faction/campaigns/:campaign/phase", ep, Set.of(FACTION_MANAGER));

    // D A E M O N

    ep = new DaemonDownloadServerMissionEndpoint();
    ep.register(javalin);
    verify(javalin).get("/daemon-api/:serverid/missions", ep, Set.of(DAEMON));

    ep = new DaemonNextActionEndpoint();
    ep.register(javalin);
    verify(javalin).get("/daemon-api/:serverid/actions", ep, Set.of(DAEMON));

    ep = new DaemonPostWarehousesEndpoint();
    ep.register(javalin);
    verify(javalin).post("/daemon-api/:serverid/warehouses", ep, Set.of(DAEMON));

    ep = new DaemonPostDestroyedUnitsEndpoint();
    ep.register(javalin);
    verify(javalin).post("/daemon-api/:serverid/deadunits", ep, Set.of(DAEMON));

    ep = new DaemonPostMovedUnitsEndpoint();
    ep.register(javalin);
    verify(javalin).post("/daemon-api/:serverid/movedunits", ep, Set.of(DAEMON));

    ep = new DaemonPostStatusEndpoint();
    ep.register(javalin);
    verify(javalin).put("/daemon-api/:serverid/actions/:action", ep, Set.of(DAEMON));
  }

}
