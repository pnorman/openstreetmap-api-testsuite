package osmapi
/*
This file is part of openstreetmap-api-testsuite

Copyright (c) 2013 Paul Norman, released under the MIT license.

Defines various test cases for ways.

Uses data from /way/id# for diff tests

These tests concentrate on testing response codes and that the right 
objects are emitted, not all of the details of serialization under 
the assumption that the code is shared with the more basic calls.
*/
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import bootstrap._

object WayFullScenarios {
  val wayFullScn = scenario("Way Full tests")
    .group("WF tests") {
      group("Accept header tests") {
        exec(
          http("*/*")
            .get("/way/7001/full")
            .header("Accept","*/*")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("text/*")
            .get("/way/7001/full")
            .header("Accept","text/*")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("text/xml")
            .get("/way/7001/full")
            .header("Accept","text/xml")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("invalid *")
            .get("/way/7001/full")
            .header("Accept","*")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("invalid josm")
            .get("/way/7001/full")
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
            .get("/way/asdf/full")
            .check(
              status.not(500),
              status.not(200),
              checks.headerCache
          ))
        .exec(
          http("Large ID")
            .get("/way/20000000000000000000/full") //>2^64
            .check(
              status.not(500),
              status.not(200),
              checks.headerCache
          ))
      }
      .group("Overall tests") {
        exec(
          http("osm attributes")
            .get("/way/7001/full")
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
            .head("/way/7001/full")
            .check(
              status.is(200),
              checks.isEmptyResponse,
              checks.contentType,
              checks.headerCache,
              header("Content-Length").not("0")
            ))
        .exec(
            http("missing")
            .get("/way/7000/full")
            .check(
              status.is(404),
              header("Content-Length").is("0"),
              checks.headerNoCache,
              checks.isEmptyResponse
            ))
        .exec(
            http("missing HEAD")
            .head("/way/7000/full")
            .check(
              status.is(404),
              header("Content-Length").is("0"),
              checks.headerNoCache,
              checks.isEmptyResponse
            ))
      }
      .group("Content tests") {
        exec(
          http("untagged way untagged nodes")
            .get("/way/7001/full")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.objects(3),
              checks.osm.way.unique(7001),
              checks.osm.way.attributes(7001, 7),
              checks.osm.way.visible(7001),
              checks.osm.way.version(7001,"1"),
              checks.osm.way.uid(7001,"7001"),
              checks.osm.way.user(7001,"7001"),
              checks.osm.way.changeset(7001,"7001"),
              checks.osm.way.children(7001,2),
              checks.osm.way.nd(7001,1,"7001"),
              checks.osm.way.nd(7001,2,"7002"),
              checks.osm.node.unique(7001),
              checks.osm.node.attributes(7001, 9),
              checks.osm.node.visible(7001),
              checks.osm.node.version(7001,"1"),
              checks.osm.node.uid(7001,"7001"),
              checks.osm.node.user(7001,"7001"),
              checks.osm.node.changeset(7001,"7001"),
              checks.osm.node.lat(7001,"1.001"),
              checks.osm.node.lon(7001,"1.601"),
              checks.osm.node.children(7001,0),
              checks.osm.node.unique(7002),
              checks.osm.node.attributes(7002, 9),
              checks.osm.node.visible(7002),
              checks.osm.node.version(7002,"1"),
              checks.osm.node.uid(7002,"7002"),
              checks.osm.node.user(7002,"7002"),
              checks.osm.node.changeset(7002,"7002"),
              checks.osm.node.lat(7002,"1.001"),
              checks.osm.node.lon(7002,"1.602"),
              checks.osm.node.children(7002,0)
            ))
        .exec(
          http("untagged way tagged node")
            .get("/way/7002/full")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.objects(3),
              checks.osm.way.unique(7002),
              checks.osm.way.attributes(7002, 7),
              checks.osm.way.visible(7002),
              checks.osm.way.version(7002,"1"),
              checks.osm.way.uid(7002,"7002"),
              checks.osm.way.user(7002,"7002"),
              checks.osm.way.changeset(7002,"7002"),
              checks.osm.way.children(7002,2),
              checks.osm.way.nd(7002,1,"7002"),
              checks.osm.way.nd(7002,2,"7003"),
              checks.osm.node.unique(7002),
              checks.osm.node.attributes(7002, 9),
              checks.osm.node.visible(7002),
              checks.osm.node.version(7002,"1"),
              checks.osm.node.uid(7002,"7002"),
              checks.osm.node.user(7002,"7002"),
              checks.osm.node.changeset(7002,"7002"),
              checks.osm.node.lat(7002,"1.001"),
              checks.osm.node.lon(7002,"1.602"),
              checks.osm.node.children(7002,0),
              checks.osm.node.unique(7003),
              checks.osm.node.attributes(7003, 9),
              checks.osm.node.visible(7003),
              checks.osm.node.version(7003,"1"),
              checks.osm.node.uid(7003,"7003"),
              checks.osm.node.user(7003,"7003"),
              checks.osm.node.changeset(7003,"7003"),
              checks.osm.node.lat(7003,"1.001"),
              checks.osm.node.lon(7003,"1.603"),
              checks.osm.node.children(7003,1),
              checks.osm.node.tag(7003,"a","1")
            ))
        exec(
          http("tagged way untagged nodes")
            .get("/way/7003/full")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.objects(3),
              checks.osm.way.unique(7003),
              checks.osm.way.attributes(7003, 7),
              checks.osm.way.visible(7003),
              checks.osm.way.version(7003,"1"),
              checks.osm.way.uid(7003,"7003"),
              checks.osm.way.user(7003,"7003"),
              checks.osm.way.changeset(7003,"7003"),
              checks.osm.way.tag(7003,"x","0"),
              checks.osm.way.children(7003,3),
              checks.osm.way.nd(7003,1,"7001"),
              checks.osm.way.nd(7003,2,"7002"),
              checks.osm.node.unique(7001),
              checks.osm.node.attributes(7001, 9),
              checks.osm.node.visible(7001),
              checks.osm.node.version(7001,"1"),
              checks.osm.node.uid(7001,"7001"),
              checks.osm.node.user(7001,"7001"),
              checks.osm.node.changeset(7001,"7001"),
              checks.osm.node.lat(7001,"1.001"),
              checks.osm.node.lon(7001,"1.601"),
              checks.osm.node.children(7001,0),
              checks.osm.node.unique(7002),
              checks.osm.node.attributes(7002, 9),
              checks.osm.node.visible(7002),
              checks.osm.node.version(7002,"1"),
              checks.osm.node.uid(7002,"7002"),
              checks.osm.node.user(7002,"7002"),
              checks.osm.node.changeset(7002,"7002"),
              checks.osm.node.lat(7002,"1.001"),
              checks.osm.node.lon(7002,"1.602"),
              checks.osm.node.children(7002,0)
            ))
        .exec(
          http("tagged way tagged node")
            .get("/way/7004/full")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.objects(3),
              checks.osm.way.unique(7004),
              checks.osm.way.attributes(7004, 7),
              checks.osm.way.visible(7004),
              checks.osm.way.version(7004,"1"),
              checks.osm.way.uid(7004,"7004"),
              checks.osm.way.user(7004,"7004"),
              checks.osm.way.changeset(7004,"7004"),
              checks.osm.way.children(7004,3),
              checks.osm.way.tag(7004,"x","0"),
              checks.osm.way.nd(7004,1,"7002"),
              checks.osm.way.nd(7004,2,"7003"),
              checks.osm.node.unique(7002),
              checks.osm.node.attributes(7002, 9),
              checks.osm.node.visible(7002),
              checks.osm.node.version(7002,"1"),
              checks.osm.node.uid(7002,"7002"),
              checks.osm.node.user(7002,"7002"),
              checks.osm.node.changeset(7002,"7002"),
              checks.osm.node.lat(7002,"1.001"),
              checks.osm.node.lon(7002,"1.602"),
              checks.osm.node.children(7002,0),
              checks.osm.node.unique(7003),
              checks.osm.node.attributes(7003, 9),
              checks.osm.node.visible(7003),
              checks.osm.node.version(7003,"1"),
              checks.osm.node.uid(7003,"7003"),
              checks.osm.node.user(7003,"7003"),
              checks.osm.node.changeset(7003,"7003"),
              checks.osm.node.lat(7003,"1.001"),
              checks.osm.node.lon(7003,"1.603"),
              checks.osm.node.children(7003,1),
              checks.osm.node.tag(7003,"a","1")
            ))
        .exec(
          http("way with repeating nodes")
            .get("/way/7005/full")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.objects(3),
              checks.osm.way.unique(7005),
              checks.osm.way.attributes(7005, 7),
              checks.osm.way.visible(7005),
              checks.osm.way.version(7005,"1"),
              checks.osm.way.uid(7005,"7005"),
              checks.osm.way.user(7005,"7005"),
              checks.osm.way.changeset(7005,"7005"),
              checks.osm.way.children(7005,3),
              checks.osm.way.nd(7005,1,"7001"),
              checks.osm.way.nd(7005,2,"7002"),
              checks.osm.way.nd(7005,3,"7001"),
              checks.osm.node.unique(7001),
              checks.osm.node.attributes(7001, 9),
              checks.osm.node.visible(7001),
              checks.osm.node.version(7001,"1"),
              checks.osm.node.uid(7001,"7001"),
              checks.osm.node.user(7001,"7001"),
              checks.osm.node.changeset(7001,"7001"),
              checks.osm.node.lat(7001,"1.001"),
              checks.osm.node.lon(7001,"1.601"),
              checks.osm.node.children(7001,0),
              checks.osm.node.unique(7002),
              checks.osm.node.attributes(7002, 9),
              checks.osm.node.visible(7002),
              checks.osm.node.version(7002,"1"),
              checks.osm.node.uid(7002,"7002"),
              checks.osm.node.user(7002,"7002"),
              checks.osm.node.changeset(7002,"7002"),
              checks.osm.node.lat(7002,"1.001"),
              checks.osm.node.lon(7002,"1.602"),
              checks.osm.node.children(7002,0)
            ))
      }
    }

    val wayFullDiffScn = scenario("Way Full diff tests")
    .group("WF diff tests") {
      group("Status tests") {
        exec(
          http("recreated")
            .get("/way/4002/full")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.objects(3),
              checks.osm.way.unique(4002),
              checks.osm.way.attributes(4002, 7),
              checks.osm.way.visible(4002),
              checks.osm.way.version(4002,"3"),
              checks.osm.way.uid(4002,"4202"),
              checks.osm.way.user(4002,"4202"),
              checks.osm.way.changeset(4002,"4202"),
              checks.osm.way.children(4002,2),
              checks.osm.way.nd(4002,1,"4013"),
              checks.osm.way.nd(4002,2,"4014"),
              checks.osm.node.unique(4013),
              checks.osm.node.unique(4014)
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
