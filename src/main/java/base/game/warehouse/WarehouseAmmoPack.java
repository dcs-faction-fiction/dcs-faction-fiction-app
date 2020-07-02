package base.game.warehouse;

public enum WarehouseAmmoPack {
  A_A_NORMAL(2, 10),
  A_A_PRO(4, 10),
  A_G_NORMAL(2, 100),
  A_G_PRO(4, 10),
  ;

  private final int cost;
  private final int points;

  private WarehouseAmmoPack(int cost, int points) {
    this.cost = cost;
    this.points = points;
  }

  public int cost() {
    return cost;
  }

  public int points() {
    return points;
  }
}
