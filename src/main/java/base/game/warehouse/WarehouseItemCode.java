package base.game.warehouse;

import static base.game.warehouse.WarehouseItemCategory.AMMO;
import static base.game.warehouse.WarehouseItemCategory.FUEL;
import static base.game.warehouse.WarehouseItemCategory.HELICOPTERS;
import static base.game.warehouse.WarehouseItemCategory.PLANES;
import java.util.Arrays;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Optional;

public enum WarehouseItemCode {

  // Fuels
  JET_FUEL_TONS(FUEL, "jet_fuel", emptyList()),
  AVIATION_GASOLINE_TONS(FUEL, "gasoline", emptyList()),
  MW50_TONS(FUEL, "methanol_mixture", emptyList()),
  DIESEL_TONS(FUEL, "diesel", emptyList()),

  // Planes - US

  F_14_B(PLANES, "F-14B", List.of("1", "1", "1", "272")),
  F_15_C(PLANES, "F-15C", List.of("1", "1", "1", "6")),
  F_16_C(PLANES, "F-16C_50", List.of("1", "1", "1", "269")),
  F_A_18_C(PLANES, "FA-18C_hornet", List.of("1", "1", "1", "273")),
  AV_8B_NA(PLANES, "AV8BNA", List.of("1", "1", "1", "216")),

  // Planes - RUS

  SU_27(PLANES, "Su-27", List.of("1", "1", "1", "3")),
  SU_33(PLANES, "Su-33", List.of("1", "1", "1", "4")),
  MIG_29_S(PLANES, "MiG-29S", List.of("1", "1", "1", "50")),

  // Planes - OTHER

  M_2000_C(PLANES, "M-2000C", List.of("1", "1", "1", "277")),
  JF_17(PLANES, "JF-17", List.of("1", "1", "1", "266")),

  // Helicopters - US

  UH_1H(HELICOPTERS, "UH-1H", List.of("1", "2", "6", "166")),

  // Helicopters - RUS

  KA_50(HELICOPTERS, "Ka-50", List.of("1", "2", "6", "155")),

  // A/A missiles - US

  AIM_120_B      (AMMO, "AIM_120", List.of("4", "4", "7", "24")),
  AIM_120_C      (AMMO, "AIM_120C", List.of("4", "4", "7", "106")),
  AIM_54_A_MK_47 (AMMO, "AIM_54A_Mk47", List.of("4", "4", "7", "312")),
  AIM_54_A_MK_60 (AMMO, "AIM_54A_Mk60", List.of("4", "4", "7", "313")),
  AIM_54_C_MK_47 (AMMO, "AIM_54C_Mk47", List.of("4", "4", "7", "314")),
  AIM_7_F        (AMMO, "AIM-7F", List.of("4", "4", "7", "269")),
  AIM_7_M        (AMMO, "weapons.missiles.AIM_7", List.of("4", "4", "7", "21")),
  AIM_7_MH       (AMMO, "AIM-7MH", List.of("4", "4", "7", "270")),
  AIM_9_L        (AMMO, "AIM-9L", List.of("4", "4", "7", "267")),
  AIM_9_M        (AMMO, "AIM_9", List.of("4", "4", "7", "22")),
  AIM_9_P5       (AMMO, "AIM-9P5", List.of("4", "4", "7", "266")),
  AIM_9_X        (AMMO, "AIM_9X", List.of("4", "4", "7", "136")),

  // A/A missiles - RUS

  R_27_ER        (AMMO, "P_27PE", List.of("4", "4", "7", "14")),
  R_27_ET        (AMMO, "P_27TE", List.of("4", "4", "7", "16")),
  R_27_R         (AMMO, "P_27P", List.of("4", "4", "7", "13")),
  R_27_T         (AMMO, "P_27T", List.of("4", "4", "7", "15")),
  R_73           (AMMO, "P_73", List.of("4", "4", "7", "18")),
  R_60           (AMMO, "P_60", List.of("4", "4", "7", "331")),
  R_77           (AMMO, "P_77", List.of("4", "4", "7", "19")),

