package base.api.factionmanager.data;

import java.util.List;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
@Value.Style(jdkOnly = true)
public interface InventoryWarehousePayload {
  List<InventoryWarehousePayloadItem> items();
}
