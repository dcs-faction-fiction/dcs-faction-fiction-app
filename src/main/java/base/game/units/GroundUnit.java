package base.game.units;

import static base.game.units.GroundUnitType.AAA;
import static base.game.units.GroundUnitType.IFV;
import static base.game.units.GroundUnitType.MBT;
import static base.game.units.GroundUnitType.SAM;

public enum GroundUnit {

  ABRAMS       (MBT, "M-1 Abrams",      2, 1),
  T_80         (MBT, "T-80UD",          2, 1),
  BRADLEY      (IFV, "M-2 Bradley",     2, 1),
  BMP_3        (IFV, "BMP-3",           2, 1),
  SHILKA       (AAA, "ZSU-23-4 Shilka", 2, 1),
  SA_9         (SAM, "Strela-1 9P31",   2, 1),
  SA_13        (SAM, "Strela-10M3",     2, 1),
  // For SAMS we use a fake unit and send as grouptype:<enum> in the name
  // and the script will spawn a multi unit group for that sam template
  SA_6         (SAM, "M 818",           1, 2),
  SA_11        (SAM, "M 818",           1, 2),
  SA_HAWK      (SAM, "M 818",           1, 2),
  SA_10        (SAM, "M 818",           1, 4),
  SA_PATRIOT   (SAM, "M 818",           1, 4),
  ;

  private final GroundUnitType type;
  private final String dcstype;
  private final int amount;
  private final int cost;

  private GroundUnit(GroundUnitType type, String dcstype, int amount, int cost) {
    this.type = type;
    this.dcstype = dcstype;
    this.amount = amount;
    this.cost = cost;
  }

  public String dcstype() {
    return dcstype;
  }

  public int amount() {
    return amount;
  }

  public int cost() {
    return cost;
  }

}
