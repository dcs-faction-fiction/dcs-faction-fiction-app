package base.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.apilab.rest.exceptions.ServerException;
import java.io.IOException;
import java.util.Optional;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import java.util.UUID;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Class used to make calls to the api itself during integration tests.
 *
 */
public final class EndpointCallerMethods {

  private static final String TEST_HOST = "localhost";
  private static final String TEST_SECRET = "test";
  private static final String[] ROLES = new String[]{"admin", "daemon", "campaign_manager", "faction_manager"};
  public static final String SAME_USER = UUID.randomUUID().toString();

  private EndpointCallerMethods() {
  }

  public static String get(
    OkHttpClient client,
    String url,
    boolean authenticated,
    boolean withRealUser,
    boolean withWrongUser) {

    return call(client, "get", url, empty(), empty(), authenticated, withRealUser, withWrongUser);
  }

  public static String delete(
    OkHttpClient client,
    String url,
    boolean authenticated,
    boolean withRealUser,
    boolean withWrongUser) {

    return call(client, "delete", url, empty(), empty(), authenticated, withRealUser, withWrongUser);
  }

  public static String put(
    OkHttpClient client,
    String url,
    String sendBody,
    String mediaType,
    boolean authenticated,
    boolean withRealUser,
    boolean withWrongUser) {

    return call(client, "put", url, of(sendBody), of(mediaType), authenticated, withRealUser, withWrongUser);
  }

  public static String post(
    OkHttpClient client,
    String url,
    String sendBody,
    String mediaType,
    boolean authenticated,
    boolean withRealUser,
    boolean withWrongUser) {

    return call(client, "post", url, of(sendBody), of(mediaType), authenticated, withRealUser, withWrongUser);
  }

  public static String call(
    OkHttpClient client,
    String method,
    String url,
    Optional<String> sendBody,
    Optional<String> mediaType,
    boolean authenticated,
    boolean withRealUser,
    boolean withWrongUser) {

    try {
      var user = SAME_USER;
      if (url.startsWith("/daemon-api/")) {
        user = url.replaceAll("/daemon-api/", "")
          .replaceAll("/.*", "")
          .trim();
      }
      if (withWrongUser) {
        user = UUID.randomUUID().toString();
      }
      var host = TEST_HOST;
      var port = ofNullable(System.getenv("JAVALIN_HTTP2_PORT"))
        .map(Integer::valueOf)
        .orElse(8080);
      var rootURL = "http://" + host + ":" + port;
      var alg = Algorithm.HMAC256(TEST_SECRET);
      var token = JWT.create()
        .withArrayClaim("roles", ROLES)
        .withSubject(withRealUser ? user : null)
        .sign(alg);
      var requestBody = RequestBody.create(
        MediaType.get(mediaType.orElse("application/json")),
        sendBody.orElse("")
      );
      var requestBuilder = new Request.Builder()
        .method(method, requestBody)
        .url(rootURL + url);
      if (authenticated) {
        requestBuilder = requestBuilder.header("Authorization", "Bearer " + token);
      }
      var resp = client.newCall(requestBuilder.build()).execute();
      var body = Optional.ofNullable(resp.body())
        .map(b -> {
          try {
            return b.string();
          } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
          }
        })
        .orElse("");
      if (resp.code() >= 500) {
        throw new ServerException(resp.code(), body);
      } else if (resp.code() >= 400) {
        throw new ServerException(resp.code(), body);
      } else {
        return body;
      }
    } catch (IOException ex) {
      throw new ServerException(ex.getMessage(), ex);
    }
  }

}
