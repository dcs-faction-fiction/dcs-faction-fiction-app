package base.api.campaignmanager.data;

import base.game.Airbases;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
public interface CampaignFactionPayload {
  String faction();
  Airbases airbase();
}
