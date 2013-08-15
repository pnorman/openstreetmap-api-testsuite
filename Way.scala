package osmapi
/*
This file is part of openstreetmap-api-testsuite

Copyright (c) 2013 Paul Norman, released under the MIT license.

Defines various test cases for ways
*/
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import bootstrap._

object WayScenarios {
  val wayScn = scenario("Way tests")
    .group("W tests") {
      group("Accept header tests") {
        exec(
          http("*/*")
            .get("/way/3001")
            .header("Accept","*/*")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("text/*")
            .get("/way/3001")
            .header("Accept","text/*")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("text/xml")
            .get("/way/3001")
            .header("Accept","text/xml")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("invalid *")
            .get("/way/3001")
            .header("Accept","*")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("invalid josm")
            .get("/way/3001")
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
            .get("/way/asdf")
            .check(
              status.not(500),
              status.not(200),
              checks.headerCache
          ))
        .exec(
          http("Large ID")
            .get("/way/20000000000000000000") //>2^64
            .check(
              status.not(500),
              status.not(200),
              checks.headerCache
          ))
      }
      .group("Overall tests") {
        exec(
          http("osm attributes")
            .get("/way/3001")
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
            .head("/way/3001")
            .check(
              status.is(200),
              checks.isEmptyResponse,
              checks.contentType,
              checks.headerCache,
              header("Content-Length").not("0")
            ))
        .exec(
            http("missing")
            .get("/way/3000")
            .check(
              status.is(404),
              header("Content-Length").is("0"),
              checks.headerNoCache,
              checks.isEmptyResponse
            ))
        .exec(
            http("missing HEAD")
            .head("/way/3000")
            .check(
              status.is(404),
              header("Content-Length").is("0"),
              checks.headerNoCache,
              checks.isEmptyResponse
            ))
      }
      .group("Content tests") {
        exec(
          http("attributes")
            .get("/way/3001")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.way.unique(3001),
              checks.osm.way.attributes(3001, 7),
              checks.osm.way.visible(3001),
              checks.osm.way.version(3001,"1"),
              checks.osm.way.uid(3001,"3001"),
              checks.osm.way.user(3001,"3001"),
              checks.osm.way.changeset(3001,"3001"),
              checks.osm.way.children(3001,2),
              checks.osm.way.nd(3001,1,"3011"),
              checks.osm.way.nd(3001,2,"3012")
            ))
        .exec(
          http("anonymous way")
            .get("/way/3002")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.way.unique(3002),
              checks.osm.way.attributes(3002, 5),
              checks.osm.way.visible(3002),
              checks.osm.way.version(3002,"1"),
              checks.osm.way.changeset(3002,"3002"),
              checks.osm.way.children(3002,2),
              checks.osm.way.nd(3002,1,"3011"),
              checks.osm.way.nd(3002,2,"3012")
            ))
        .exec(
          http("tagged way")
            .get("/way/3003")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.way.unique(3003),
              checks.osm.way.attributes(3003, 7),
              checks.osm.way.visible(3003),
              checks.osm.way.version(3003,"1"),
              checks.osm.way.uid(3003,"3003"),
              checks.osm.way.user(3003,"3003"),
              checks.osm.way.changeset(3003,"3003"),
              checks.osm.way.children(3003,4),
              checks.osm.way.nd(3003,1,"3011"),
              checks.osm.way.nd(3003,2,"3012"),
              checks.osm.way.tag(3003,"a","1"),
              checks.osm.way.tag(3003,"b","2")
            ))
        .exec(
          http("one node way")
            .get("/way/3004")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.way.unique(3004),
              checks.osm.way.attributes(3004, 7),
              checks.osm.way.visible(3004),
              checks.osm.way.version(3004,"1"),
              checks.osm.way.uid(3004,"3004"),
              checks.osm.way.user(3004,"3004"),
              checks.osm.way.changeset(3004,"3004"),
              checks.osm.way.children(3004,1),
              checks.osm.way.nd(3004,1,"3011")
            )) 
      }
    }

    val wayDiffScn = scenario("Way diff tests")
    .group("W diff tests") {
      group("Status tests") {
        exec(
          http("recreated")
            .get("/way/4002")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.way.unique(4002),
              checks.osm.way.attributes(4002, 7),
              checks.osm.way.visible(4002),
              checks.osm.way.version(4002,"3"),
              checks.osm.way.uid(4002,"4202"),
              checks.osm.way.user(4002,"4202"),
              checks.osm.way.changeset(4002,"4202"),
              checks.osm.way.children(4002,2),
              checks.osm.way.nd(4002,1,"4013"),
              checks.osm.way.nd(4002,2,"4014")
            ))
        .exec(
          http("recreated as untagged")
            .get("/way/4004")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.way.unique(4004),
              checks.osm.way.attributes(4004, 7),
              checks.osm.way.visible(4004),
              checks.osm.way.version(4004,"3"),
              checks.osm.way.uid(4004,"4204"),
              checks.osm.way.user(4004,"4204"),
              checks.osm.way.changeset(4004,"4204"),
              checks.osm.way.children(4004,2),
              checks.osm.way.nd(4004,1,"4013"),
              checks.osm.way.nd(4004,2,"4014")
            ))
        .exec(
          http("diff created")
            .get("/way/4005")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.way.unique(4005),
              checks.osm.way.attributes(4005, 7),
              checks.osm.way.visible(4005),
              checks.osm.way.version(4005,"1"),
              checks.osm.way.uid(4005,"4205"),
              checks.osm.way.user(4005,"4205"),
              checks.osm.way.changeset(4005,"4205"),
              checks.osm.way.children(4005,2),
              checks.osm.way.nd(4005,1,"4013"),
              checks.osm.way.nd(4005,2,"4014")
            ))
        .exec(
          http("diff created tagged")
            .get("/way/4006")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.way.unique(4006),
              checks.osm.way.attributes(4006, 7),
              checks.osm.way.visible(4006),
              checks.osm.way.version(4006,"1"),
              checks.osm.way.uid(4006,"4206"),
              checks.osm.way.user(4006,"4206"),
              checks.osm.way.changeset(4006,"4206"),
              checks.osm.way.children(4006,4),
              checks.osm.way.nd(4006,1,"4013"),
              checks.osm.way.nd(4006,2,"4014"),
              checks.osm.way.tag(4006,"a","1"),
              checks.osm.way.tag(4006,"b","2")
            ))
      }
    }
    .group("W history tests") {
        exec(
          http("deleted")
            .get("/way/4001")
            .check(
              checks.isEmptyResponse,
              header("Content-Length").is("0"),
              checks.headerCache,
              status.is(410)
            ))
        .exec(
          http("deleted HEAD")
            .head("/way/4001")
            .check(
              checks.isEmptyResponse,
              header("Content-Length").is("0"),
              checks.headerCache,
              status.is(410)
            ))
        .exec(
          http("deleted tagged")
            .get("/way/4003")
            .check(
              checks.isEmptyResponse,
              header("Content-Length").is("0"),
              checks.headerCache,
              status.is(410)
            ))
        .exec(
          http("deleted tagged HEAD")
            .head("/way/4003")
            .check(
              checks.isEmptyResponse,
              header("Content-Length").is("0"),
              checks.headerCache,
              status.is(410)
            ))
    }
}
