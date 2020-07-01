package base.game.units;

public class OptionsBuilder {

  private OptionsBuilder() {}

  public static String build() {
    return
"options = \n" +
"{\n" +
"    [\"difficulty\"] = \n" +
"    {\n" +
"        [\"geffect\"] = \"realistic\",\n" +
"        [\"padlock\"] = true,\n" +
"        [\"cockpitStatusBarAllowed\"] = false,\n" +
"        [\"wakeTurbulence\"] = false,\n" +
"        [\"map\"] = true,\n" +
"        [\"easyRadar\"] = false,\n" +
"        [\"fuel\"] = false,\n" +
"        [\"miniHUD\"] = false,\n" +
"        [\"controlsIndicator\"] = false,\n" +
"        [\"birds\"] = 0,\n" +
"        [\"optionsView\"] = \"optview_all\",\n" +
"        [\"permitCrash\"] = true,\n" +
"        [\"immortal\"] = false,\n" +
"        [\"easyCommunication\"] = false,\n" +
"        [\"cockpitVisualRM\"] = false,\n" +
"        [\"easyFlight\"] = false,\n" +
"        [\"reports\"] = true,\n" +
"        [\"hideStick\"] = true,\n" +
"        [\"radio\"] = false,\n" +
"        [\"userMarks\"] = true,\n" +
"        [\"unrestrictedSATNAV\"] = true,\n" +
"        [\"units\"] = \"imperial\",\n" +
"        [\"spectatorExternalViews\"] = true,\n" +
"        [\"tips\"] = true,\n" +
"        [\"userSnapView\"] = false,\n" +
"        [\"RBDAI\"] = false,\n" +
"        [\"externalViews\"] = true,\n" +
"        [\"iconsTheme\"] = \"russian\",\n" +
"        [\"avionicsLanguage\"] = \"english\",\n" +
"        [\"weapons\"] = false,\n" +
"        [\"setGlobal\"] = true,\n" +
"        [\"labels\"] = 0,\n" +
"    }, -- end of [\"difficulty\"]\n" +
"} -- end of options"
;
  }
}
