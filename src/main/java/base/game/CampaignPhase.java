package base.game;

public enum CampaignPhase {
  // Commanders can view the intel file as a result of the previous mission.
  // This is the phase when a previous mission is ended or the campaign is
  // just started.
  DECLARING,
  // In this phase the commanders can purchase items and place them in warehouses,
  // actions, zone sizing, units and move or place units around their owned zones.
  PLACING,
  // Briefings are composed, flight plans created, payloads decided.
  BRIEFING,
  // Mission file has been craeted by the system, uploaded and it's ready to start
  // on the server.
  // No actions can be done on the web at this phase.
  // Map is frozen at the state of the mission, like being paused.
  READY,
  // Mission is currently running on the server.
  // No actions can be done on the web at this phase.
  // Some live telemetries available on the web interface with map.
  RUNNING
}