  // A/A missiles - Other

  MATRA_MAGIC_2  (AMMO, "P_77", List.of("4", "4", "7", "320")),
  MATRA_S_530_D  (AMMO, "P_77", List.of("4", "4", "7", "321")),

  // A/G missiles - US

  AGM_122        (AMMO, "weapons.missiles.AGM_122", List.of("4", "4", "8", "68")),
  AGM_154_A      (AMMO, "weapons.missiles.AGM_154A", List.of("4", "4", "8", "277")),
  AGM_154_C      (AMMO, "weapons.missiles.AGM_154", List.of("4", "4", "8", "132")),

  AGM_65_D       (AMMO, "weapons.missiles.AGM_65D", List.of("4", "4", "8", "77")),
  AGM_65_E       (AMMO, "weapons.missiles.AGM_65E", List.of("4", "4", "8", "70")),
  AGM_65_F       (AMMO, "weapons.missiles.AGM_65F", List.of("4", "4", "8", "271")),
  AGM_65_G       (AMMO, "weapons.missiles.AGM_65G", List.of("4", "4", "8", "139")),
  AGM_65_H       (AMMO, "weapons.missiles.AGM_65H", List.of("4", "4", "8", "138")),
  AGM_65_K       (AMMO, "weapons.missiles.AGM_65K", List.of("4", "4", "8", "61")),

  AGM_84_D       (AMMO, "weapons.missiles.AGM_84D", List.of("4", "4", "8", "275")),
  AGM_84_E       (AMMO, "weapons.missiles.AGM_84E", List.of("4", "4", "8", "63")),
  AGM_88_C       (AMMO, "weapons.missiles.AGM_88", List.of("4", "4", "8", "65")),

  // A/G missiles - RUS

  VIKHR          (AMMO, "weapons.missiles.Vikhr_M", List.of("4", "4", "8", "58")),
  KH_58          (AMMO, "weapons.missiles.Vikhr_M", List.of("4", "4", "8", "46")),
  KH_25MPU       (AMMO, "weapons.missiles.Vikhr_M", List.of("4", "4", "8", "47")),
  KH_29L         (AMMO, "weapons.missiles.Vikhr_M", List.of("4", "4", "8", "49")),
  KH_29T         (AMMO, "weapons.missiles.Vikhr_M", List.of("4", "4", "8", "75")),

  // Bombs - US

  GBU_10         (AMMO, "weapons.bombs.GBU_10", List.of("4", "5", "36", "36")),
  GBU_12         (AMMO, "weapons.bombs.GBU_12", List.of("4", "5", "36", "38")),
  GBU_16         (AMMO, "weapons.bombs.GBU_16", List.of("4", "5", "36", "39")),
  GBU_31         (AMMO, "weapons.bombs.GBU_31", List.of("4", "5", "36", "85")),
  GBU_31_V3B     (AMMO, "weapons.bombs.GBU_31_V_3B", List.of("4", "5", "36", "92")),
  GBU_38         (AMMO, "weapons.bombs.GBU_38", List.of("4", "5", "36", "86")),
  AGM_62         (AMMO, "weapons.bombs.AGM_62", List.of("4", "5", "36", "47")),

  MK_81          (AMMO, "weapons.bombs.Mk_81", List.of("4", "5", "9", "30")),
  MK_82          (AMMO, "weapons.bombs.Mk_82", List.of("4", "5", "9", "31")),
  MK_83          (AMMO, "weapons.bombs.Mk_83", List.of("4", "5", "9", "32")),
  MK_84          (AMMO, "weapons.bombs.Mk_84", List.of("4", "5", "9", "33")),
  MK_82X         (AMMO, "weapons.bombs.MK_82SNAKEYE", List.of("4", "5", "9", "79")),
  MK_82Y         (AMMO, "weapons.bombs.Mk_82Y", List.of("4", "5", "9", "298")),

  // Rockets - US

