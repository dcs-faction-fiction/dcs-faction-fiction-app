package base.game;

import static base.game.Airbases.KUTAISI;
import static base.game.Airbases.MAYKOP;
import static base.game.CampaignCoalition.BLUE;
import static base.game.CampaignCoalition.RED;
import static base.game.CampaignMap.CAUCASUS;
import static base.game.warehouse.WarehouseItemCode.AIM_120_C;
import static base.game.warehouse.WarehouseItemCode.F_A_18_C;
import static base.game.warehouse.WarehouseItemCode.KA_50;
import static base.game.warehouse.WarehouseItemCode.VIKHR;
import base.service.CampaignService;
import base.service.UnitService;
import base.service.WarehouseService;
import com.github.apilab.rest.exceptions.ServerException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SampleMissionTest {

  @Test
  public void testSampleMission() throws IOException {
    var builder = new FullMissionBuilder();
    var unitService = mock(UnitService.class);
    var campaignService = mock(CampaignService.class);
    var warehouseService = mock(WarehouseService.class);
    builder.unitService = unitService;
    builder.campaignService = campaignService;
    builder.warehouseService = warehouseService;

    when(unitService.getCoalitionUnitsForCampaign("campaign1", RED))
      .thenReturn(List.of());
    when(unitService.getCoalitionUnitsForCampaign("campaign1", BLUE))
      .thenReturn(List.of());
    when(campaignService.getCampaignMap("campaign1"))
      .thenReturn(CAUCASUS);
    when(warehouseService.getWarehouses("campaign1"))
      .thenReturn(Map.of(
        KUTAISI, Map.of(
          F_A_18_C, 1,
          AIM_120_C, 2
        ),
        MAYKOP, Map.of(
          KA_50, 1,
          VIKHR, 2
        )
      ));

    var out = new ByteArrayOutputStream();
    builder.build("campaign1", out);

    assertThrows(ServerException.class, () -> {
      builder.build("campaign1", new OutputStream() {
        @Override
        public void write(int b) throws IOException {
          throw new IOException();
        }
      });
    });
  }

}
