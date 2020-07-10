package base.api.data;

import base.game.Airbases;
import base.game.units.GroundUnitCost;
import base.game.warehouse.WarehouseItemCode;

public class EnumsPayload {

  public static final EnumsPayload ENUMS = new EnumsPayload();

  public final Airbases[] airbases = Airbases.values();
  public final WarehouseItemCode[] warehouseItems = WarehouseItemCode.values();
  public final GroundUnitCost[] units = GroundUnitCost.values();

}
