package base.game;

import static base.game.CampaignCoalition.BLUE;
import static base.game.CampaignCoalition.RED;
import static base.game.CampaignMap.CAUCASUS;
import java.util.Arrays;
import java.util.Optional;

public enum Airbases {

  ANAPA(12, "Anapa-Vityazevo", RED, CAUCASUS, Location.of("45.0045166", "37.3466833", "141")),
  KRYMSK(15, "Krymsk", RED, CAUCASUS, Location.of("44.96881666", "37.99353333", "66")),
  SOCHI(18, "Sochi-Adler", RED, CAUCASUS, Location.of("43.44441666", "39.94143333", "98")),
  MAYKOP(16, "Maykop-Khanskaya", RED, CAUCASUS, Location.of("44.67143", "40.18808", "180")),


  SENAKI(23, "Senaki-Kolkhi", BLUE, CAUCASUS, Location.of("42.240266666", "42.0479", "43")),
  KOBULETI(24, "Kobuleti", BLUE, CAUCASUS, Location.of("41.93015", "41.864233333", "59")),
  BATUMI(22, "Batumi", BLUE, CAUCASUS, Location.of("41.60975", "41.5999", "33")),
  KUTAISI(25, "Kutaisi", BLUE, CAUCASUS, Location.of("42.17915", "42.49568", "45")),
  ;

  private final int warehouseId;
  private final String airbaseName;
  private final CampaignCoalition coalition;
  private final CampaignMap map;
  private final Location location;

  private Airbases(
    int warehouseId,
    String airbaseName,
    CampaignCoalition coalition,
    CampaignMap map,
    Location location) {

    this.warehouseId = warehouseId;
    this.airbaseName = airbaseName;
    this.coalition = coalition;
    this.map = map;
    this.location = location;
  }

  public int warehouseId() {
    return warehouseId;
  }

  public Location location() {
    return location;
  }

  public CampaignCoalition coalition() {
    return coalition;
  }

  public CampaignMap map() {
    return map;
  }

  public static Optional<Airbases> fromName(String name) {
    return Arrays.asList(values()).stream()
      .filter(o -> o.airbaseName.equals(name))
      .findFirst();
  }

}
