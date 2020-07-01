package base.game;

public enum CampaignState {
  // The campaign is just created and no factions have been finalized yet
  // Factions in this phase must chose: airbase, participants
  PREPARING,
  // Factions variables chosen above are now locked,
  // initial status is generated and the campaign can
  // actually start and be scheduled.
  RUNNING,
  // Campaign is closed, nothing can be done anymore but historical information remains.
  CONCLUDED
}
