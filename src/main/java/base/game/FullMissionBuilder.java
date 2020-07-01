package base.game;

import static base.game.CampaignCoalition.BLUE;
import static base.game.CampaignCoalition.RED;
import base.game.units.MissionBuilder;
import base.game.units.OptionsBuilder;
import base.game.warehouse.WarehouseBuilder;
import base.service.CampaignService;
import base.service.UnitService;
import base.service.WarehouseService;
import com.github.apilab.rest.exceptions.ServerException;
import java.io.IOException;
import java.io.OutputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.inject.Inject;

public class FullMissionBuilder {

  @Inject UnitService unitService;
  @Inject CampaignService campaignService;
  @Inject WarehouseService warehouseService;

  @Inject
  public FullMissionBuilder() {
    ////
  }

  /**
   * Build a final .miz file.
   * @param campaignName campaign name from which to generate the next mission
   * @param out the stream where to write the file
   */
  public void build(String campaignName, OutputStream out) {

    try {

      var map = campaignService.getCampaignMap(campaignName);
      var warehousesMap = warehouseService.getWarehouses(campaignName);
      var blueUnits = unitService.getCoalitionUnitsForCampaign(campaignName, BLUE);
      var redUnits = unitService.getCoalitionUnitsForCampaign(campaignName, RED);

      var mb = new MissionBuilder();

      var mission = mb.mission(map, warehousesMap, blueUnits, redUnits);
      var dictionary = mb.dict();
      var mapResources = mb.mapResource();
      var warehouses = WarehouseBuilder.build(warehousesMap);
      var options = OptionsBuilder.build();
      var theatre = map.dcsname();

      try (ZipOutputStream zipOut = new ZipOutputStream(out)) {
        ZipEntry zipEntry = new ZipEntry("mission");
        zipOut.putNextEntry(zipEntry);
        zipOut.write(mission.getBytes(UTF_8));
        zipEntry = new ZipEntry("l10n/DEFAULT/dictionary");
        zipOut.putNextEntry(zipEntry);
        zipOut.write(dictionary.getBytes(UTF_8));
        zipEntry = new ZipEntry("l10n/DEFAULT/mapResources");
        zipOut.putNextEntry(zipEntry);
        zipOut.write(mapResources.getBytes(UTF_8));

        zipEntry = new ZipEntry("warehouses");
        zipOut.putNextEntry(zipEntry);
        zipOut.write(warehouses.getBytes(UTF_8));

        zipEntry = new ZipEntry("options");
        zipOut.putNextEntry(zipEntry);
        zipOut.write(options.getBytes(UTF_8));

        zipEntry = new ZipEntry("theatre");
        zipOut.putNextEntry(zipEntry);
        zipOut.write(theatre.getBytes(UTF_8));
      }
    } catch (IOException ex) {
      throw new ServerException(ex.getMessage(), ex);
    }
  }
}
