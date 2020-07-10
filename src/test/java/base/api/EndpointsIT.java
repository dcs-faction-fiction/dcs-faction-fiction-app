package base.api;

import base.Main;
import static base.api.EndpointCallerMethods.get;
import static base.api.EndpointCallerMethods.post;
import com.github.apilab.rest.exceptions.ServerException;
import java.io.IOException;
import okhttp3.OkHttpClient;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EndpointsIT {

  static OkHttpClient client;

  @BeforeAll
  static void prepare() throws Exception {
    client = new OkHttpClient();
    Main.main(new String[]{});
  }

  @AfterAll
  static void shutdown() {
    Main.stop();
  }

  @Test
  void testAll() throws IOException, JSONException {
    // Test the error case in which a user is not in the token but the endpoints requires it
    var ex = assertThrows(ServerException.class, () -> {
      post(client, "/campaignmanager-api/campaigns", "{}", "application/json", true, false, false);
    });
    assertThat("response result is the error message", ex.getMessage(), is("\"No user identified in this session, check your token.\""));

    // Test that the swagger docs is a VALID JSON
    // This is because there is a replacing hack to enable token authentication in the swagger ui
    // and it's very possible that json will become invalid if stuff changes without noticing
    var result = get(client, "/swagger-docs", false, false, false);
    assertThat("json is valid", isValidJSON(result), is(true));

    assertThrows(ServerException.class, () -> {
      post(client, "/campaignmanager-api/campaigns/undefined", "{}", "application/json", true, true, false);
    });
    assertThrows(ServerException.class, () -> {
      post(client, "/factionmanager-api/factions/undefined", "{}", "application/json", true, true, false);
    });
    assertThrows(ServerException.class, () -> {
      get(client, "/factionmanager-api/factions/undefined/campaigns/undefined/units", true, true, false);
    });
  }

  public static boolean isValidJSON(String json) {
    try {
      var object = new JSONObject(json);
      return object.length() > 1;
    } catch (JSONException ex) {
      return false;
    }
  }
}
