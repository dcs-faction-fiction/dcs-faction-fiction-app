package base.api.auth;

import io.javalin.core.security.Role;

public enum Roles implements Role {
  ADMIN, DAEMON, CAMPAIGN_MANAGER, FACTION_MANAGER
}
