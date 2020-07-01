package base.service.data;

import base.game.Location;
import base.game.units.GroundUnit;
import java.util.UUID;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
@Value.Style(jdkOnly = true)
public interface FactionUnit {
  UUID id();
  GroundUnit type();
  Location location();
}
