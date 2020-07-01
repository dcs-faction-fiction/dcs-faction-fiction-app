package base.game;

import java.math.BigDecimal;
import static java.math.BigDecimal.ZERO;
import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.immutables.value.Value.Default;

@Value.Immutable
@Gson.TypeAdapters
public interface Location {
  BigDecimal latitude();
  BigDecimal longitude();
  @Default default BigDecimal altitude() {return ZERO;}
  @Default default BigDecimal angle() {return ZERO;}

  static ImmutableLocation of(String latitude, String longitude) {
    return of(new BigDecimal(latitude), new BigDecimal(longitude));
  }

  static ImmutableLocation of(String latitude, String longitude, String altitude) {
    return of(new BigDecimal(latitude), new BigDecimal(longitude), new BigDecimal(altitude));
  }

  static ImmutableLocation of(BigDecimal latitude, BigDecimal longitude) {
    return ImmutableLocation.builder()
      .latitude(latitude)
      .longitude(longitude)
      .build();
  }

  static ImmutableLocation of(BigDecimal latitude, BigDecimal longitude, BigDecimal altitude) {
    return ImmutableLocation.builder()
      .latitude(latitude)
      .longitude(longitude)
      .altitude(altitude)
      .build();
  }
}
