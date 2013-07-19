package osmapi
/*
This file is part of openstreetmap-api-testsuite

Copyright (c) 2013 Paul Norman, released under the MIT license.

Defines various test cases for nodes
*/
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import bootstrap._

object NodeScenario {
  def stripZero = (s:Option[String]) => s.map(_.replaceAll("0+$",""))

  val nodeScn = scenario("Node tests")
    .group("Node tests") {
      group("Header tests") {
        exec(
          http("*/*")
            .get("/node/1001")
            .header("Accept","*/*")
            .check(
              status.is(200),
              header("Content-Type").is("text/xml; charset=utf-8"),
              headerRegex("Cache-Control","""(^|(, *))max-age=0($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))private($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))must-revalidate($|(, *))""").exists,
              xpath("""/osm""").count.is(1)))
        .exec(
          http("text/*")
            .get("/node/1001")
            .header("Accept","text/*")
            .check(
              status.is(200),
              header("Content-Type").is("text/xml; charset=utf-8"),
              headerRegex("Cache-Control","""(^|(, *))max-age=0($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))private($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))must-revalidate($|(, *))""").exists,
              xpath("""/osm""").count.is(1)))
        .exec(
          http("text/xml")
            .get("/node/1001")
            .header("Accept","text/xml")
            .check(
              status.is(200),
              header("Content-Type").is("text/xml; charset=utf-8"),
              headerRegex("Cache-Control","""(^|(, *))max-age=0($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))private($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))must-revalidate($|(, *))""").exists,
              xpath("""/osm""").count.is(1)))
        .exec(
          http("invalid *")
            .get("/node/1001")
            .header("Accept","*")
            .check(
              status.is(200),
              header("Content-Type").is("text/xml; charset=utf-8"),
              headerRegex("Cache-Control","""(^|(, *))max-age=0($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))private($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))must-revalidate($|(, *))""").exists,
              xpath("""/osm""").count.is(1)))
        .exec(
          http("invalid josm")
            .get("/node/1001")
            .header("Accept","text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2")
            .check(
              status.is(200),
              header("Content-Type").is("text/xml; charset=utf-8"),
              headerRegex("Cache-Control","""(^|(, *))max-age=0($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))private($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))must-revalidate($|(, *))""").exists,
              xpath("""/osm""").count.is(1)))
      }
      .group("Overall tests") {
        exec(
          http("osm xml attributes")
            .get("/node/1001")
            .check(
              status.is(200),
              header("Content-Type").is("text/xml; charset=utf-8"),
              headerRegex("Cache-Control","""(^|(, *))max-age=0($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))private($|(, *))""").exists,
              headerRegex("Cache-Control","""(^|(, *))must-revalidate($|(, *))""").exists,
              xpath("""/*""").count.is(1),
              xpath("""/osm/@version""").is("0.6"),
              xpath("""/osm/@copyright""").is("OpenStreetMap and contributors"),
              xpath("""/osm/@attribution""").is("http://www.openstreetmap.org/copyright"),
              xpath("""/osm/@license""").is("http://opendatacommons.org/licenses/odbl/1-0/")
            ))
        .exec(
            http("missing node")
            .get("/node/1000")
            .check(
              status.is(404),
              header("Cache-Control").is("no-cache"),
              header("Content-Length").is("1"),
              sha1.is("b858cb282617fb0956d960215c8e84d1ccf909c6")
            ))
      }
      .group("Content tests") {
        exec(
          http("attributes")
            .get("/node/1001")
            .check(
              status.is(200),
              xpath("""/osm/*""").count.is(1), 
              xpath("""/osm/node[@id="1001"]""").count.is(1),
              xpath("""/osm/node[@id="1001"]/@*""").count.is(9),
              xpath("""/osm/node[@id="1001"]/@version""").is("1"),
              xpath("""/osm/node[@id="1001"]/@changeset""").is("1001"),
              xpath("""/osm/node[@id="1001"]/@lat""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="1001"]/@lon""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="1001"]/@user""").is("user_1001"),
              xpath("""/osm/node[@id="1001"]/@uid""").is("1001"),
              xpath("""/osm/node[@id="1001"]/@visible""").is("true")
            ))
        .exec(
          http("different attributes")
            .get("/node/1002")
            .check(
              status.is(200),
              xpath("""/osm/*""").count.is(1), 
              xpath("""/osm/node[@id="1002"]""").count.is(1),
              xpath("""/osm/node[@id="1002"]/@*""").count.is(9),
              xpath("""/osm/node[@id="1002"]/@version""").is("2"),
              xpath("""/osm/node[@id="1002"]/@changeset""").is("1002"),
              xpath("""/osm/node[@id="1002"]/@lat""").transform(s => stripZero(s)).is("1.002"),
              xpath("""/osm/node[@id="1002"]/@lon""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="1002"]/@user""").is("user_1002"),
              xpath("""/osm/node[@id="1002"]/@uid""").is("1002"),
              xpath("""/osm/node[@id="1002"]/@visible""").is("true")
            ))
        .exec(
          http("anonymous user")
            .get("/node/1003")
            .check(
              status.is(200),
              xpath("""/osm/*""").count.is(1),
              xpath("""/osm/node[@id="1003"]""").count.is(1),
              xpath("""/osm/node[@id="1003"]/@*""").count.is(7),
              xpath("""/osm/node[@id="1003"]/@version""").is("1"),
              xpath("""/osm/node[@id="1003"]/@changeset""").is("1003"),
              xpath("""/osm/node[@id="1003"]/@lat""").transform(s => stripZero(s)).is("1.003"),
              xpath("""/osm/node[@id="1003"]/@lon""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="1003"]/@visible""").is("true")
            ))
        .exec(
          http("one tag")
            .get("/node/1004")
            .check(
              status.is(200),
              xpath("""/osm/*""").count.is(1),
              xpath("""/osm/node[@id="1004"]""").count.is(1),
              xpath("""/osm/node[@id="1004"]/@*""").count.is(9),
              xpath("""/osm/node[@id="1004"]/@version""").is("1"),
              xpath("""/osm/node[@id="1004"]/@changeset""").is("1001"),
              xpath("""/osm/node[@id="1004"]/@lat""").transform(s => stripZero(s)).is("1.004"),
              xpath("""/osm/node[@id="1004"]/@lon""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="1004"]/@user""").is("user_1001"),
              xpath("""/osm/node[@id="1004"]/@uid""").is("1001"),
              xpath("""/osm/node[@id="1004"]/@visible""").is("true"),
              xpath("""/osm/node[@id="1004"]/*""").count.is(1),
              xpath("""/osm/node[@id="1004"]/tag[@k="a"]/@v""").is("1")
            ))
        .exec(
          http("multiple tags")
            .get("/node/1005")
            .check(
              status.is(200),
              xpath("""/osm/*""").count.is(1),
              xpath("""/osm/node[@id="1005"]""").count.is(1),
              xpath("""/osm/node[@id="1005"]/@*""").count.is(9),
              xpath("""/osm/node[@id="1005"]/@version""").is("1"),
              xpath("""/osm/node[@id="1005"]/@changeset""").is("1001"),
              xpath("""/osm/node[@id="1005"]/@lat""").transform(s => stripZero(s)).is("1.005"),
              xpath("""/osm/node[@id="1005"]/@lon""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="1005"]/@user""").is("user_1001"),
              xpath("""/osm/node[@id="1005"]/@uid""").is("1001"),
              xpath("""/osm/node[@id="1005"]/@visible""").is("true"),
              xpath("""/osm/node[@id="1005"]/*""").count.is(2),
              xpath("""/osm/node[@id="1005"]/tag[@k="a"]/@v""").is("1"),
              xpath("""/osm/node[@id="1005"]/tag[@k="b"]/@v""").is("2")
            ))
        .exec(
          http("different tag order")
            .get("/node/1006")
            .check(
              status.is(200),
              xpath("""/osm/*""").count.is(1),
              xpath("""/osm/node[@id="1006"]""").count.is(1),
              xpath("""/osm/node[@id="1006"]/@*""").count.is(9),
              xpath("""/osm/node[@id="1006"]/@version""").is("1"),
              xpath("""/osm/node[@id="1006"]/@changeset""").is("1001"),
              xpath("""/osm/node[@id="1006"]/@lat""").transform(s => stripZero(s)).is("1.006"),
              xpath("""/osm/node[@id="1006"]/@lon""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="1006"]/@user""").is("user_1001"),
              xpath("""/osm/node[@id="1006"]/@uid""").is("1001"),
              xpath("""/osm/node[@id="1006"]/@visible""").is("true"),
              xpath("""/osm/node[@id="1006"]/*""").count.is(2),
              xpath("""/osm/node[@id="1006"]/tag[@k="a"]/@v""").is("1"),
              xpath("""/osm/node[@id="1006"]/tag[@k="b"]/@v""").is("2")
            ))
      }
    }
}
