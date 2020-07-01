package base.service;

import base.api.factionmanager.data.FactionCampaignComboPayload;
import base.api.factionmanager.data.ImmutableFactionCampaignComboPayload;
import base.game.Airbases;
import com.github.apilab.rest.exceptions.NotFoundException;
import static java.lang.String.format;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;

public class FactionService {

  @Inject Jdbi jdbi;

  @Inject
  public FactionService() {
    ////
  }

  public void createFaction(UUID user, String factionName) {
    jdbi.useHandle(h ->
      h.execute("insert into faction (name, commander_user) values(?, ?)",
        factionName, user)
    );
  }

  public FactionCampaignComboPayload getFactionCampaignCombo(UUID user, String factionName, String campaignName) {
    return jdbi.withHandle(h ->
      h.select("select "
        + "cf.faction_name, "
        + "cf.campaign_name, "
        + "cf.airbase, "
        + "cf.zone_size_ft "
        + "from campaign_faction cf "
        + "left join faction f on cf.faction_name = f.name "
        + "where cf.campaign_name = ? and cf.faction_name = ? and f.commander_user = ? "
        + "limit 1", campaignName, factionName, user)
        .map((RowMapper<FactionCampaignComboPayload>) (r, c) ->
          ImmutableFactionCampaignComboPayload
            .builder()
            .factionName(r.getString(1))
            .campaignName(r.getString(2))
            .airbase(Airbases.valueOf(r.getString(3)))
            .zoneSizeFt(r.getInt(4))
            .build())
        .findFirst()
      .orElseThrow(() -> new NotFoundException("Not found"))
    );
  }

  public List<String> getFactions(UUID user) {
    return jdbi.withHandle(h ->
      h.select("select name from faction where commander_user = ? limit 10", user)
        .mapTo(String.class)
        .list()
    );
  }

  public List<String> getCampaigns(String factionName) {
    return jdbi.withHandle(h ->
      h.select("select campaign_name from campaign_faction where faction_name = ? limit 1000", factionName)
        .mapTo(String.class)
        .list()
    );
  }

  public int getCredits(String factionName, String campaignName, Airbases airbase) {
    return jdbi.withHandle(h ->
      h.select("select credits from campaign_faction "
             + "where campaign_name = ? and faction_name = ? and airbase = ?",
                campaignName, factionName, airbase)
        .mapTo(Integer.class)
        .findFirst()
        .orElse(0)
    );
  }

  public Airbases assertGetAirbase(String factionName, String campaignName) {
    return jdbi.withHandle(h ->
      h.select("select airbase from campaign_faction "
             + "where campaign_name = ? and faction_name = ?",
                campaignName, factionName)
        .mapTo(Airbases.class)
        .findFirst()
        .orElseThrow(() -> new NotFoundException(format("No airbase found for %s assigned to campaign %s", factionName, campaignName)))
    );
  }

}
