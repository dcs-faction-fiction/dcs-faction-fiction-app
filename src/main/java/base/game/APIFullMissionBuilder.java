package base.game;

import static base.game.CampaignCoalition.BLUE;
import static base.game.CampaignCoalition.RED;
import base.service.CampaignService;
import base.service.UnitService;
import base.service.WarehouseService;
import java.io.OutputStream;
import javax.inject.Inject;

public class APIFullMissionBuilder {

  @Inject UnitService unitService;
  @Inject CampaignService campaignService;
  @Inject WarehouseService warehouseService;

  @Inject
  public APIFullMissionBuilder() {
    ////
  }

  /**
   * Build a final .miz file.
   * @param campaignName campaign name from which to generate the next mission
   * @param out the stream where to write the file
   */
  public void build(String campaignName, OutputStream out) {

    var map = campaignService.getCampaignMap(campaignName);
    var warehousesMap = warehouseService.getWarehouses(campaignName);
    var blueUnits = unitService.getCoalitionUnitsForCampaign(campaignName, BLUE);
    var redUnits = unitService.getCoalitionUnitsForCampaign(campaignName, RED);

    FullMissionBuilder mb = new FullMissionBuilder();
    mb.build(map, warehousesMap, blueUnits, redUnits, out);
  }
}
