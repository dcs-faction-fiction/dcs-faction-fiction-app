package base.config;

import base.api.EstimateEndpoint;
import base.api.FetchEnumsEndpoint;
import base.api.NewJWTEndpoint;
import base.api.admin.AdminAssignServer;
import base.api.admin.AdminUnassignServer;
import base.api.auth.EndpointPermissionFilter;
import base.api.auth.Roles;
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
import base.service.data.ServerInfo;
import com.github.apilab.core.ApplicationLifecycleItem;
import com.github.apilab.core.Env;
import com.github.apilab.rest.Endpoint;
import com.github.apilab.rest.auth.AuthConfiguration;
import com.github.apilab.rest.auth.ImmutableAuthConfiguration;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import java.time.Clock;
import java.util.Set;
import javax.inject.Named;
import javax.inject.Singleton;

@dagger.Module
public class ComponentsModule {

  @Provides
  @Singleton
  @IntoSet
  public ApplicationLifecycleItem lifecycle(MyLifecycleItem s) {
    return s;
  }

  @Provides
  @Singleton
  public Clock clock() {
    return Clock.systemUTC();
  }

  @Provides
  @Singleton
  public Env env() {
    return new Env();
  }

  @Provides
  @Named("jdbiImmutables")
  public Set<Class<?>> immutableClasses() {
    return Set.of(ServerInfo.class);
  }

  @Provides
  @Singleton
  public AuthConfiguration initalizer() {
    return ImmutableAuthConfiguration.builder()
      .roleMapper(Roles::valueOf)
      .build();
  }

  //
  //   S H A R E D   E N D P O I N T S
  //

  @Provides
  @IntoSet
  public Endpoint estimateEndpoint(EstimateEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint fetchEnumsEndpoint(FetchEnumsEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint newJWTEndpoint(NewJWTEndpoint endpoint) {
    return endpoint;
  }

  //
  //   A D M I N   E N D P O I N T S
  //

  @Provides
  @IntoSet
  public Endpoint adminAssignServer(AdminAssignServer endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint adminUnassignServer(AdminUnassignServer endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint endpointPermissionFilter(EndpointPermissionFilter endpoint) {
    return endpoint;
  }

  //
  //   F A C T I O N   E N D P O I N T S
  //

  @Provides
  @IntoSet
  public Endpoint factionGetServerInfoEndpoint(FactionGetServerInfoEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint factionIncreaseZoneEndpoint(FactionIncreaseZoneEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint factionDecreaseZoneEndpoint(FactionDecreaseZoneEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint factionCampaignComboGetEndpoint(FactionCampaignComboGetEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint factionListCampaignsEndpoint(FactionListCampaignsEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint factionBuyUnitEndpoint(FactionBuyUnitEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint factionPlaceUnitEndpoint(FactionPlaceUnitEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint factionListEndpoint(FactionListEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint factionGetUnitsEndpoint(FactionGetUnitsEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint factionWarehouseBuyEndpoint(FactionWarehouseBuyEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint factionWarehouseGetEndpoint(FactionWarehouseGetEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint factionCreateEndpoint(FactionCreateEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint factionGetCreditsEndpoints(FactionGetCreditsEndpoints endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint factionViewLogEndpoint(FactionViewLogEndpoint endpoint) {
    return endpoint;
  }

  //
  //   C A M P A I G N   E N D P O I N T S
  //

  @Provides
  @IntoSet
  public Endpoint campaignListEndpoint(CampaignListEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint campaignGetServerInfoEndpoint(CampaignGetServerInfoEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint campaignAddCreditsEndpoint(CampaignAddCreditsEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint campaignCreateEndpoint(CampaignCreateEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint campaignStartServerEndpoint(CampaignStartServerEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint campaignRegisterFactionEndpoint(CampaignRegisterFactionEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint campaignListFactionsEndpoint(CampaignListFactionsEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint campaignDownloadCurrentMissionEndpoint(CampaignDownloadCurrentMissionEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint campaignGetStateEndpoint(CampaignGetStateEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint campaignGetPhaseEndpoint(CampaignGetPhaseEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint campaignGetSituationEndpoint(CampaignGetSituationEndpoint endpoint) {
    return endpoint;
  }

  //
  //   D A E M O N   E N D P O I N T S
  //

  @Provides
  @IntoSet
  public Endpoint daemonPostWarehousesEndpoint(DaemonPostWarehousesEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint daemonNextActionEndpoint(DaemonNextActionEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint daemonDownloadServerMissionEndpoint(DaemonDownloadServerMissionEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint daemonPostDestroyedUnitsEndpoint(DaemonPostDestroyedUnitsEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint daemonPostMovedUnitsEndpoint(DaemonPostMovedUnitsEndpoint endpoint) {
    return endpoint;
  }

  @Provides
  @IntoSet
  public Endpoint daemonPostStatusEndpoint(DaemonPostStatusEndpoint endpoint) {
    return endpoint;
  }

}
