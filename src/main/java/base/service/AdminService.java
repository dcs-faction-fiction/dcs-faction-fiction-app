package base.service;

import java.util.UUID;
import javax.inject.Inject;
import org.jdbi.v3.core.Jdbi;

public class AdminService {

  @Inject Jdbi jdbi;

  @Inject
  public AdminService() {
    ///
  }

  public void assignServerToUser(UUID user, String server) {
    jdbi.useHandle(h -> {
      boolean exists = h.select("select server_name from user_server where user_id = ? and server_name = ?",
        user, server)
        .mapTo(String.class)
        .findFirst()
        .isPresent();
      if (!exists) {
        h.execute("insert into user_server (user_id, server_name) values(?, ?)", user, server);
      }
    });
  }

  public void unassignServerFromUser(UUID user, String server) {
    jdbi.useHandle(h ->
      h.execute("delete from user_server where user_id = ? and server_name = ?", user, server)
    );
  }

}
