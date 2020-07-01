#!/bin/bash -xe

HOST="http://localhost:8080"
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIwMThkYTA5ZC1jMjllLTQ2M2ItOWFhYy0wZWQ2YzI2MjZkM2UiLCJuYW1lIjoiSm9obiBEb2UiLCJpYXQiOjE1MTYyMzkwMjIsInJvbGVzIjpbImNhbXBhaWduX21hbmFnZXIiLCJmYWN0aW9uX21hbmFnZXIiXX0.DDoUAdr-Z_R5bGIwOnjW-IFcXsncwMg2kAuUcB9AYGA"
URLLOGIN="file:///C:/Users/raffa/Documents/GitHub/dcs-faction-fiction-ui/dcs-faction-fiction-initiator/index.html?jwt=$TOKEN"
HEADAUTH="Authorization: Bearer $TOKEN"
HUEY='{ "items": [ { "name": "UH_1H", "amount": 1 } ] }'
BRADLEY='{ "type": "BRADLEY", "location": { "latitude": 41.92665, "longitude": 41.85967, "altitude": 0, "angle": 0} }'
curl -sH "$HEADAUTH" -X POST -d '{"name":"fa"}'                        $HOST/factionmanager-api/factions
curl -sH "$HEADAUTH" -X POST -d '{"name":"ca"}'                        $HOST/campaignmanager-api/campaigns
curl -sH "$HEADAUTH" -X POST -d '{"faction":"fa","airbase":"KUTAISI"}' $HOST/campaignmanager-api/campaigns/ca/factions
curl -sH "$HEADAUTH" -X POST -d "$HUEY"                                $HOST/factionmanager-api/factions/fa/campaigns/ca/warehouse
curl -sH "$HEADAUTH" -X POST -d "$BRADLEY"                             $HOST/factionmanager-api/factions/fa/campaigns/ca/units
