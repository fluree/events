(ns user
  (:require [clojure.edn :as edn]
            [fluree.events.core :as events]
            [cheshire.core :as json]
            [fluree.db.api :as fdb]
            [aleph.http :as xhttp]
            ))

;
;(def schema (edn/read-string (slurp "resources/schema.edn")))
;(def conn (fdb/connect "http://localhost:8090"))
;(def db-name "event/a1")
;(def db (fdb/db conn db-name))
;

(defn transact-event-schema
  [conn ledger]
  @(fdb/transact conn ledger (edn/read-string (slurp "resources/schema.edn")))
  :done)


(defn transact-daas-data
  [conn ledger]
  @(fdb/transact conn ledger (edn/read-string (slurp "resources/daas-schema.edn")))
  @(fdb/transact conn ledger (edn/read-string (slurp "resources/daas-seed.edn")))
  @(fdb/transact conn ledger (edn/read-string (slurp "resources/daas-rules.edn")))
  @(fdb/transact conn ledger (edn/read-string (slurp "resources/daas-auth.edn")))
  :done)

(defn edn->json
  "Converts EDN data to JSON data"
  [edn-string]
  (-> (edn/read-string edn-string)
      (json/encode)))


(comment



  @(xhttp/get "https://l92xqqxncf.execute-api.us-east-1.amazonaws.com/Prod/hello")

  (def conn (fdb/connect "http://localhost:8080"))
  (def ledger "daas/sprint6")

  @(fdb/transact conn ledger (edn/read-string (slurp "resources/sample-tx.edn")))

  (transact-event-schema conn ledger)

  (-> (slurp "resources/daas-seed.edn")
      (edn->json  )
      (println))



  schema
  @(fdb/ledger-list conn)
  @(fdb/new-ledger conn "event/a1")
  @(fdb/transact conn "event/a1" schema)

  @(fdb/query (fdb/db conn db-name)
              {:select ["*"] :from "monitor"})

  @(fdb/transact (:conn @fluree.events.core/system) "event/a1"
                 [{:_id                "metadata",
                   :missionContext     {:_id      "missionContext",
                                        :activity {:_id "activityType", :id "activity"},
                                        :mission  {:_id "missionType", :id "mission"}},
                   :security           {:_id "securityType", :Classification "UNCLASSIFIED"},
                   :cdsMetadata        {:_id "cdsMetadataType", :destinationDomain "secret", :sourceDomain "unclassified"},
                   :exercise           "EXERCISE",
                   :controlSet         {:_id                 "controlSetType",
                                        :dataType            "Access Controls",
                                        :resourceIdentity    {:_id  "resourceIdentityType",
                                                              :hash {:_id       "hashType",
                                                                     :hash      "bdda4444cbe025bd6ffba8a069b372762818faf02775978066d77b1173c25439",
                                                                     :algorithm "SHA-256"},
                                                              :id   "https://www.kaggle.com/heshamasem/binetflow?resource=download/JALvbFxOYwmeddBAmbZW%2Fversions%2F0mAQLG3iPvKhJWRxNMpm%2Ffiles%2Fcapture20110819.binetflow&downloadHash=b914e7ae81618de99514ddab3a4abeb5412bcf66d76eb8542127ad927f0e088e"},
                                        :createDateTime      1603152000000,
                                        :dataSource          "acas",
                                        :responsibleEntity   {:_id "responsibleEntityType", :cust "cust", :orig "orig"},
                                        :resourceDisposition {:_id "resourceDispositionType", :rule "rule"},
                                        :policyReference     {:_id "policyReferenceType", :reference "ref"}},
                   :authorityReference {:_id "authorityReferenceType", :id "ref"},
                   :provenanceData     {:_id "provenanceType", :dataProviderType "DMSS-KIT", :dataProvider "provider"},
                   :ocoMetadata        {:_id                "ocoMetadataType",
                                        :infrastructureUsed [{:_id "infrastructureType", :id "infra1"}],
                                        :cyberToolsUsed     [{:_id "cyberToolType", :id "tool1"}]}}])


  (def sample-tx (edn/read-string (slurp "resources/sample-tx.edn")))
  sample-tx

  (transact-auth)

  (json/encode {:select ["_id"] :from "metadata" :limit 1})

  )


