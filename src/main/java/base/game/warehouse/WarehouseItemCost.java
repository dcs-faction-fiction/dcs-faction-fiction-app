package base.game.warehouse;

import java.util.Arrays;
import java.util.Optional;

public enum WarehouseItemCost {
  VEHICLE_PLANE(WarehouseItemCategory.PLANES, 1, 2),
  VEHICLE_HELICOPTER(WarehouseItemCategory.HELICOPTERS, 1, 2),
  FUEL(WarehouseItemCategory.FUEL, 25, 1),
  ;

  private final WarehouseItemCategory category;
  private final int amount;
  private final int cost;

  private WarehouseItemCost(WarehouseItemCategory category, int amount, int cost) {
    this.category = category;
    this.amount = amount;
    this.cost = cost;
  }

  public int amount() {
    return amount;
  }

  public int cost() {
    return cost;
  }

  public static Optional<WarehouseItemCost> fromCategory(WarehouseItemCategory category) {
    return Arrays.asList(values()).stream()
      .filter(o -> o.category == category)
      .findFirst();
  }

}
