
package base.api.factionmanager.data;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
public interface NewFactionPayload {
  String name();
}
