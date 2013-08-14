package osmapi
/*
This file is part of openstreetmap-api-testsuite

Copyright (c) 2013 Paul Norman, released under the MIT license.

Defines various test cases for ways?ways=...

Note: This test re-uses the XML from the /way/# tests
*/
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import bootstrap._

object WaysScenarios {

  val waysScn = scenario("Ways tests")
    .group("Ws tests") {
      group("Single accept header tests") {
        exec(
          http("*/*")
            .get("/ways?ways=3001")
            .header("Accept","*/*")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("text/*")
            .get("/ways?ways=3001")
            .header("Accept","text/*")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("text/xml")
            .get("/ways?ways=3001")
            .header("Accept","text/xml")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
        .exec(
          http("invalid *")
            .get("/ways?ways=3001")
            .header("Accept","*")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
			))
        .exec(
          http("invalid josm")
            .get("/ways?ways=3001")
            .header("Accept","text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2")
            .check(
              status.is(200),
              checks.contentType,
              checks.headerCache,
              checks.rootIsOsm
            ))
      }
      .group("Multi deleted tests") {
        exec(
            http("1 missing 1 existing")
            .get("/ways?ways=4000,4001")
            .check(
              checks.isEmptyResponse,
              header("Content-Length").is("0"),
              checks.headerNoCache,
              status.is(404)
            ))
        .exec(
            http("1 existing 1 missing")
            .get("/ways?ways=4000,4001")
            .check(
              checks.isEmptyResponse,
              header("Content-Length").is("0"),
              checks.headerNoCache,
              status.is(404)
            ))
      }
      .group("Syntax tests") {
        exec(
          http("Empty request")
            .get("/ways")
            .check(
              checks.headerCache,
              status.is(400)
          ))
        .exec(
          http("Empty ways param")
            .get("/ways?ways")
            .check(
              checks.headerCache,
              status.is(400)
          ))
        .exec(
          http("Empty ways param")
            .get("/ways?ways=")
            .check(
              checks.headerCache,
              status.is(400)
          ))
        .exec(
          http("Invalid ways param")
            .get("/ways?ways=asdf")
            .check(
              checks.headerCache,
              status.is(400)
          ))
        .exec(
          http("Invalid and valid ways param")
            .get("/ways?ways=3001,asdf")
            .check(
              checks.headerCache,
              status.is(400)
          ))
        .exec(
          http("Large ID")
            .get("/ways?ways=20000000000000000000") //>2^64
            .check(
              checks.headerCache,
              status.not(500),
              status.not(200)
          ))
      }
      .group("Single content tests") {
        exec(
          http("attributes")
            .get("/ways?ways=3001")
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
            .get("/ways?ways=3002")
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
            .get("/ways?ways=3003")
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
            .get("/ways?ways=3004")
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
      .group("Multi content tests") {
        exec(
          http("multiple ways")
            .get("/ways?ways=3001,3003")
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
              xpath("""/osm/*""").count.is(2), 
              status.is(200)
              ))
      }
    }
  val waysDiffScn = scenario("Ways diff tests")
    .group("Ws diff tests") {
      group("Single tests") {
        exec(
          http("recreated")
            .get("/ways?ways=4002")
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
            .get("/ways?ways=4004")
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
            .get("/ways?ways=4005")
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
      }
    }
    .group("Ws history tests") {
      group("Single content tests") {
        exec(
          http("recreated")
            .get("/ways?ways=4002")
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
            .get("/ways?ways=4004")
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
            .get("/ways?ways=4005")
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
            .get("/ways?ways=4006")
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
      .group("Multi content tests") {
        exec(
          http("1 created 1 deleted tagged")
            .get("/ways?ways=3001,4003")
            .check(
              xpath("""/osm/way[@id="3001"]/@version""").is("1"),
              xpath("""/osm/way[@id="3001"]/@changeset""").is("3001"),
              xpath("""/osm/way[@id="3001"]/@user""").is("user_3001"),
              xpath("""/osm/way[@id="3001"]/@uid""").is("3001"),
              xpath("""/osm/way[@id="3001"]/@visible""").is("true"),
              xpath("""/osm/way[@id="3001"]/@*""").count.is(7),
              xpath("""/osm/way[@id="4003"]/*""").count.is(0),
              xpath("""/osm/way[@id="4003"]/@version""").is("2"),
              xpath("""/osm/way[@id="4003"]/@changeset""").is("4103"),
              xpath("""/osm/way[@id="4003"]/@user""").is("user_4103"),
              xpath("""/osm/way[@id="4003"]/@uid""").is("4103"),
              xpath("""/osm/way[@id="4003"]/@visible""").is("false"),
              xpath("""/osm/way[@id="4003"]/@*""").count.is(7),
              xpath("""/osm/way[@id="4003"]""").count.is(1),
              xpath("""/osm/*""").count.is(2),
              checks.contentType,
              status.is(200)
            ))
      }
      .group("multi response tests") {
        exec(
            http("1 deleted 1 missing")
            .get("/ways?ways=3000,4001")
            .check(
              checks.isEmptyResponse,
              header("Content-Length").is("0"),
              checks.headerNoCache,
              status.is(404)
            ))
      }
    }
}
