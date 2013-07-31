package osmapi
/*
This file is part of openstreetmap-api-testsuite

Copyright (c) 2013 Paul Norman, released under the MIT license.

Defines various test cases for nodes
*/
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import bootstrap._

object WayScenarios {
  val wayScn = scenario("Way tests")
    .group("W tests") {
      group("Header accept tests") {
        exec(
          http("*/*")
            .get("/way/3001")
            .header("Accept","*/*")
            .check(
              xpath("""/osm""").count.is(1),
              headerRegex("Cache-Control","""(^|(, *))max-age=0($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))private($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))must-revalidate($|(, *))""").exists,
              header("Content-Type").is("text/xml; charset=utf-8"),
              status.is(200)
            ))
        .exec(
          http("text/*")
            .get("/way/3001")
            .header("Accept","text/*")
            .check(
              xpath("""/osm""").count.is(1),
              headerRegex("Cache-Control","""(^|(, *))max-age=0($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))private($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))must-revalidate($|(, *))""").exists,
              header("Content-Type").is("text/xml; charset=utf-8"),
              status.is(200)
            ))
        .exec(
          http("text/xml")
            .get("/way/3001")
            .header("Accept","text/xml")
            .check(
              xpath("""/osm""").count.is(1),
              headerRegex("Cache-Control","""(^|(, *))max-age=0($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))private($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))must-revalidate($|(, *))""").exists,
              header("Content-Type").is("text/xml; charset=utf-8"),
              status.is(200)
            ))
        .exec(
          http("invalid *")
            .get("/way/3001")
            .header("Accept","*")
            .check(
              xpath("""/osm""").count.is(1),
              headerRegex("Cache-Control","""(^|(, *))max-age=0($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))private($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))must-revalidate($|(, *))""").exists,
              header("Content-Type").is("text/xml; charset=utf-8"),
              status.is(200)))
        .exec(
          http("invalid josm")
            .get("/way/3001")
            .header("Accept","text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2")
            .check(
              xpath("""/osm""").count.is(1),
              headerRegex("Cache-Control","""(^|(, *))max-age=0($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))private($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))must-revalidate($|(, *))""").exists,
              header("Content-Type").is("text/xml; charset=utf-8"),
              status.is(200)
            ))
      }
      .group("Overall tests") {
        exec(
          http("osm xml attributes")
            .get("/way/3001")
            .check(
              xpath("""/osm/@version""").is("0.6"),
              xpath("""/osm/@copyright""").is("OpenStreetMap and contributors"),
              xpath("""/osm/@attribution""").is("http://www.openstreetmap.org/copyright"),
              xpath("""/osm/@license""").is("http://opendatacommons.org/licenses/odbl/1-0/"),
              xpath("""/*""").count.is(1),
              header("Content-Type").is("text/xml; charset=utf-8"),
              headerRegex("Cache-Control","""(^|(, *))max-age=0($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))private($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))must-revalidate($|(, *))""").exists,
              status.is(200)
            ))
        .exec(
            http("missing node")
            .get("/way/3000")
            .check(
              sha1.is("da39a3ee5e6b4b0d3255bfef95601890afd80709"),
              header("Content-Length").is("0"),
              header("Cache-Control").is("no-cache"),
              status.is(404)
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
              xpath("""/osm/*""").count.is(1), 
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
              xpath("""/osm/*""").count.is(1), 
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
}
