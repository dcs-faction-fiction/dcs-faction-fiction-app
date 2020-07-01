package base.api.campaignmanager.data;

import base.game.CampaignPhase;
import base.game.CampaignState;
import java.util.Map;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
@Value.Style(jdkOnly = true)
public interface CampaignSituation {
  CampaignState state();
  CampaignPhase phase();
  Map<String, CampaignSituationFaction> factions();
}
