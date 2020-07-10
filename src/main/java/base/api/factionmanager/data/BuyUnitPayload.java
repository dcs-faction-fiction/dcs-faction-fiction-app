package base.api.factionmanager.data;

import base.game.Location;
import base.game.units.GroundUnitCost;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
@Value.Style(jdkOnly = true)
public interface BuyUnitPayload {
  GroundUnitCost type();
  Location location();
}
