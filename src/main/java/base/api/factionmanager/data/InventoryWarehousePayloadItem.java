package base.api.factionmanager.data;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
@Value.Style(jdkOnly = true)
public interface InventoryWarehousePayloadItem {
  String name();
  Integer amount();
}
