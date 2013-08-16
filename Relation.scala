package osmapi
/*
This file is part of openstreetmap-api-testsuite

Copyright (c) 2013 Paul Norman, released under the MIT license.

Defines various test cases for relations
*/
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import bootstrap._

object RelationScenarios {
  val relationScn = scenario("Relation tests")
    .group("R tests") {
      group("Accept header tests") {
        exec(
          http("*/*")
            .get("/relation/5001")
            .header("Accept","*/*")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("text/*")
            .get("/relation/5001")
            .header("Accept","text/*")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("text/xml")
            .get("/relation/5001")
            .header("Accept","text/xml")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("invalid *")
            .get("/relation/5001")
            .header("Accept","*")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("invalid josm")
            .get("/relation/5001")
            .header("Accept","text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
      }
      .group("Syntax tests") {
        exec(
          http("Invalid request")
            .get("/relation/asdf")
            .check(
              status.not(500),
              status.not(200),
              checks.headerCache
          ))
        .exec(
          http("Large ID")
            .get("/relation/20000000000000000000") //>2^64
            .check(
              status.not(500),
              status.not(200),
              checks.headerCache
          ))
      }
      .group("Overall tests") {
        exec(
          http("osm attributes")
            .get("/relation/5001")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.version,
              checks.osm.copyright,
              checks.osm.attribution,
              checks.osm.license
            ))
        .exec(
            http("existing HEAD")
            .head("/relation/5001")
            .check(
              status.is(200),
              checks.isEmptyResponse,
              checks.contentType,
              checks.headerCache,
              header("Content-Length").not("0")
            ))
        .exec(
            http("missing")
            .get("/relation/5000")
            .check(
              status.is(404),
              checks.headerNoCache,
              checks.isEmptyResponse,
              header("Content-Length").is("0")
            ))
        .exec(
            http("missing HEAD")
            .head("/relation/5000")
            .check(
              status.is(404),
              header("Content-Length").is("0"),
              checks.headerNoCache,
              checks.isEmptyResponse
            ))
      }
      .group("Content tests") {
        exec(
          http("node member")
            .get("/relation/5001")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.relation.unique(5001),
              checks.osm.relation.attributes(5001, 7),
              checks.osm.relation.visible(5001),
              checks.osm.relation.version(5001,"1"),
              checks.osm.relation.uid(5001,"5001"),
              checks.osm.relation.user(5001,"5001"),
              checks.osm.relation.changeset(5001,"5001"),
              checks.osm.relation.children(5001,1),
              checks.osm.relation.member.t(5001,1,"node"),
              checks.osm.relation.member.ref(5001,1,"5001"),
              checks.osm.relation.member.role(5001,1,"")
            ))
        .exec(
          http("way member")
            .get("/relation/5002")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.relation.unique(5002),
              checks.osm.relation.attributes(5002, 7),
              checks.osm.relation.visible(5002),
              checks.osm.relation.version(5002,"1"),
              checks.osm.relation.uid(5002,"5002"),
              checks.osm.relation.user(5002,"5002"),
              checks.osm.relation.changeset(5002,"5002"),
              checks.osm.relation.children(5002,1),
              checks.osm.relation.member.t(5002,1,"way"),
              checks.osm.relation.member.ref(5002,1,"5001"),
              checks.osm.relation.member.role(5002,1,"")
            ))
        .exec(
          http("relation member")
            .get("/relation/5003")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.relation.unique(5003),
              checks.osm.relation.attributes(5003, 7),
              checks.osm.relation.visible(5003),
              checks.osm.relation.version(5003,"1"),
              checks.osm.relation.uid(5003,"5003"),
              checks.osm.relation.user(5003,"5003"),
              checks.osm.relation.changeset(5003,"5003"),
              checks.osm.relation.children(5003,1),
              checks.osm.relation.member.t(5003,1,"relation"),
              checks.osm.relation.member.ref(5003,1,"5001"),
              checks.osm.relation.member.role(5003,1,"")
            ))
        .exec(
          http("no members")
            .get("/relation/5004")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.relation.unique(5004),
              checks.osm.relation.attributes(5004, 7),
              checks.osm.relation.visible(5004),
              checks.osm.relation.version(5004,"1"),
              checks.osm.relation.uid(5004,"5004"),
              checks.osm.relation.user(5004,"5004"),
              checks.osm.relation.changeset(5004,"5004"),
              checks.osm.relation.children(5004,0)
            ))
        .exec(
          http("self-referential")
            .get("/relation/5005")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.relation.unique(5005),
              checks.osm.relation.attributes(5005, 7),
              checks.osm.relation.visible(5005),
              checks.osm.relation.version(5005,"2"),
              checks.osm.relation.uid(5005,"5005"),
              checks.osm.relation.user(5005,"5005"),
              checks.osm.relation.changeset(5005,"5005"),
              checks.osm.relation.children(5005,1),
              checks.osm.relation.member.t(5005,1,"relation"),
              checks.osm.relation.member.ref(5005,1,"5005"),
              checks.osm.relation.member.role(5005,1,"")
            ))
        .exec(
          http("roles")
            .get("/relation/5006")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.relation.unique(5006),
              checks.osm.relation.attributes(5006, 7),
              checks.osm.relation.visible(5006),
              checks.osm.relation.version(5006,"1"),
              checks.osm.relation.uid(5006,"5006"),
              checks.osm.relation.user(5006,"5006"),
              checks.osm.relation.changeset(5006,"5006"),
              checks.osm.relation.children(5006,3),
              checks.osm.relation.member.t(5006,1,"node"),
              checks.osm.relation.member.ref(5006,1,"5001"),
              checks.osm.relation.member.role(5006,1,"a"),
              checks.osm.relation.member.t(5006,2,"node"),
              checks.osm.relation.member.ref(5006,2,"5002"),
              checks.osm.relation.member.role(5006,2,"b"),
              checks.osm.relation.member.t(5006,3,"node"),
              checks.osm.relation.member.ref(5006,3,"5003"),
              checks.osm.relation.member.role(5006,3,"c")
            ))
        .exec(
          http("roles alternate")
            .get("/relation/5007")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.relation.unique(5007),
              checks.osm.relation.attributes(5007, 7),
              checks.osm.relation.visible(5007),
              checks.osm.relation.version(5007,"1"),
              checks.osm.relation.uid(5007,"5007"),
              checks.osm.relation.user(5007,"5007"),
              checks.osm.relation.changeset(5007,"5007"),
              checks.osm.relation.children(5007,3),
              checks.osm.relation.member.t(5007,1,"node"),
              checks.osm.relation.member.ref(5007,1,"5002"),
              checks.osm.relation.member.role(5007,1,"b"),
              checks.osm.relation.member.t(5007,2,"node"),
              checks.osm.relation.member.ref(5007,2,"5001"),
              checks.osm.relation.member.role(5007,2,"a"),
              checks.osm.relation.member.t(5007,3,"node"),
              checks.osm.relation.member.ref(5007,3,"5003"),
              checks.osm.relation.member.role(5007,3,"c")
            ))
        .exec(
          http("anonymous")
            .get("/relation/5008")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.relation.unique(5008),
              checks.osm.relation.attributes(5008, 5),
              checks.osm.relation.visible(5008),
              checks.osm.relation.version(5008,"1"),
              checks.osm.relation.changeset(5008,"5008"),
              checks.osm.relation.children(5008,1),
              checks.osm.relation.member.t(5008,1,"node"),
              checks.osm.relation.member.ref(5008,1,"5001"),
              checks.osm.relation.member.role(5008,1,"")
            ))
        .exec(
          http("tagged")
            .get("/relation/5009")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.relation.unique(5009),
              checks.osm.relation.attributes(5009, 7),
              checks.osm.relation.visible(5009),
              checks.osm.relation.version(5009,"1"),
              checks.osm.relation.uid(5009,"5009"),
              checks.osm.relation.user(5009,"5009"),
              checks.osm.relation.changeset(5009,"5009"),
              checks.osm.relation.children(5009,2),
              checks.osm.relation.tag(5009,"a","1"),
              checks.osm.relation.member.t(5009,1,"node"),
              checks.osm.relation.member.ref(5009,1,"5001"),
              checks.osm.relation.member.role(5009,1,"")
            ))
      }
    }
  val relationDiffScn = scenario("Relation diff tests")
    .group("R diff tests") {
      group("Content tests") {
        exec(
          http("recreated")
            .get("/relation/6002")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.relation.unique(6002),
              checks.osm.relation.attributes(6002, 7),
              checks.osm.relation.visible(6002),
              checks.osm.relation.version(6002,"3"),
              checks.osm.relation.uid(6002,"6202"),
              checks.osm.relation.user(6002,"6202"),
              checks.osm.relation.changeset(6002,"6202"),
              checks.osm.relation.children(6002,1),
              checks.osm.relation.member.t(6002,1,"node"),
              checks.osm.relation.member.ref(6002,1,"6002"),
              checks.osm.relation.member.role(6002,1,"")
            ))
        .exec(
          http("recreated as untagged")
            .get("/relation/6004")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.relation.unique(6004),
              checks.osm.relation.attributes(6004, 7),
              checks.osm.relation.visible(6004),
              checks.osm.relation.version(6004,"3"),
              checks.osm.relation.uid(6004,"6204"),
              checks.osm.relation.user(6004,"6204"),
              checks.osm.relation.changeset(6004,"6204"),
              checks.osm.relation.children(6004,1),
              checks.osm.relation.member.t(6004,1,"node"),
              checks.osm.relation.member.ref(6004,1,"6002"),
              checks.osm.relation.member.role(6004,1,"")
            ))
        .exec(
          http("diff created")
            .get("/relation/6005")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.relation.unique(6005),
              checks.osm.relation.attributes(6005, 7),
              checks.osm.relation.visible(6005),
              checks.osm.relation.version(6005,"1"),
              checks.osm.relation.uid(6005,"6205"),
              checks.osm.relation.user(6005,"6205"),
              checks.osm.relation.changeset(6005,"6205"),
              checks.osm.relation.children(6005,2),
              checks.osm.relation.member.t(6005,1,"node"),
              checks.osm.relation.member.ref(6005,1,"6001"),
              checks.osm.relation.member.role(6005,1,""),
              checks.osm.relation.member.t(6005,2,"node"),
              checks.osm.relation.member.ref(6005,2,"6002"),
              checks.osm.relation.member.role(6005,2,"")
            ))
        .exec(
          http("diff created")
            .get("/relation/6006")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.relation.unique(6006),
              checks.osm.relation.attributes(6006, 7),
              checks.osm.relation.visible(6006),
              checks.osm.relation.version(6006,"1"),
              checks.osm.relation.uid(6006,"6206"),
              checks.osm.relation.user(6006,"6206"),
              checks.osm.relation.changeset(6006,"6206"),
              checks.osm.relation.children(6006,3),
              checks.osm.relation.tag(6006,"b","2"),
              checks.osm.relation.member.t(6006,1,"node"),
              checks.osm.relation.member.ref(6006,1,"6001"),
              checks.osm.relation.member.role(6006,1,"baz"),
              checks.osm.relation.member.t(6006,2,"node"),
              checks.osm.relation.member.ref(6006,2,"6002"),
              checks.osm.relation.member.role(6006,2,"brr")
            ))
      }
    }
    .group("R history tests") {
      exec(
          http("deleted")
            .get("/relation/6001")
            .check(
              checks.isEmptyResponse,
              header("Content-Length").is("0"),
              checks.headerCache,
              status.is(410)
            ))
        .exec(
          http("deleted HEAD")
            .head("/relation/6001")
            .check(
              checks.isEmptyResponse,
              header("Content-Length").is("0"),
              checks.headerCache,
              status.is(410)
            ))
        .exec(
          http("deleted tagged")
            .get("/relation/6003")
            .check(
              checks.isEmptyResponse,
              header("Content-Length").is("0"),
              checks.headerCache,
              status.is(410)
            ))
        .exec(
          http("deleted tagged HEAD")
            .head("/relation/6003")
            .check(
              checks.isEmptyResponse,
              header("Content-Length").is("0"),
              checks.headerCache,
              status.is(410)
            ))
    }
}
