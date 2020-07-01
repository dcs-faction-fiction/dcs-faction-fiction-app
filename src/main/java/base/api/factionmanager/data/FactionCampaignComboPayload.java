package base.api.factionmanager.data;

import base.game.Airbases;
import base.game.Location;
import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.immutables.value.Value.Default;

@Value.Immutable
@Gson.TypeAdapters
@Value.Style(jdkOnly = true)
public interface FactionCampaignComboPayload {
  String factionName();
  String campaignName();
  Airbases airbase();
  @Default
  default Location airbaseLocation() { return airbase().location(); }
  int zoneSizeFt();
}
