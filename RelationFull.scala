package osmapi
/*
This file is part of openstreetmap-api-testsuite

Copyright (c) 2013 Paul Norman, released under the MIT license.

Defines various test cases for relations.

Uses data from /relation/id# for diff tests

These tests concentrate on testing response codes and that the right 
objects are emitted, not all of the details of serialization under 
the assumption that the code is shared with the more basic calls.
*/
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import bootstrap._

object RelationFullScenarios {
  val relationFullScn = scenario("Relation Full tests")
    .group("RF tests") {
      group("Accept header tests") {
        exec(
          http("*/*")
            .get("/relation/8001/full")
            .header("Accept","*/*")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("text/*")
            .get("/relation/8001/full")
            .header("Accept","text/*")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("text/xml")
            .get("/relation/8001/full")
            .header("Accept","text/xml")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("invalid *")
            .get("/relation/8001/full")
            .header("Accept","*")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("invalid josm")
            .get("/relation/8001/full")
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
            .get("/relation/asdf/full")
            .check(
              status.not(500),
              status.not(200),
              checks.headerCache
          ))
        .exec(
          http("Large ID")
            .get("/relation/20000000000000000000/full") //>2^64
            .check(
              status.not(500),
              status.not(200),
              checks.headerCache
          ))
      }
      .group("Overall tests") {
        exec(
          http("osm attributes")
            .get("/relation/8001/full")
            .check(
              status.is(200),
              checks.rootIsOsm,
              checks.osm.version,
              checks.osm.copyright,
              checks.osm.attribution,
              checks.osm.license
            ))
        .exec(
            http("existing HEAD")
            .head("/relation/8001/full")
            .check(
              status.is(200),
              checks.isEmptyResponse,
              checks.contentType,
              checks.headerCache,
              header("Content-Length").not("0")
            ))
        .exec(
            http("missing")
            .get("/relation/8000/full")
            .check(
              status.is(404),
              header("Content-Length").is("0"),
              checks.headerNoCache,
              checks.isEmptyResponse
            ))
        .exec(
            http("missing HEAD")
            .head("/relation/8000/full")
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
            .get("/relation/8001/full")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.objects(2),
              checks.osm.relation.unique(8001),
              checks.osm.relation.member.t(8001,1,"node"),
              checks.osm.relation.member.ref(8001,1,"8001"),
              checks.osm.relation.member.role(8001,1,""),
              checks.osm.node.unique(8001)
            ))
        .exec(
          http("way member")
            .get("/relation/8002/full")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.objects(4),
              checks.osm.relation.unique(8002),
              checks.osm.relation.member.t(8002,1,"way"),
              checks.osm.relation.member.ref(8002,1,"8001"),
              checks.osm.relation.member.role(8002,1,""),
              checks.osm.way.unique(8001),
              checks.osm.way.nd(8001,1,"8002"),
              checks.osm.way.nd(8001,2,"8003"),
              checks.osm.node.unique(8002),
              checks.osm.node.unique(8003)
            ))
        .exec(
          http("relation member")
            .get("/relation/8003/full")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.objects(2),
              checks.osm.relation.unique(8003),
              checks.osm.relation.member.t(8003,1,"relation"),
              checks.osm.relation.member.ref(8003,1,"8001"),
              checks.osm.relation.member.role(8003,1,""),
              checks.osm.relation.unique(8001),
              checks.osm.relation.member.t(8001,1,"node"),
              checks.osm.relation.member.ref(8001,1,"8001"),
              checks.osm.relation.member.role(8001,1,"")
            ))
        .exec(
          http("relation non-recursion")
            .get("/relation/8004/full")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.objects(3),
              checks.osm.relation.unique(8004),
              checks.osm.relation.member.t(8004,1,"relation"),
              checks.osm.relation.member.ref(8004,1,"8001"),
              checks.osm.relation.member.role(8004,1,""),
              checks.osm.relation.member.t(8004,2,"relation"),
              checks.osm.relation.member.ref(8004,2,"8002"),
              checks.osm.relation.member.role(8004,2,""),
              checks.osm.relation.unique(8001),
              checks.osm.relation.member.t(8001,1,"node"),
              checks.osm.relation.member.ref(8001,1,"8001"),
              checks.osm.relation.member.role(8001,1,""),
              checks.osm.relation.unique(8002),
              checks.osm.relation.member.t(8002,1,"way"),
              checks.osm.relation.member.ref(8002,1,"8001"),
              checks.osm.relation.member.role(8002,1,"")
            ))
      }
    }

    val wayFullDiffScn = scenario("Relation Full diff tests")
    .group("RF diff tests") {
      group("Status tests") {
        exec(
          http("recreated")
            .get("/relation/6002/full")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.objects(2),
              checks.osm.relation.unique(6002),
              checks.osm.relation.version(6002,"2"),
              checks.osm.relation.version(6002,"3"),
              checks.osm.relation.children(6002,1),
              checks.osm.relation.member.t(6002,1,"node"),
              checks.osm.relation.member.ref(6002,1,"6002"),
              checks.osm.relation.member.role(6002,1,""),
              checks.osm.node.unique(6002)
            ))
      }
    }
    .group("WF history tests") {
        exec(
          http("deleted")
            .get("/way/4001/full")
            .check(
              checks.isEmptyResponse,
              header("Content-Length").is("0"),
              checks.headerCache,
              status.is(410)
            ))
        .exec(
          http("deleted HEAD")
            .head("/way/4001/full")
            .check(
              checks.isEmptyResponse,
              header("Content-Length").is("0"),
              checks.headerCache,
              status.is(410)
            ))
    }
}