(comment
  (-> "[\n  {\n    \"_id\": \"_collection\",\n    \"name\": \"organization\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"organization/name\",\n    \"type\": \"string\",\n    \"unique\": true\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"organization/members\",\n    \"type\": \"ref\",\n    \"component\": true,\n    \"multi\": true,\n    \"restrictCollection\": \"_user\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"organization/datasets\",\n    \"type\": \"ref\",\n    \"component\": true,\n    \"multi\": true,\n    \"restrictCollection\": \"metadata\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"_user/name\",\n    \"type\": \"string\",\n    \"unique\": true\n  },\n  {\n    \"_id\": \"_collection\",\n    \"name\": \"metadata\",\n    \"spec\": [\"_fn$ReqSecurity\", \"_fn$ReqControlSet\"],\n    \"specDoc\": \"Metadata is required to have /security and /controlSet\"\n  },\n  {\n    \"_id\": \"_fn$ReqSecurity\",\n    \"name\": \"ReqSecurity\",\n    \"code\": \"(boolean (get (?s) \\\"metadata/security\\\"))\"\n  },\n  {\n    \"_id\": \"_fn$ReqControlSet\",\n    \"name\": \"ReqControlSet\",\n    \"code\": \"(boolean (get (?s) \\\"metadata/controlSet\\\"))\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"metadata/exercise\",\n    \"type\": \"tag\",\n    \"restrictTag\": true,\n    \"doc\": \"enum: [EXERCISE]\"\n  },\n  { \"_id\": \"_tag\", \"id\": \"metadata/exercise:EXERCISE\" },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"metadata/security\",\n    \"type\": \"ref\",\n    \"component\": true,\n    \"restrictCollection\": \"securityType\"\n  },\n  {\n    \"_id\": \"_collection\",\n    \"name\": \"securityType\",\n    \"spec\": [\"_fn$ReqClassification\"],\n    \"specDoc\": \"SecurityType is required to have /Classification\"\n  },\n  {\n    \"_id\": \"_fn$ReqClassification\",\n    \"name\": \"ReqClassification\",\n    \"code\": \"(boolean (get (?s) \\\"securityType/Classification\\\"))\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"securityType/Classification\",\n    \"type\": \"tag\",\n    \"restrictTag\": true,\n    \"doc\": \"enum: [UNCLASSIFIED, U]\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"securityType/Classification:UNCLASSIFIED\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"securityType/Classification:U\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"securityType/Dissemination\",\n    \"type\": \"tag\",\n    \"restrictTag\": true,\n    \"doc\": \"enum: [FOR OFFICIAL USE ONLY, FOUO]\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"securityType/Dissemination:FOR OFFICIAL USE ONLY\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"securityType/Dissemination:FOUO\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"metadata/authorityReference\",\n    \"type\": \"ref\",\n    \"component\": true,\n    \"restrictCollection\": \"authorityReferenceType\"\n  },\n  {\n    \"_id\": \"_collection\",\n    \"name\": \"authorityReferenceType\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"authorityReferenceType/id\",\n    \"type\": \"string\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"metadata/controlSet\",\n    \"type\": \"ref\",\n    \"component\": true,\n    \"restrictCollection\": \"controlSetType\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"metadata/missionContext\",\n    \"type\": \"ref\",\n    \"component\": true,\n    \"restrictCollection\": \"missionContext\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"metadata/ocoMetadata\",\n    \"type\": \"ref\",\n    \"component\": true,\n    \"restrictCollection\": \"ocoMetadataType\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"metadata/cdsMetadata\",\n    \"type\": \"ref\",\n    \"component\": true,\n    \"restrictCollection\": \"cdsMetadataType\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"metadata/provenanceData\",\n    \"type\": \"ref\",\n    \"component\": true,\n    \"restrictCollection\": \"provenanceType\"\n  },\n  {\n    \"_id\": \"_collection\",\n    \"name\": \"provenanceType\",\n    \"spec\": [\"_fn$ReqDataProviderType\", \"_fn$ReqDataProvider\"],\n    \"specDoc\": \"provenanceType is required to have /dataProviderType, /dataProvider\"\n  },\n  {\n    \"_id\": \"_fn$ReqDataProviderType\",\n    \"name\": \"ReqDataProviderType\",\n    \"code\": \"(boolean (get (?s) \\\"provenanceType/dataProviderType\\\"))\"\n  },\n  {\n    \"_id\": \"_fn$ReqDataProvider\",\n    \"name\": \"ReqDataProvider\",\n    \"code\": \"(boolean (get (?s) \\\"provenanceType/dataProvider\\\"))\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"provenanceType/dataProviderType\",\n    \"type\": \"tag\",\n    \"restrictTag\": true,\n    \"doc\": \"enum: [DMSS-KIT]\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"provenanceType/dataProviderType:DMSS-KIT\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"provenanceType/dataProvider\",\n    \"type\": \"string\",\n    \"index\": true\n  },\n  {\n    \"_id\": \"_collection\",\n    \"name\": \"cdsMetadataType\",\n    \"spec\": [\"_fn$ReqDestinationDomain\", \"_fn$ReqSourceDomain\"],\n    \"specDoc\": \"cdsMetadataType is required to have /destinationDomain, /sourceDomain\"\n  },\n  {\n    \"_id\": \"_fn$ReqDestinationDomain\",\n    \"name\": \"ReqDestinationDomain\",\n    \"code\": \"(boolean (get (?s) \\\"cdsMetadataType/destinationDomain\\\"))\"\n  },\n  {\n    \"_id\": \"_fn$ReqSourceDomain\",\n    \"name\": \"ReqSourceDomain\",\n    \"code\": \"(boolean (get (?s) \\\"cdsMetadataType/sourceDomain\\\"))\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"cdsMetadataType/destinationDomain\",\n    \"type\": \"tag\",\n    \"restrictTag\": true,\n    \"doc\": \"enum: [unclassified, secret, top secret]\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"cdsMetadataType/destinationDomain:unclassified\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"cdsMetadataType/destinationDomain:secret\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"cdsMetadataType/destinationDomain:top secret\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"cdsMetadataType/sourceDomain\",\n    \"type\": \"tag\",\n    \"restrictTag\": true,\n    \"doc\": \"enum: [unclassified, secret, top secret]\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"cdsMetadataType/sourceDomain:unclassified\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"cdsMetadataType/sourceDomain:secret\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"cdsMetadataType/sourceDomain:top secret\"\n  },\n  {\n    \"_id\": \"_collection\",\n    \"name\": \"ocoMetadataType\",\n    \"spec\": [\"_fn$ReqInfrastructureUsed\", \"_fn$ReqCyberToolsUsed\"],\n    \"specDoc\": \"OcoMetadataType is required to have /infrastructureUsed, /cyberToolsUsed\"\n  },\n  {\n    \"_id\": \"_fn$ReqInfrastructureUsed\",\n    \"name\": \"ReqInfrastructureUsed\",\n    \"code\": \"(boolean (get (?s) \\\"ocoMetadataType/infrastructureUsed\\\"))\"\n  },\n  {\n    \"_id\": \"_fn$ReqCyberToolsUsed\",\n    \"name\": \"ReqCyberToolsUsed\",\n    \"code\": \"(boolean (get (?s) \\\"ocoMetadataType/cyberToolsUsed\\\"))\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"ocoMetadataType/infrastructureUsed\",\n    \"type\": \"ref\",\n    \"component\": true,\n    \"multi\": true,\n    \"restrictCollection\": \"infrastructureType\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"ocoMetadataType/cyberToolsUsed\",\n    \"type\": \"ref\",\n    \"component\": true,\n    \"multi\": true,\n    \"restrictCollection\": \"cyberToolType\"\n  },\n  {\n    \"_id\": \"_collection\",\n    \"name\": \"infrastructureType\",\n    \"spec\": [\"_fn$ReqInfrastructureId\"],\n    \"specDoc\": \"InfrastructureType is required to have /id\"\n  },\n  {\n    \"_id\": \"_fn$ReqInfrastructureId\",\n    \"name\": \"ReqInfrastructureId\",\n    \"code\": \"(boolean (get (?s) \\\"infrastructureType/id\\\"))\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"infrastructureType/id\",\n    \"type\": \"string\",\n    \"index\": true\n  },\n  {\n    \"_id\": \"_collection\",\n    \"name\": \"cyberToolType\",\n    \"spec\": [\"_fn$ReqCyberToolId\"],\n    \"specDoc\": \"cyberToolType is required to have /id\"\n  },\n  {\n    \"_id\": \"_fn$ReqCyberToolId\",\n    \"name\": \"ReqCyberToolId\",\n    \"code\": \"(boolean (get (?s) \\\"cyberToolType/id\\\"))\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"cyberToolType/id\",\n    \"type\": \"string\",\n    \"index\": true\n  },\n\n  {\n    \"_id\": \"_collection\",\n    \"name\": \"missionContext\",\n    \"spec\": [\"_fn$ReqMission\", \"_fn$ReqActivity\"],\n    \"specDoc\": \"MssionContext is required to have /mission, /activity\"\n  },\n  {\n    \"_id\": \"_fn$ReqMission\",\n    \"name\": \"ReqMission\",\n    \"code\": \"(boolean (get (?s) \\\"missionContext/mission\\\"))\"\n  },\n  {\n    \"_id\": \"_fn$ReqActivity\",\n    \"name\": \"ReqActivity\",\n    \"code\": \"(boolean (get (?s) \\\"missionContext/activity\\\"))\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"missionContext/mission\",\n    \"type\": \"ref\",\n    \"component\": true,\n    \"restrictCollection\": \"missionType\"\n  },\n  {\n    \"_id\": \"_collection\",\n    \"name\": \"missionType\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"missionType/id\",\n    \"type\": \"string\",\n    \"index\": true\n  },\n  {\n    \"_id\": \"_collection\",\n    \"name\": \"activityType\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"activityType/id\",\n    \"type\": \"string\",\n    \"index\": true\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"missionContext/activity\",\n    \"type\": \"ref\",\n    \"component\": true,\n    \"restrictCollection\": \"activityType\"\n  },\n  {\n    \"_id\": \"_collection\",\n    \"name\": \"controlSetType\",\n    \"spec\": [\n      \"_fn$ReqResourceIdentity\",\n      \"_fn$ReqCreateDateTime\",\n      \"_fn$ReqDataType\",\n      \"_fn$ReqDataSource\"\n    ],\n    \"specDoc\": \"ControlSetType is required to have /resourceIdentity, /createDateTime, /dataType, /dataSource\"\n  },\n  {\n    \"_id\": \"_fn$ReqResourceIdentity\",\n    \"name\": \"ReqResourceIdentity\",\n    \"code\": \"(boolean (get (?s) \\\"controlSetType/resourceIdentity\\\"))\"\n  },\n  {\n    \"_id\": \"_fn$ReqCreateDateTime\",\n    \"name\": \"ReqCreateDateTime\",\n    \"code\": \"(boolean (get (?s) \\\"controlSetType/createDateTime\\\"))\"\n  },\n  {\n    \"_id\": \"_fn$ReqDataType\",\n    \"name\": \"ReqDataType\",\n    \"code\": \"(boolean (get (?s) \\\"controlSetType/dataType\\\"))\"\n  },\n  {\n    \"_id\": \"_fn$ReqDataSource\",\n    \"name\": \"ReqDataSource\",\n    \"code\": \"(boolean (get (?s) \\\"controlSetType/dataSource\\\"))\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"controlSetType/resourceIdentity\",\n    \"type\": \"ref\",\n    \"component\": true,\n    \"restrictCollection\": \"resourceIdentityType\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"controlSetType/resourceDisposition\",\n    \"type\": \"ref\",\n    \"component\": true,\n    \"restrictCollection\": \"resourceDispositionType\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"controlSetType/policyReference\",\n    \"type\": \"ref\",\n    \"component\": true,\n    \"restrictCollection\": \"policyReferenceType\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"controlSetType/responsibleEntity\",\n    \"type\": \"ref\",\n    \"component\": true,\n    \"restrictCollection\": \"responsibleEntityType\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"controlSetType/createDateTime\",\n    \"type\": \"instant\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"controlSetType/dataType\",\n    \"type\": \"tag\",\n    \"restrictTag\": true,\n    \"doc\": \"enum: [Asset Compliance, Firewall Logs, Endpoint Security, Indicators, Analysis, DNS, Enrichment, IDS/IPS, Firewall/VPN, Proxy, Email Logs, Network Analysis, Windows Logs, Asset Ticketing, Asset Attribution, Session Analysis, Vulnerability Scan, Active Directory, SIEM, Access Controls, Management System, Operating System, Audit]\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Asset Compliance\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Firewall Logs\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Endpoint Security\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Indicators\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Analysis\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:DNS\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Enrichment\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:IDS-IPS\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Firewall-VPN\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Proxy\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Email Logs\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Network Analysis\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Windows Logs\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Asset Ticketing\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Asset Attribution\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Session Analysis\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Vulnerability Scan\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Active Directory\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:SIEM\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Access Controls\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Management System\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Operating System\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataType:Audit\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"controlSetType/dataSource\",\n    \"type\": \"tag\",\n    \"restrictTag\": true,\n    \"doc\": \"enum: [acas, asa-sucom, aess, ais, autofocus, blutmagie-tor, bro, cnmf-bro, cisco-joy-fingerprint-db, critical stack, crowdstrike, dns, dshield, esiem, esiem-cef, esiem-eesmg-iap, esiem-suricata-iap, esiem-wef, esiem-zeek, evtx-c, evtx-json, expanse-exposures, firehol, firewall-k, geobase, greynoise, hbss-raw, iana, itsm, jrss-cef, majestic-million, netflow, nipr-info-asn, nipr-info-ip, nipr-info-ip-summary, nvd-cpe, nvd-cve, openphish, paloalto-defender, pan-sucom, proxy, recorded futures, sccm, service-registry, spongebob-service, tanium, torexitnodes, tychon-asset-vuln, whoi]\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:acas\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:asa-sucom\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:aess\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:ais\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:autofocus\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:blutmagie-tor\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:bro\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:cnmf-bro\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:cisco-joy-fingerprint-db\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:critical stack\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:crowdstrike\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:dns\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:dshield\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:esiem\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:esiem-cef\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:esiem-eesmg-iap\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:esiem-suricata-iap\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:esiem-wef\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:esiem-zeek\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:evtx-c\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:evtx-json\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:expanse-exposures\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:firehol\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:firewall-k\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:geobase\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:greynoise\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:hbss-raw\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:iana\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:itsm\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:jrss-cef\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:majestic-million\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:netflow\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:nipr-info-asn\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:nipr-info-ip\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:nipr-info-ip-summary\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:nvd-cpe\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:nvd-cve\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:openphish\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:paloalto-defender\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:pan-sucom\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:proxy\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:recorded futures\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:sccm\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:service-registry\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:spongebob-service\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:tanium\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:torexitnodes\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:tychon-asset-vuln\"\n  },\n  {\n    \"_id\": \"_tag\",\n    \"id\": \"controlSetType/dataSource:whoi\"\n  },\n  {\n    \"_id\": \"_collection\",\n    \"name\": \"responsibleEntityType\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"responsibleEntityType/orig\",\n    \"type\": \"string\",\n    \"index\": true\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"responsibleEntityType/cust\",\n    \"type\": \"string\",\n    \"index\": true\n  },\n  {\n    \"_id\": \"_collection\",\n    \"name\": \"policyReferenceType\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"policyReferenceType/reference\",\n    \"type\": \"string\",\n    \"index\": true\n  },\n  {\n    \"_id\": \"_collection\",\n    \"name\": \"resourceDispositionType\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"resourceDispositionType/rule\",\n    \"type\": \"string\",\n    \"index\": true\n  },\n  {\n    \"_id\": \"_collection\",\n    \"name\": \"resourceIdentityType\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"resourceIdentityType/id\",\n    \"type\": \"string\",\n    \"index\": true\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"resourceIdentityType/hash\",\n    \"type\": \"ref\",\n    \"component\": true,\n    \"restrictCollection\": \"hashType\"\n  },\n  {\n    \"_id\": \"_collection\",\n    \"name\": \"hashType\",\n    \"spec\": [\"_fn$ReqHash\", \"_fn$ReqAlgorithm\"],\n    \"specDoc\": \"HashType is required to have /hash, /algorithm\"\n  },\n  {\n    \"_id\": \"_fn$ReqHash\",\n    \"name\": \"ReqHash\",\n    \"code\": \"(boolean (get (?s) \\\"hashType/hash\\\"))\"\n  },\n  {\n    \"_id\": \"_fn$ReqAlgorithm\",\n    \"name\": \"ReqAlgorithm\",\n    \"code\": \"(boolean (get (?s) \\\"hashType/algorithm\\\"))\"\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"hashType/algorithm\",\n    \"type\": \"string\",\n    \"index\": true\n  },\n  {\n    \"_id\": \"_predicate\",\n    \"name\": \"hashType/hash\",\n    \"type\": \"string\",\n    \"index\": true\n  }\n]"
      (json/parse-string true)
      vec)

  )