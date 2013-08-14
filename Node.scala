package osmapi
/*
This file is part of openstreetmap-api-testsuite

Copyright (c) 2013 Paul Norman, released under the MIT license.

Defines various test cases for nodes
*/
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import bootstrap._

object NodeScenarios {
  def stripZero = (s:Option[String]) => s.map(_.replaceAll("0+$",""))
     
  val nodeScn = scenario("Node tests")
    .group("N tests") {
      group("Accept header tests") {
        exec(
          http("*/*")
            .get("/node/1001")
            .header("Accept","*/*")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("text/*")
            .get("/node/1001")
            .header("Accept","text/*")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("text/xml")
            .get("/node/1001")
            .header("Accept","text/xml")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("invalid *")
            .get("/node/1001")
            .header("Accept","*")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("invalid josm")
            .get("/node/1001")
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
            .get("/node/asdf")
            .check(
              status.not(500),
              status.not(200),
              checks.headerCache
          ))
        .exec(
          http("Large node ID")
            .get("/node/20000000000000000000") //>2^64
            .check(
              status.not(500),
              status.not(200),
              checks.headerCache
          ))
      }
      .group("Overall tests") {
        exec(
          http("osm attributes")
            .get("/node/1001")
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
            .head("/node/1001")
            .check(
              status.is(200),
              checks.isEmptyResponse,
              checks.contentType,
              checks.headerCache,
              header("Content-Length").not("0")
            ))
        .exec(
            http("missing")
            .get("/node/1000")
            .check(
              status.is(404),
              checks.headerNoCache,
              checks.isEmptyResponse,
              header("Content-Length").is("0")
            ))
        .exec(
            http("missing HEAD")
            .head("/node/1000")
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
            .get("/node/1001")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.node.unique(1001),
              checks.osm.node.attributes(1001, 9),
              checks.osm.node.visible(1001),
              checks.osm.node.version(1001,"1"),
              checks.osm.node.uid(1001,"1001"),
              checks.osm.node.user(1001,"1001"),
              checks.osm.node.changeset(1001,"1001"),
              checks.osm.node.lat(1001,"1.001"),
              checks.osm.node.lon(1001,"1.001")
            ))
        .exec(
          http("different attributes")
            .get("/node/1002")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.node.unique(1002),
              checks.osm.node.attributes(1002, 9),
              checks.osm.node.visible(1002),
              checks.osm.node.version(1002,"2"),
              checks.osm.node.uid(1002,"1002"),
              checks.osm.node.user(1002,"1002"),
              checks.osm.node.changeset(1002,"1002"),
              checks.osm.node.lat(1002,"1.002"),
              checks.osm.node.lon(1002,"1.001")
            ))
        .exec(
          http("anonymous user")
            .get("/node/1003")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.node.unique(1003),
              checks.osm.node.attributes(1003, 7),
              checks.osm.node.visible(1003),
              checks.osm.node.version(1003,"1"),
              checks.osm.node.changeset(1003,"1003"),
              checks.osm.node.lat(1003,"1.003"),
              checks.osm.node.lon(1003,"1.001")
            ))
        .exec(
          http("one tag")
            .get("/node/1004")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.node.unique(1004),
              checks.osm.node.attributes(1004, 9),
              checks.osm.node.visible(1004),
              checks.osm.node.version(1004,"1"),
              checks.osm.node.uid(1004,"1001"),
              checks.osm.node.user(1004,"1001"),
              checks.osm.node.changeset(1004,"1001"),
              checks.osm.node.lat(1004,"1.004"),
              checks.osm.node.lon(1004,"1.001"),
              checks.osm.node.children(1004,1),
              checks.osm.node.tag(1004,"a","1")
            ))
        .exec(
          http("multiple tags")
            .get("/node/1005")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.node.unique(1005),
              checks.osm.node.attributes(1005, 9),
              checks.osm.node.visible(1005),
              checks.osm.node.version(1005,"1"),
              checks.osm.node.uid(1005,"1001"),
              checks.osm.node.user(1005,"1001"),
              checks.osm.node.changeset(1005,"1001"),
              checks.osm.node.lat(1005,"1.005"),
              checks.osm.node.lon(1005,"1.001"),
              checks.osm.node.children(1005,2),
              checks.osm.node.tag(1005,"a","1"),
              checks.osm.node.tag(1005,"b","2")
            ))
        .exec(
          http("different tag order")
            .get("/node/1006")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.node.unique(1006),
              checks.osm.node.attributes(1006, 9),
              checks.osm.node.visible(1006),
              checks.osm.node.version(1006,"1"),
              checks.osm.node.uid(1006,"1001"),
              checks.osm.node.user(1006,"1001"),
              checks.osm.node.changeset(1006,"1001"),
              checks.osm.node.lat(1006,"1.006"),
              checks.osm.node.lon(1006,"1.001"),
              checks.osm.node.children(1006,2),
              checks.osm.node.tag(1006,"a","1"),
              checks.osm.node.tag(1006,"b","2")
            ))
      }
    }

  val nodeDiffScn = scenario("Node diff tests")
    .group("N diff tests") {
      group("Content tests") {
        exec(
          http("recreated")
            .get("/node/2002")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.node.unique(2002),
              checks.osm.node.attributes(2002, 9),
              checks.osm.node.visible(2002),
              checks.osm.node.version(2002,"3"),
              checks.osm.node.uid(2002,"2202"),
              checks.osm.node.user(2002,"2202"),
              checks.osm.node.changeset(2002,"2202"),
              checks.osm.node.lat(2002,"1.001"),
              checks.osm.node.lon(2002,"1.102")
            ))
        .exec(
          http("recreated as untagged")
            .get("/node/2004")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.node.unique(2004),
              checks.osm.node.attributes(2004, 9),
              checks.osm.node.visible(2004),
              checks.osm.node.version(2004,"3"),
              checks.osm.node.uid(2004,"2204"),
              checks.osm.node.user(2004,"2204"),
              checks.osm.node.changeset(2004,"2204"),
              checks.osm.node.lat(2004,"1.001"),
              checks.osm.node.lon(2004,"1.104"),
              checks.osm.node.children(2004,0) // untagged
            ))
        .exec(
          http("diff created")
            .get("/node/2005")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.node.unique(2005),
              checks.osm.node.attributes(2005, 9),
              checks.osm.node.visible(2005),
              checks.osm.node.version(2005,"1"),
              checks.osm.node.uid(2005,"2005"),
              checks.osm.node.user(2005,"2005"),
              checks.osm.node.changeset(2005,"2005"),
              checks.osm.node.lat(2005,"1.001"),
              checks.osm.node.lon(2005,"1.105")
            ))
        .exec(
          http("diff created tagged")
            .get("/node/2006")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.node.unique(2006),
              checks.osm.node.attributes(2006, 9),
              checks.osm.node.visible(2006),
              checks.osm.node.version(2006,"1"),
              checks.osm.node.uid(2006,"2006"),
              checks.osm.node.user(2006,"2006"),
              checks.osm.node.changeset(2006,"2006"),
              checks.osm.node.lat(2006,"1.001"),
              checks.osm.node.lon(2006,"1.106"),
              checks.osm.node.children(2006,1),
              checks.osm.node.tag(2006,"a","2006")
            ))
        .exec(
          http("moved")
            .get("/node/2007")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.node.unique(2007),
              checks.osm.node.attributes(2007, 9),
              checks.osm.node.visible(2007),
              checks.osm.node.version(2007,"2"),
              checks.osm.node.uid(2007,"2107"),
              checks.osm.node.user(2007,"2107"),
              checks.osm.node.changeset(2007,"2107"),
              checks.osm.node.lat(2007,"1.001"),
              checks.osm.node.lon(2007,"1.1071")
            ))
        .exec(
          http("moved tagged")
            .get("/node/2008")
            .check(
              status.is(200),
              checks.contentType,
              checks.rootIsOsm,
              checks.osm.node.unique(2008),
              checks.osm.node.attributes(2008, 9),
              checks.osm.node.visible(2008),
              checks.osm.node.version(2008,"2"),
              checks.osm.node.uid(2008,"2108"),
              checks.osm.node.user(2008,"2108"),
              checks.osm.node.changeset(2008,"2108"),
              checks.osm.node.lat(2008,"1.001"),
              checks.osm.node.lon(2008,"1.1081"),
              checks.osm.node.children(2008,1),
              checks.osm.node.tag(2008,"a","2008")
            ))
      }
    }
    .group("N history tests") {
        exec(
          http("deleted")
            .get("/node/2001")
            .check(
              checks.isEmptyResponse,
              header("Content-Length").is("0"),
              checks.headerCache,
              status.is(410)
            ))
        .exec(
          http("deleted HEAD")
            .head("/node/2001")
            .check(
              checks.isEmptyResponse,
              header("Content-Length").is("0"),
              checks.headerCache,
              status.is(410)
            ))
        .exec(
          http("deleted tagged")
            .get("/node/2003")
            .check(
              checks.isEmptyResponse,
              header("Content-Length").is("0"),
              checks.headerCache,
              status.is(410)
            ))
        .exec(
          http("deleted tagged HEAD")
            .head("/node/2003")
            .check(
              checks.isEmptyResponse,
              header("Content-Length").is("0"),
              checks.headerCache,
              status.is(410)
            ))
    }
}
