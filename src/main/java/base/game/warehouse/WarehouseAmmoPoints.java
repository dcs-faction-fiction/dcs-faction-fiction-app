package base.game.warehouse;

import static base.game.warehouse.WarehouseAmmoPack.A_A_NORMAL;
import static base.game.warehouse.WarehouseAmmoPack.A_A_PRO;
import static base.game.warehouse.WarehouseAmmoPack.A_G_NORMAL;
import static base.game.warehouse.WarehouseAmmoPack.A_G_PRO;
import java.util.Arrays;
import java.util.Optional;

public enum WarehouseAmmoPoints {

  // A/A

  AIM_120_B(A_A_PRO, WarehouseItemCode.AIM_120_B, 2),
  AIM_120_C(A_A_PRO, WarehouseItemCode.AIM_120_C, 2),
  R_77(A_A_PRO, WarehouseItemCode.R_77, 2),
  AIM_54_A_MK_47(A_A_PRO, WarehouseItemCode.AIM_54_A_MK_47, 3),
  AIM_54_A_MK_60(A_A_PRO, WarehouseItemCode.AIM_54_A_MK_60, 3),
  AIM_54_C_MK_47(A_A_PRO, WarehouseItemCode.AIM_54_C_MK_47, 3),

  AIM_9_X(A_A_NORMAL, WarehouseItemCode.AIM_9_X, 1),
  AIM_9_P5(A_A_NORMAL, WarehouseItemCode.AIM_9_P5, 1),
  AIM_9_M(A_A_NORMAL, WarehouseItemCode.AIM_9_M, 1),
  AIM_9_L(A_A_NORMAL, WarehouseItemCode.AIM_9_L, 1),

  AIM_7_MH(A_A_NORMAL, WarehouseItemCode.AIM_7_MH, 1),
  AIM_7_M(A_A_NORMAL, WarehouseItemCode.AIM_7_M, 1),
  AIM_7_F(A_A_NORMAL, WarehouseItemCode.AIM_7_F, 1),

  R_27_ET(A_A_NORMAL, WarehouseItemCode.R_27_ET, 2),
  R_27_ER(A_A_NORMAL, WarehouseItemCode.R_27_ER, 1),
  R_27_R(A_A_NORMAL, WarehouseItemCode.R_27_R, 1),
  R_27_T(A_A_NORMAL, WarehouseItemCode.R_27_T, 1),

  R_73(A_A_NORMAL, WarehouseItemCode.R_73, 1),
  R_60(A_A_NORMAL, WarehouseItemCode.R_60, 1),

  // A/G

  AGM_122(A_G_PRO, WarehouseItemCode.AGM_122, 2),
  AGM_88_C(A_G_PRO, WarehouseItemCode.AGM_88_C, 3),

  AGM_154_A(A_G_PRO, WarehouseItemCode.AGM_154_A, 4),
  AGM_154_C(A_G_PRO, WarehouseItemCode.AGM_154_C, 4),
  AGM_84_D(A_G_PRO, WarehouseItemCode.AGM_84_D, 4),
  AGM_84_E(A_G_PRO, WarehouseItemCode.AGM_84_E, 4),

  AGM_65_D(A_G_PRO, WarehouseItemCode.AGM_65_D, 2),
  AGM_65_E(A_G_PRO, WarehouseItemCode.AGM_65_E, 2),
  AGM_65_F(A_G_PRO, WarehouseItemCode.AGM_65_F, 2),
  AGM_65_G(A_G_PRO, WarehouseItemCode.AGM_65_G, 2),
  AGM_65_H(A_G_PRO, WarehouseItemCode.AGM_65_H, 2),
  AGM_65_K(A_G_PRO, WarehouseItemCode.AGM_65_K, 2),

  VIKHR(A_G_PRO, WarehouseItemCode.VIKHR, 2),
  KH_58(A_G_PRO, WarehouseItemCode.KH_58, 3),
  KH_25MPU(A_G_PRO, WarehouseItemCode.KH_25MPU, 3),
  KH_29L(A_G_PRO, WarehouseItemCode.KH_29L, 2),
  KH_29T(A_G_PRO, WarehouseItemCode.KH_29T, 2),

  GBU_10(A_G_PRO, WarehouseItemCode.GBU_10, 2),
  GBU_12(A_G_PRO, WarehouseItemCode.GBU_12, 2),
  GBU_16(A_G_PRO, WarehouseItemCode.GBU_16, 2),
  GBU_31(A_G_PRO, WarehouseItemCode.GBU_31, 2),
  GBU_31_V3B(A_G_PRO, WarehouseItemCode.GBU_31_V3B, 2),
  GBU_38(A_G_PRO, WarehouseItemCode.GBU_38, 2),
  AGM_62(A_G_PRO, WarehouseItemCode.AGM_62, 2),

  MK_81(A_G_NORMAL, WarehouseItemCode.MK_81, 10),
  MK_82(A_G_NORMAL, WarehouseItemCode.MK_82, 10),
  MK_83(A_G_NORMAL, WarehouseItemCode.MK_83, 10),
  MK_84(A_G_NORMAL, WarehouseItemCode.MK_84, 10),
  MK_82X(A_G_NORMAL, WarehouseItemCode.MK_82X, 10),
  MK_82Y(A_G_NORMAL, WarehouseItemCode.MK_82Y, 10),

  ZUNI(A_G_NORMAL, WarehouseItemCode.ZUNI, 10),
  M151(A_G_NORMAL, WarehouseItemCode.M151, 1),
  M5_HE(A_G_NORMAL, WarehouseItemCode.M5_HE, 1),

  ;

  private final WarehouseAmmoPack pack;
  private final WarehouseItemCode ammo;
  private final int points;

  private WarehouseAmmoPoints(WarehouseAmmoPack pack, WarehouseItemCode ammo, int points) {
    this.pack = pack;
    this.ammo = ammo;
    this.points = points;
  }

  public WarehouseAmmoPack pack() {
    return pack;
  }

  public int points() {
    return points;
  }

  public static Optional<WarehouseAmmoPoints> fromAmmo(WarehouseItemCode ammo) {
    return Arrays.asList(values()).stream()
      .filter(o -> o.ammo == ammo)
      .findFirst();
  }

}
