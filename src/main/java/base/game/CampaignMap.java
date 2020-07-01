package base.game;

public enum CampaignMap {

  CAUCASUS("Caucasus", Location.of("45.12945", "34.26552")),
  ;

  private final String dcsname;
  private final Location zero;

  private CampaignMap(String dcsname, Location zero) {
    this.dcsname = dcsname;
    this.zero = zero;
  }

  public String dcsname() {
    return dcsname;
  }
}

