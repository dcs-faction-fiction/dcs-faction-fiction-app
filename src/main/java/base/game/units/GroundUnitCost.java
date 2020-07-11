package base.game.units;

public enum GroundUnitCost {

  AWACS        (GroundUnit.AWACS,           1, 3),
  TANKER       (GroundUnit.TANKER,          1, 1),

  // TANKS
  ABRAMS       (GroundUnit.ABRAMS,          2, 1),
  T_80         (GroundUnit.T_80,            2, 1),
  // IFV
  BRADLEY      (GroundUnit.BRADLEY,         2, 1),
  BMP_3        (GroundUnit.BMP_3,           2, 1),
  // AAA
  SHILKA       (GroundUnit.SHILKA,          2, 1),
  // Small IR SAMS
  SA_9         (GroundUnit.SA_9,            2, 1),
  SA_13        (GroundUnit.SA_13,           2, 1),
  // These SAMs can shoot down airborne missiles
  SA_15        (GroundUnit.SA_15,           1, 3),
  SA_19        (GroundUnit.SA_19,           1, 2),
  // For multiunit SAMS we use a fake unit and send as grouptype:<enum> in the name
  // and the script will spawn a multi unit group for that sam template
  SA_6         (GroundUnit.SA_6,            1, 2),
  SA_11        (GroundUnit.SA_11,           1, 2),
  SA_HAWK      (GroundUnit.SA_HAWK,         1, 2),
  SA_10        (GroundUnit.SA_10,           1, 4),
  SA_PATRIOT   (GroundUnit.SA_PATRIOT,      1, 4),
  ;

  private final GroundUnit unit;
  private final int amount;
  private final int cost;

  private GroundUnitCost(GroundUnit unit, int amount, int cost) {
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
