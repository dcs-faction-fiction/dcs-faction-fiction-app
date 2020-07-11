package base.config;

import static com.github.apilab.rest.auth.JavalinJWTFilter.REQ_ATTR_ROLES;
import static com.github.apilab.rest.auth.JavalinJWTFilter.REQ_ATTR_SUBJECT;
import com.github.apilab.rest.exceptions.NotAuthenticatedException;
import io.javalin.http.Context;
import static java.util.Optional.ofNullable;
import java.util.UUID;

public final class EndpointLoginFetcher {

  private EndpointLoginFetcher() {
  }

  public static UUID requireLoggedInUserUUID(Context ctx) {
    return ofNullable((String) ctx.attribute(REQ_ATTR_SUBJECT))
      .map(UUID::fromString)
      .orElseThrow(() -> new NotAuthenticatedException("No user identified in this session, check your token."));
  }

  public static String requireLoggedInUser(Context ctx) {
    return ofNullable((String) ctx.attribute(REQ_ATTR_SUBJECT))
      .orElseThrow(() -> new NotAuthenticatedException("No user identified in this session, check your token."));
  }
}
