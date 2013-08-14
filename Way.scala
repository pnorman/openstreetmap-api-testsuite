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
              checks.headerNoCache,
              checks.isEmptyResponse,
              header("Content-Length").is("0")
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
              xpath("""/osm/way[@id="3001"]/nd[2]/@ref""").is("3012"),
              xpath("""/osm/way[@id="3001"]/nd[1]/@ref""").is("3011"),
              xpath("""/osm/way[@id="3001"]/*""").count.is(2),
              xpath("""/osm/way[@id="3001"]/@version""").is("1"),
              xpath("""/osm/way[@id="3001"]/@changeset""").is("3001"),
              xpath("""/osm/way[@id="3001"]/@user""").is("user_3001"),
              xpath("""/osm/way[@id="3001"]/@uid""").is("3001"),
              xpath("""/osm/way[@id="3001"]/@visible""").is("true"),
              xpath("""/osm/way[@id="3001"]/@*""").count.is(7),
              xpath("""/osm/way[@id="3001"]""").count.is(1),
              checks.rootIsOsm,
              status.is(200)
            ))
        .exec(
          http("anonymous way")
            .get("/way/3002")
            .check(
              xpath("""/osm/way[@id="3002"]/nd[2]/@ref""").is("3012"),
              xpath("""/osm/way[@id="3002"]/nd[1]/@ref""").is("3011"),
              xpath("""/osm/way[@id="3002"]/*""").count.is(2),
              xpath("""/osm/way[@id="3002"]/@version""").is("1"),
              xpath("""/osm/way[@id="3002"]/@changeset""").is("3002"),
              xpath("""/osm/way[@id="3002"]/@visible""").is("true"),
              xpath("""/osm/way[@id="3002"]/@*""").count.is(5),
              xpath("""/osm/way[@id="3002"]""").count.is(1),
              checks.rootIsOsm,
              status.is(200)
            ))
        .exec(
          http("tagged way")
            .get("/way/3003")
            .check(
              xpath("""/osm/way[@id="3003"]/tag[@k="b"]/@v""").is("2"),
              xpath("""/osm/way[@id="3003"]/tag[@k="a"]/@v""").is("1"),
              xpath("""/osm/way[@id="3003"]/nd[2]/@ref""").is("3012"),
              xpath("""/osm/way[@id="3003"]/nd[1]/@ref""").is("3011"),
              xpath("""/osm/way[@id="3003"]/*""").count.is(4),
              xpath("""/osm/way[@id="3003"]/@version""").is("1"),
              xpath("""/osm/way[@id="3003"]/@changeset""").is("3003"),
              xpath("""/osm/way[@id="3003"]/@user""").is("user_3003"),
              xpath("""/osm/way[@id="3003"]/@uid""").is("3003"),
              xpath("""/osm/way[@id="3003"]/@visible""").is("true"),
              xpath("""/osm/way[@id="3003"]/@*""").count.is(7),
              xpath("""/osm/way[@id="3003"]""").count.is(1),
              status.is(200)
            ))
        .exec(
          http("one node way")
            .get("/way/3004")
            .check(
              xpath("""/osm/way[@id="3004"]/nd[1]/@ref""").is("3011"),
              xpath("""/osm/way[@id="3004"]/*""").count.is(1),
              xpath("""/osm/way[@id="3004"]/@version""").is("1"),
              xpath("""/osm/way[@id="3004"]/@changeset""").is("3004"),
              xpath("""/osm/way[@id="3004"]/@user""").is("user_3004"),
              xpath("""/osm/way[@id="3004"]/@uid""").is("3004"),
              xpath("""/osm/way[@id="3004"]/@visible""").is("true"),
              xpath("""/osm/way[@id="3004"]/@*""").count.is(7),
              xpath("""/osm/way[@id="3004"]""").count.is(1),
              status.is(200)
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
              xpath("""/osm/way[@id="4002"]/nd[2]/@ref""").is("4014"),
              xpath("""/osm/way[@id="4002"]/nd[1]/@ref""").is("4013"),
              xpath("""/osm/way[@id="4002"]/*""").count.is(2),
              xpath("""/osm/way[@id="4002"]/@version""").is("3"),
              xpath("""/osm/way[@id="4002"]/@changeset""").is("4202"),
              xpath("""/osm/way[@id="4002"]/@user""").is("user_4202"),
              xpath("""/osm/way[@id="4002"]/@uid""").is("4202"),
              xpath("""/osm/way[@id="4002"]/@visible""").is("true"),
              xpath("""/osm/way[@id="4002"]/@*""").count.is(7),
              xpath("""/osm/way[@id="4002"]""").count.is(1),
              checks.rootIsOsm,
              status.is(200)
            ))
        .exec(
          http("recreated as untagged")
            .get("/way/4004")
            .check(
              xpath("""/osm/way[@id="4004"]/nd[2]/@ref""").is("4014"),
              xpath("""/osm/way[@id="4004"]/nd[1]/@ref""").is("4013"),
              xpath("""/osm/way[@id="4004"]/*""").count.is(2),
              xpath("""/osm/way[@id="4004"]/@version""").is("3"),
              xpath("""/osm/way[@id="4004"]/@changeset""").is("4204"),
              xpath("""/osm/way[@id="4004"]/@user""").is("user_4204"),
              xpath("""/osm/way[@id="4004"]/@uid""").is("4204"),
              xpath("""/osm/way[@id="4004"]/@visible""").is("true"),
              xpath("""/osm/way[@id="4004"]/@*""").count.is(7),
              xpath("""/osm/way[@id="4004"]""").count.is(1),
              checks.rootIsOsm,
              status.is(200)
            ))
        .exec(
          http("diff created")
            .get("/way/4005")
            .check(
              xpath("""/osm/way[@id="4005"]/nd[2]/@ref""").is("4014"),
              xpath("""/osm/way[@id="4005"]/nd[1]/@ref""").is("4013"),
              xpath("""/osm/way[@id="4005"]/*""").count.is(2),
              xpath("""/osm/way[@id="4005"]/@version""").is("1"),
              xpath("""/osm/way[@id="4005"]/@changeset""").is("4205"),
              xpath("""/osm/way[@id="4005"]/@user""").is("user_4205"),
              xpath("""/osm/way[@id="4005"]/@uid""").is("4205"),
              xpath("""/osm/way[@id="4005"]/@visible""").is("true"),
              xpath("""/osm/way[@id="4005"]/@*""").count.is(7),
              xpath("""/osm/way[@id="4005"]""").count.is(1),
              checks.rootIsOsm,
              status.is(200)
            ))
        .exec(
          http("diff created tagged")
            .get("/way/4006")
            .check(
              xpath("""/osm/way[@id="4006"]/tag[@k="b"]/@v""").is("2"),
              xpath("""/osm/way[@id="4006"]/tag[@k="a"]/@v""").is("1"),
              xpath("""/osm/way[@id="4006"]/nd[2]/@ref""").is("4014"),
              xpath("""/osm/way[@id="4006"]/nd[1]/@ref""").is("4013"),
              xpath("""/osm/way[@id="4006"]/*""").count.is(4),
              xpath("""/osm/way[@id="4006"]/@version""").is("1"),
              xpath("""/osm/way[@id="4006"]/@changeset""").is("4206"),
              xpath("""/osm/way[@id="4006"]/@user""").is("user_4206"),
              xpath("""/osm/way[@id="4006"]/@uid""").is("4206"),
              xpath("""/osm/way[@id="4006"]/@visible""").is("true"),
              xpath("""/osm/way[@id="4006"]/@*""").count.is(7),
              xpath("""/osm/way[@id="4006"]""").count.is(1),
              checks.rootIsOsm,
              status.is(200)
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
