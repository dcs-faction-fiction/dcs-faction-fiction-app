package base.game.units;

import base.game.Airbases;
import static base.game.CampaignCoalition.BLUE;
import static base.game.CampaignCoalition.RED;
import base.game.CampaignMap;
import base.game.Parkings;
import static base.game.warehouse.WarehouseItemCategory.HELICOPTERS;
import static base.game.warehouse.WarehouseItemCategory.PLANES;
import base.game.warehouse.WarehouseItemCode;
import base.service.data.FactionUnit;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class MissionBuilder {

  private int nextId = 1;
  private Set<String> dict = new LinkedHashSet<>();

  public String dict() {
    var strdict = dict.stream()
      .map(s -> "[\""+s+"\"] = \""+s+"\",\n")
      .collect(joining());
return
"dictionary = \n" +
"{\n" +
strdict +
"} -- end of dictionary";
  }

  public String mapResource() {
return
"mapResource = \n" +
"{\n" +
"} -- end of mapResource";
  }

  public String mission(CampaignMap map,
    Map<Airbases, Map<WarehouseItemCode, Integer>> warehouses,
    List<FactionUnit> blueUnitsGround,
    List<FactionUnit> redUnitsGround) {

    // TODO
    // - set date and time
    // - set weather
    // - add ground routes

    // 2h mission, alert 5 minutes before end.
    var missionDurationSeconds = 3600*2;
    var missionEndWarningSeconds = 300;

    var blueUnits = "";
    var redUnits = "";

    for (var e: warehouses.entrySet()) {
      switch(e.getKey().coalition()) {
        default:
        case BLUE:
          blueUnits += addAirbaseAircrafts(e.getKey(), e.getValue());
          break;
        case RED:
          redUnits += addAirbaseAircrafts(e.getKey(), e.getValue());
          break;
      }
    }

    blueUnits += addGroundUnits(blueUnitsGround);
    redUnits += addGroundUnits(redUnitsGround);

    return
"mission = \n" +
"{\n" +
"    [\"requiredModules\"] = \n" +
"    {\n" +
"    }, -- end of [\"requiredModules\"]\n" +
"    [\"date\"] = \n" +
"    {\n" +
"        [\"Day\"] = 21,\n" +
"        [\"Year\"] = 2016,\n" +
"        [\"Month\"] = 6,\n" +
"    }, -- end of [\"date\"]\n" +
"    [\"trig\"] = \n" +
"    {\n" +
"        [\"actions\"] = \n" +
"        {\n" +
"            [1] = \"a_end_mission(\\\"\\\", \\\"\\\", "+missionEndWarningSeconds+"); mission.trig.func[1]=nil;\",\n" +
"        }, -- end of [\"actions\"]\n" +
"        [\"events\"] = \n" +
"        {\n" +
"        }, -- end of [\"events\"]\n" +
"        [\"custom\"] = \n" +
"        {\n" +
"        }, -- end of [\"custom\"]\n" +
"        [\"func\"] = \n" +
"        {\n" +
"            [1] = \"if mission.trig.conditions[1]() then mission.trig.actions[1]() end\",\n" +
"        }, -- end of [\"func\"]\n" +
"        [\"flag\"] = \n" +
"        {\n" +
"            [1] = true,\n" +
"        }, -- end of [\"flag\"]\n" +
"        [\"conditions\"] = \n" +
"        {\n" +
"            [1] = \"return(c_time_after("+missionDurationSeconds+") )\",\n" +
"        }, -- end of [\"conditions\"]\n" +
"        [\"customStartup\"] = \n" +
"        {\n" +
"        }, -- end of [\"customStartup\"]\n" +
"        [\"funcStartup\"] = \n" +
"        {\n" +
"        }, -- end of [\"funcStartup\"]\n" +
"    }, -- end of [\"trig\"]\n" +
"    [\"result\"] = \n" +
"    {\n" +
"        [\"offline\"] = \n" +
"        {\n" +
"            [\"conditions\"] = \n" +
"            {\n" +
"            }, -- end of [\"conditions\"]\n" +
"            [\"actions\"] = \n" +
"            {\n" +
"            }, -- end of [\"actions\"]\n" +
"            [\"func\"] = \n" +
"            {\n" +
"            }, -- end of [\"func\"]\n" +
"        }, -- end of [\"offline\"]\n" +
"        [\"total\"] = 0,\n" +
"        [\"blue\"] = \n" +
"        {\n" +
"            [\"conditions\"] = \n" +
"            {\n" +
"            }, -- end of [\"conditions\"]\n" +
"            [\"actions\"] = \n" +
"            {\n" +
"            }, -- end of [\"actions\"]\n" +
"            [\"func\"] = \n" +
"            {\n" +
"            }, -- end of [\"func\"]\n" +
"        }, -- end of [\"blue\"]\n" +
"        [\"red\"] = \n" +
"        {\n" +
"            [\"conditions\"] = \n" +
"            {\n" +
"            }, -- end of [\"conditions\"]\n" +
"            [\"actions\"] = \n" +
"            {\n" +
"            }, -- end of [\"actions\"]\n" +
"            [\"func\"] = \n" +
"            {\n" +
"            }, -- end of [\"func\"]\n" +
"        }, -- end of [\"red\"]\n" +
"    }, -- end of [\"result\"]\n" +
"    [\"maxDictId\"] = 1,\n" +
"    [\"pictureFileNameN\"] = \n" +
"    {\n" +
"    }, -- end of [\"pictureFileNameN\"]\n" +
"    [\"groundControl\"] = \n" +
"    {\n" +
"        [\"isPilotControlVehicles\"] = false,\n" +
"        [\"roles\"] = \n" +
"        {\n" +
"            [\"artillery_commander\"] = \n" +
"            {\n" +
"                [\"neutrals\"] = 0,\n" +
"                [\"blue\"] = 8,\n" +
"                [\"red\"] = 8,\n" +
"            }, -- end of [\"artillery_commander\"]\n" +
"            [\"instructor\"] = \n" +
"            {\n" +
"                [\"neutrals\"] = 0,\n" +
"                [\"blue\"] = 1,\n" +
"                [\"red\"] = 1,\n" +
"            }, -- end of [\"instructor\"]\n" +
"            [\"observer\"] = \n" +
"            {\n" +
"                [\"neutrals\"] = 0,\n" +
"                [\"blue\"] = 0,\n" +
"                [\"red\"] = 0,\n" +
"            }, -- end of [\"observer\"]\n" +
"            [\"forward_observer\"] = \n" +
"            {\n" +
"                [\"neutrals\"] = 0,\n" +
"                [\"blue\"] = 8,\n" +
"                [\"red\"] = 8,\n" +
"            }, -- end of [\"forward_observer\"]\n" +
"        }, -- end of [\"roles\"]\n" +
"    }, -- end of [\"groundControl\"]\n" +
"    [\"goals\"] = \n" +
"    {\n" +
"    }, -- end of [\"goals\"]\n" +
"    [\"weather\"] = \n" +
"    {\n" +
"        [\"atmosphere_type\"] = 0,\n" +
"        [\"groundTurbulence\"] = 0,\n" +
"        [\"enable_fog\"] = false,\n" +
"        [\"wind\"] = \n" +
"        {\n" +
"            [\"at8000\"] = \n" +
"            {\n" +
"                [\"speed\"] = 0,\n" +
"                [\"dir\"] = 0,\n" +
"            }, -- end of [\"at8000\"]\n" +
"            [\"at2000\"] = \n" +
"            {\n" +
"                [\"speed\"] = 0,\n" +
"                [\"dir\"] = 0,\n" +
"            }, -- end of [\"at2000\"]\n" +
"            [\"atGround\"] = \n" +
"            {\n" +
"                [\"speed\"] = 0,\n" +
"                [\"dir\"] = 0,\n" +
"            }, -- end of [\"atGround\"]\n" +
"        }, -- end of [\"wind\"]\n" +
"        [\"season\"] = \n" +
"        {\n" +
"            [\"temperature\"] = 20,\n" +
"        }, -- end of [\"season\"]\n" +
"        [\"type_weather\"] = 0,\n" +
"        [\"qnh\"] = 760,\n" +
"        [\"cyclones\"] = \n" +
"        {\n" +
"        }, -- end of [\"cyclones\"]\n" +
"        [\"name\"] = \"Winter, clean sky\",\n" +
"        [\"fog\"] = \n" +
"        {\n" +
"            [\"thickness\"] = 0,\n" +
"            [\"visibility\"] = 0,\n" +
"        }, -- end of [\"fog\"]\n" +
"        [\"dust_density\"] = 0,\n" +
"        [\"enable_dust\"] = false,\n" +
"        [\"visibility\"] = \n" +
"        {\n" +
"            [\"distance\"] = 80000,\n" +
"        }, -- end of [\"visibility\"]\n" +
"        [\"clouds\"] = \n" +
"        {\n" +
"            [\"thickness\"] = 200,\n" +
"            [\"density\"] = 0,\n" +
"            [\"base\"] = 300,\n" +
"            [\"iprecptns\"] = 0,\n" +
"        }, -- end of [\"clouds\"]\n" +
"    }, -- end of [\"weather\"]\n" +
"    [\"theatre\"] = \""+map.dcsname()+"\",\n" +
"    [\"triggers\"] = \n" +
"    {\n" +
"        [\"zones\"] = \n" +
"        {\n" +
"        }, -- end of [\"zones\"]\n" +
"    }, -- end of [\"triggers\"]\n" +
"    [\"map\"] = \n" +
"    {\n" +
"        [\"centerY\"] = 638429.14857143,\n" +
"        [\"zoom\"] = 131220,\n" +
"        [\"centerX\"] = -298572.99714286,\n" +
"    }, -- end of [\"map\"]\n" +
"    [\"coalitions\"] = \n" +
"    {\n" +
"        [\"neutrals\"] = \n" +
"        {\n" +
"        }, -- end of [\"neutrals\"]\n" +
"        [\"blue\"] = \n" +
"        {\n" +
"            [1] = 2,\n" +
"        }, -- end of [\"blue\"]\n" +
"        [\"red\"] = \n" +
"        {\n" +
"            [1] = 7,\n" +
"        }, -- end of [\"red\"]\n" +
"    }, -- end of [\"coalitions\"]\n" +
"    [\"descriptionText\"] = \"\",\n" +
"    [\"pictureFileNameR\"] = \n" +
"    {\n" +
"    }, -- end of [\"pictureFileNameR\"]\n" +
"    [\"descriptionNeutralsTask\"] = \"\",\n" +
"    [\"descriptionBlueTask\"] = \"\",\n" +
"    [\"descriptionRedTask\"] = \"\",\n" +
"    [\"pictureFileNameB\"] = \n" +
"    {\n" +
"    }, -- end of [\"pictureFileNameB\"]\n" +
"    [\"coalition\"] = \n" +
"    {\n" +
"        [\"blue\"] = \n" +
"        {\n" +
"            [\"bullseye\"] = \n" +
"            {\n" +
"                [\"y\"] = 617414,\n" +
"                [\"x\"] = -291014,\n" +
"            }, -- end of [\"bullseye\"]\n" +
"            [\"nav_points\"] = \n" +
"            {\n" +
"            }, -- end of [\"nav_points\"]\n" +
"            [\"name\"] = \"blue\",\n" +
"            [\"country\"] = \n" +
"            {\n" +
"                [1] = \n" +
"                {\n" +
"                    [\"id\"] = 2,\n" +
"                    [\"name\"] = \"USA\",\n" +
blueUnits+
"                }, -- end of [8]\n" +
"            }, -- end of [\"country\"]\n" +
"        }, -- end of [\"blue\"]\n" +
"        [\"red\"] = \n" +
"        {\n" +
"            [\"bullseye\"] = \n" +
"            {\n" +
"                [\"y\"] = 371700,\n" +
"                [\"x\"] = 11557,\n" +
"            }, -- end of [\"bullseye\"]\n" +
"            [\"nav_points\"] = \n" +
"            {\n" +
"            }, -- end of [\"nav_points\"]\n" +
"            [\"name\"] = \"red\",\n" +
"            [\"country\"] = \n" +
"            {\n" +
"                [1] = \n" +
"                {\n" +
"                    [\"id\"] = 7,\n" +
"                    [\"name\"] = \"USAF Aggressors\",\n" +
redUnits+
"                }, -- end of [2]\n" +
"            }, -- end of [\"country\"]\n" +
"        }, -- end of [\"red\"]\n" +
"    }, -- end of [\"coalition\"]\n" +
"    [\"sortie\"] = \"\",\n" +
"    [\"version\"] = 16,\n" +
"    [\"trigrules\"] = \n" +
"    {\n" +
"        [1] = \n" +
"        {\n" +
"            [\"rules\"] = \n" +
"            {\n" +
"                [1] = \n" +
"                {\n" +
"                    [\"seconds\"] = "+missionDurationSeconds+",\n" +
"                    [\"predicate\"] = \"c_time_after\",\n" +
"                    [\"zone\"] = \"\",\n" +
"                }, -- end of [1]\n" +
"            }, -- end of [\"rules\"]\n" +
"            [\"comment\"] = \"END\",\n" +
"            [\"eventlist\"] = \"\",\n" +
"            [\"actions\"] = \n" +
"            {\n" +
"                [1] = \n" +
"                {\n" +
"                    [\"text\"] = \"\",\n" +
"                    [\"start_delay\"] = "+missionEndWarningSeconds+",\n" +
"                    [\"zone\"] = \"\",\n" +
"                    [\"predicate\"] = \"a_end_mission\",\n" +
"                    [\"winner\"] = \"\",\n" +
"                    [\"meters\"] = 1000,\n" +
"                }, -- end of [1]\n" +
"            }, -- end of [\"actions\"]\n" +
"            [\"predicate\"] = \"triggerOnce\",\n" +
"        }, -- end of [1]\n" +
"    }, -- end of [\"trigrules\"]\n" +
"    [\"currentKey\"] = 54,\n" +
"    [\"start_time\"] = 28800,\n" +
"    [\"forcedOptions\"] = \n" +
"    {\n" +
"    }, -- end of [\"forcedOptions\"]\n" +
"    [\"failures\"] = \n" +
"    {\n" +
"    }, -- end of [\"failures\"]\n" +
"} -- end of mission\n"
;
  }

  public String addAirbaseAircrafts(Airbases base, Map<WarehouseItemCode, Integer> warehouse) {
    int parkingCt = 0;
    var parkings = Parkings.getParkings(base.map(), base.warehouseId());
    String builtPlanes = "";
    String builtHelos = "";

    var planes = warehouse.entrySet().stream()
      .filter(e -> e.getKey().category() == PLANES)
      .collect(toList());
    var helos = warehouse.entrySet().stream()
      .filter(e ->  e.getKey().category() == HELICOPTERS)
      .collect(toList());

    int luact = 1;
    for (int i = 0; i < planes.size(); i++) {
      var e = planes.get(i);
      var k = e.getKey();
      var v = e.getValue();
      for (int ct = 0; ct < v; ct++) {
        var parking = parkings.get(parkingCt++);
        builtPlanes +=
"                            ["+(luact)+"] = \n" +
"                            {\n" +
buildParkedUnit(nextId++, k.dcsname(), base.warehouseId(),
  parking.get(0).intValue(),
  parking.get(1).intValue(),
  parking.get(2),
  parking.get(3))+
"                            }, -- end of ["+(luact)+"]\n"
;
        luact++;
      }
    }

    luact = 1;
    for (int i = 0; i < helos.size(); i++) {
      var e = helos.get(i);
      var k = e.getKey();
      var v = e.getValue();
      for (int ct = 0; ct < v; ct++) {
        var parking = parkings.get(parkingCt++);
        builtHelos +=
"                            ["+(luact)+"] = \n" +
"                            {\n" +
buildParkedUnit(nextId++, k.dcsname(), base.warehouseId(),
  parking.get(0).intValue(),
  parking.get(1).intValue(),
  parking.get(2),
  parking.get(3))+
"                            }, -- end of ["+(luact)+"]\n"
;
        luact++;
      }
    }

    return
"                    [\"helicopter\"] = \n" +
"                    {\n" +
"                        [\"group\"] = \n" +
"                        {\n" +
builtHelos+
"                        }, -- end of [\"group\"]\n" +
"                    }, -- end of [\"helicopter\"]\n" +
"                    [\"plane\"] = \n" +
"                    {\n" +
"                        [\"group\"] = \n" +
"                        {\n" +
builtPlanes+
"                        }, -- end of [\"group\"]\n" +
"                    }, -- end of [\"plane\"]\n"
;
  }

  public String buildParkedUnit(int id, String type, int airdromeId, int parking, int parkingId, double x, double y) {
    var name = "ParkedUnit "+id;
    dict.add(name);
    return
"                                [\"modulation\"] = 0,\n" +
"                                [\"tasks\"] = \n" +
"                                {\n" +
"                                }, -- end of [\"tasks\"]\n" +
"                                [\"radioSet\"] = false,\n" +
"                                [\"task\"] = \"Nothing\",\n" +
"                                [\"uncontrolled\"] = false,\n" +
"                                [\"taskSelected\"] = true,\n" +
"                                [\"route\"] = \n" +
"                                {\n" +
"                                    [\"points\"] = \n" +
"                                    {\n" +
"                                        [1] = \n" +
"                                        {\n" +
"                                            [\"alt\"] = 10,\n" +
"                                            [\"action\"] = \"From Parking Area\",\n" +
"                                            [\"alt_type\"] = \"BARO\",\n" +
"                                            [\"speed\"] = 138.88888888889,\n" +
"                                            [\"task\"] = \n" +
"                                            {\n" +
"                                                [\"id\"] = \"ComboTask\",\n" +
"                                                [\"params\"] = \n" +
"                                                {\n" +
"                                                    [\"tasks\"] = \n" +
"                                                    {\n" +
"                                                    }, -- end of [\"tasks\"]\n" +
"                                                }, -- end of [\"params\"]\n" +
"                                            }, -- end of [\"task\"]\n" +
"                                            [\"type\"] = \"TakeOffParking\",\n" +
"                                            [\"ETA\"] = 0,\n" +
"                                            [\"ETA_locked\"] = true,\n" +
"                                            [\"y\"] = "+x+",\n" +
"                                            [\"x\"] = "+y+",\n" +
"                                            [\"name\"] = \"0\",\n" +
"                                            [\"formation_template\"] = \"\",\n" +
"                                            [\"airdromeId\"] = "+airdromeId+",\n" +
"                                            [\"speed_locked\"] = true,\n" +
"                                        }, -- end of [1]\n" +
"                                    }, -- end of [\"points\"]\n" +
"                                }, -- end of [\"route\"]\n" +
"                                [\"groupId\"] = "+id+",\n" +
"                                [\"hidden\"] = false,\n" +
"                                [\"units\"] = \n" +
"                                {\n" +
"                                    [1] = \n" +
"                                    {\n" +
"                                        [\"alt\"] = 10,\n" +
"                                        [\"alt_type\"] = \"BARO\",\n" +
"                                        [\"livery_id\"] = \"36STV Camouflage\",\n" +
"                                        [\"skill\"] = \"Client\",\n" +
"                                        [\"parking\"] = \""+parking+"\",\n" +
"                                        [\"speed\"] = 138.88888888889,\n" +
"                                        [\"AddPropAircraft\"] = \n" +
"                                        {\n" +
"                                        }, -- end of [\"AddPropAircraft\"]\n" +
"                                        [\"type\"] = \""+type+"\",\n" +
"                                        [\"Radio\"] = \n" +
"                                        {\n" +
"                                        }, -- end of [\"Radio\"]\n" +
"                                        [\"unitId\"] = "+id+",\n" +
"                                        [\"psi\"] = 0,\n" +
"                                        [\"parking_id\"] = \""+parkingId+"\",\n" +
"                                        [\"x\"] = "+x+",\n" +
"                                        [\"name\"] = \""+name+"\",\n" +
"                                        [\"payload\"] = \n" +
"                                        {\n" +
"                                            [\"pylons\"] = \n" +
"                                            {\n" +
"                                            }, -- end of [\"pylons\"]\n" +
"                                            [\"fuel\"] = 0,\n" +
"                                            [\"flare\"] = 0,\n" +
"                                            [\"ammo_type\"] = 0,\n" +
"                                            [\"chaff\"] = 0,\n" +
"                                            [\"gun\"] = 0,\n" +
"                                        }, -- end of [\"payload\"]\n" +
"                                        [\"y\"] = "+y+",\n" +
"                                        [\"heading\"] = 0,\n" +
"                                        [\"callsign\"] = \n" +
"                                        {\n" +
"                                            [1] = 1,\n" +
"                                            [2] = 1,\n" +
"                                            [3] = 1,\n" +
"                                            [\"name\"] = \"Enfield11\",\n" +
"                                        }, -- end of [\"callsign\"]\n" +
"                                        [\"onboard_num\"] = \"010\",\n" +
"                                    }, -- end of [1]\n" +
"                                }, -- end of [\"units\"]\n" +
"                                [\"y\"] = "+y+",\n" +
"                                [\"x\"] = "+x+",\n" +
"                                [\"name\"] = \""+name+"\",\n" +
"                                [\"communication\"] = true,\n" +
"                                [\"start_time\"] = 0,\n" +
"                                [\"frequency\"] = 305,\n";
  }

  public String addGroundUnits(List<FactionUnit> units) {
    String builtUnits = "";
    int luact = 1;
    for (var unit: units) {
      builtUnits +=
"                            ["+(luact)+"] = \n" +
"                            {\n" +
buildGroundUnit(nextId++, unit)+
"                            }, -- end of ["+(luact)+"]\n"
;
      luact++;
    }

    return
"                    [\"vehicle\"] = \n" +
"                    {\n" +
"                        [\"group\"] = \n" +
"                        {\n" +
builtUnits+
"                        }, -- end of [\"group\"]\n" +
"                    }, -- end of [\"vehicle\"]\n";
  }

  public String buildGroundUnit(int id, FactionUnit unit) {
    var name = "[UUID:"+unit.id()+"] "
             + "[grouptype:"+unit.type().name()+"]"
             + "[lat:"+unit.location().latitude()+"] "
             + "[lon:"+unit.location().longitude()+"]";
    dict.add(name);
    return
"                                [\"visible\"] = false,\n" +
"                                [\"lateActivation\"] = true,\n" +
"                                [\"tasks\"] = \n" +
"                                {\n" +
"                                }, -- end of [\"tasks\"]\n" +
"                                [\"uncontrollable\"] = false,\n" +
"                                [\"task\"] = \"Ground Nothing\",\n" +
"                                [\"route\"] = \n" +
"                                {\n" +
"                                    [\"spans\"] = \n" +
"                                    {\n" +
"                                    }, -- end of [\"spans\"]\n" +
"                                    [\"points\"] = \n" +
"                                    {\n" +
"                                        [1] = \n" +
"                                        {\n" +
"                                            [\"alt\"] = 88,\n" +
"                                            [\"type\"] = \"Turning Point\",\n" +
"                                            [\"ETA\"] = 0,\n" +
"                                            [\"alt_type\"] = \"BARO\",\n" +
"                                            [\"formation_template\"] = \"\",\n" +
"                                            [\"y\"] = 0,\n" +
"                                            [\"x\"] = 0,\n" +
"                                            [\"name\"] = \"\",\n" +
"                                            [\"ETA_locked\"] = true,\n" +
"                                            [\"speed\"] = 0,\n" +
"                                            [\"action\"] = \"Off Road\",\n" +
"                                            [\"task\"] = \n" +
"                                            {\n" +
"                                                [\"id\"] = \"ComboTask\",\n" +
"                                                [\"params\"] = \n" +
"                                                {\n" +
"                                                    [\"tasks\"] = \n" +
"                                                    {\n" +
"                                                    }, -- end of [\"tasks\"]\n" +
"                                                }, -- end of [\"params\"]\n" +
"                                            }, -- end of [\"task\"]\n" +
"                                            [\"speed_locked\"] = true,\n" +
"                                        }, -- end of [1]\n" +
"                                    }, -- end of [\"points\"]\n" +
"                                }, -- end of [\"route\"]\n" +
"                                [\"groupId\"] = "+id+",\n" +
"                                [\"hidden\"] = false,\n" +
"                                [\"units\"] = \n" +
"                                {\n" +
"                                    [1] = \n" +
"                                    {\n" +
"                                        [\"type\"] = \""+unit.type().dcstype()+"\",\n" +
"                                        [\"transportable\"] = \n" +
"                                        {\n" +
"                                            [\"randomTransportable\"] = false,\n" +
"                                        }, -- end of [\"transportable\"]\n" +
"                                        [\"unitId\"] = "+id+",\n" +
"                                        [\"skill\"] = \"Excellent\",\n" +
"                                        [\"y\"] = 0,\n" +
"                                        [\"x\"] = 0,\n" +
"                                        [\"name\"] = \""+name+"\",\n" +
"                                        [\"heading\"] = 0,\n" +
"                                        [\"playerCanDrive\"] = true,\n" +
"                                    }, -- end of [1]\n" +
"                                }, -- end of [\"units\"]\n" +
"                                [\"y\"] = 0,\n" +
"                                [\"x\"] = 0,\n" +
"                                [\"name\"] = \""+name+"\",\n" +
"                                [\"start_time\"] = 0,\n";
  }
}
