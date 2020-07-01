package base.game;

import static base.game.warehouse.WarehouseAmmoPack.A_A_PRO;
import static base.game.warehouse.WarehouseItemCode.AIM_120_C;
import static base.game.warehouse.WarehouseItemCode.F_A_18_C;
import static base.game.warehouse.WarehouseItemCode.JET_FUEL_TONS;
import static base.game.warehouse.WarehouseItemCost.FUEL;
import static base.game.warehouse.WarehouseItemCost.VEHICLE_PLANE;
import base.service.WarehouseService;
import java.util.Map;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;

public class CreditsBuyTest {

  @Test
  public void testAmmoAndCraftsSimple() {

    assertThat("Credits check", WarehouseService.calculateCreditsForBuy(Map.of(
      JET_FUEL_TONS, 10
    )), is(FUEL.cost() * 1));
    assertThat("Credits check", WarehouseService.calculateCreditsForBuy(Map.of(
      JET_FUEL_TONS, 25
    )), is(FUEL.cost() * 1));

    assertThat("Credits check", WarehouseService.calculateCreditsForBuy(Map.of(
      JET_FUEL_TONS, 26
    )), is(FUEL.cost() * 2));
    assertThat("Credits check", WarehouseService.calculateCreditsForBuy(Map.of(
      JET_FUEL_TONS, 50
    )), is(FUEL.cost() * 2));

    assertThat("Credits check", WarehouseService.calculateCreditsForBuy(Map.of(
      F_A_18_C, 1
    )), is(VEHICLE_PLANE.cost() * 1));
    assertThat("Credits check", WarehouseService.calculateCreditsForBuy(Map.of(
      F_A_18_C, 2
    )), is(VEHICLE_PLANE.cost() * 2));

    assertThat("Credits check", WarehouseService.calculateCreditsForBuy(Map.of(
      AIM_120_C, 5
    )), is(A_A_PRO.cost() * 1));
    assertThat("Credits check", WarehouseService.calculateCreditsForBuy(Map.of(
      AIM_120_C, 6
    )), is(A_A_PRO.cost() * 2));
    assertThat("Credits check", WarehouseService.calculateCreditsForBuy(Map.of(
      AIM_120_C, 9
    )), is(A_A_PRO.cost() * 2));
    assertThat("Credits check", WarehouseService.calculateCreditsForBuy(Map.of(
      AIM_120_C, 11
    )), is(A_A_PRO.cost() * 3));
  }

  @Test
  public void testAmmoAndCraftsSimpleComplex1() {
    assertThat("Credits check", WarehouseService.calculateCreditsForBuy(Map.of(
      JET_FUEL_TONS, 25,
      F_A_18_C, 2,
      AIM_120_C, 4
    )), is(9));
  }
}