  ZUNI           (AMMO, "weapons.nurs.Zuni_127", List.of("4", "7", "33", "37")),
  M151           (AMMO, "weapons.nurs.HYDRA_70_M151", List.of("4", "7", "33", "147")),
  M5_HE          (AMMO, "weapons.nurs.HYDRA_70_MK5", List.of("4", "7", "33", "145")),

  ;

  private final WarehouseItemCategory category;
  private final String dcsname;
  private final List<String> wsType;

  private WarehouseItemCode(
    WarehouseItemCategory category,
    String dcsname,
    List<String> wsType) {

    this.category = category;
    this.dcsname = dcsname;
    this.wsType = wsType;
  }

  public WarehouseItemCategory category() {
    return category;
  }

  public String dcsname() {
    return dcsname;
  }

  public String buildWsType() {
    var result = "";
    int ct = 0;
    for (String type: wsType) {
      ct++;
      result +=
"                            ["+ct+"] = "+type+",\n";
    }
    return result;
  }

  public static Optional<WarehouseItemCode> byCode(String code) {
    return Arrays.asList(values())
      .stream()
      .filter(e -> e.dcsname.equals(code))
      .findFirst();
  }

  public static final List<String> ALL_PLANE_CODES = List.of(
    "S-3B",
    "F-16C bl.50",
    "E-2C",
    "L-39ZA",
    "MiG-19P",
    "I-16",
    "Su-17M4",
    "MiG-21Bis",
    "F/A-18A",
    "MiG-29S",
    "Su-33",
    "MiG-23MLD",
    "Su-30",
    "AV8BNA",
    "F-111F",
    "KC130",
    "F-16C_50",
    "Tu-160",
    "Tu-142",
    "M-2000C",
    "B-52H",
    "F-117A",
    "AJS37",
    "Tornado IDS",
    "Su-25",
    "S-3B Tanker",
    "IL-78M",
    "MiG-15bis",
    "F/A-18C",
    "An-26B",
    "An-30M",
    "MiG-25RBT",
    "F-86F Sabre",
    "C-130",
    "Yak-52",
    "Su-25TM",
    "FA-18C_hornet",
    "F-5E-3",
    "Tu-22M3",
    "TF-51D",
    "FW-190A8",
    "C-17A",
    "KC-135",
    "MiG-25PD",
    "MiG-27K",
    "A-10C",
    "C-101EB",
    "KJ-2000",
    "F-15C",
    "Tornado GR4",
    "Tu-95MS",
    "F-16A MLU",
    "Su-24M",
    "Su-27",
    "F-14B",
    "MiG-29A",
    "MQ-9 Reaper",
    "Christen Eagle II",
    "Su-25T",
    "E-3A",
    "J-11A",
    "JF-17",
    "F-14A",
    "Hawk",
    "P-51D",
    "B-1B",
    "SpitfireLFMkIXCW",
    "RQ-1A Predator",
    "F-16A",
    "A-10A",
    "Mirage 2000-5",
    "FW-190D9",
    "Yak-40",
    "P-51D-30-NA",
    "L-39C",
    "MiG-29G",
    "SpitfireLFMkIX",
    "MiG-29K",
    "MiG-31",
    "Bf-109K-4",
    "C-101CC",
    "F-5E",
    "F-16C bl.52d",
    "A-50",
    "KC135MPRS",
    "IL-76MD",
    "Su-24MR",
    "Su-34",
    "F-4E",
    "F-15E");
  public static final List<String> ALL_HELO_CODES = List.of(
    "AH-64D",
    "Ka-52",
    "SH-3W",
    "UH-60A",
    "CH-53E",
    "Mi-8MT",
    "Mi-24V",
    "SA342Minigun",
    "SA342L",
    "Ka-50",
    "CH-47D",
    "AH-64A",
    "AH-1W",
    "Mi-26",
    "Ka-27",
    "SA342M",
    "SH-60B",
    "Mi-28N",
    "OH-58D",
    "UH-1H",
    "SA342Mistral");
}
