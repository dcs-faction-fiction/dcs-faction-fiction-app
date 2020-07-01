package base.api.daemonintegration.data;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
public interface WarehousesSpentItem {
  String airbase();
  String type();
  int amount();
}
