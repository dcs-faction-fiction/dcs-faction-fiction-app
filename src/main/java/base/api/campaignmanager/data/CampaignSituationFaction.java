package base.api.campaignmanager.data;

import base.api.factionmanager.data.FactionCampaignComboPayload;
import base.api.factionmanager.data.InventoryWarehousePayload;
import base.service.data.FactionUnit;
import java.util.List;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
@Value.Style(jdkOnly = true)
public interface CampaignSituationFaction {
  int credits();
  FactionCampaignComboPayload faction();
  List<FactionUnit> units();
  InventoryWarehousePayload warehouse();
}
