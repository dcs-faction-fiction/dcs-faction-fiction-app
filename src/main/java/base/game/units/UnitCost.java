package base.game.units;

public enum UnitCost {

  AWACS        (Unit.AWACS,           1, 3),
  TANKER       (Unit.TANKER,          1, 1),

  // TANKS
  ABRAMS       (Unit.ABRAMS,          2, 1),
  T_80         (Unit.T_80,            2, 1),
  // IFV
  BRADLEY      (Unit.BRADLEY,         2, 1),
  BMP_3        (Unit.BMP_3,           2, 1),
  // AAA
  SHILKA       (Unit.SHILKA,          2, 1),
  // Small IR SAMS
  SA_9         (Unit.SA_9,            2, 1),
  SA_13        (Unit.SA_13,           2, 1),
  // These SAMs can shoot down airborne missiles
  SA_15        (Unit.SA_15,           1, 3),
  SA_19        (Unit.SA_19,           1, 2),
  // For multiunit SAMS we use a fake unit and send as grouptype:<enum> in the name
  // and the script will spawn a multi unit group for that sam template
  SA_6         (Unit.SA_6,            1, 2),
  SA_11        (Unit.SA_11,           1, 2),
  SA_HAWK      (Unit.SA_HAWK,         1, 2),
  SA_10        (Unit.SA_10,           1, 4),
  SA_PATRIOT   (Unit.SA_PATRIOT,      1, 4),
  ;

  private final Unit unit;
  private final int amount;
  private final int cost;

  private UnitCost(Unit unit, int amount, int cost) {
    this.unit = unit;
    this.amount = amount;
    this.cost = cost;
  }

  public int amount() {
    return amount;
  }

  public int cost() {
    return cost;
  }

}
