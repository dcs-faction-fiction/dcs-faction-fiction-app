
package base.api.campaignmanager.data;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
public interface NewCampaignPayload {
  String name();
}
