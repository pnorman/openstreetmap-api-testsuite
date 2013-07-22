package osmapi
/*
This file is part of openstreetmap-api-testsuite

Copyright (c) 2013 Paul Norman, released under the MIT license.

Defines various test cases for nodes?nodes=...

Note: This test re-uses the XML from the /node/# tests
*/
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import bootstrap._

object NodesScenario {
  def stripZero = (s:Option[String]) => s.map(_.replaceAll("0+$",""))

  val nodesScn = scenario("Nodes tests")
    .group("Nodes tests") {
      group("Single-node accept header tests") {
        exec(
          http("*/*")
            .get("/nodes?nodes=1001")
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
            .get("/nodes?nodes=1001")
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
            .get("/nodes?nodes=1001")
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
            .get("/nodes?nodes=1001")
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
            .get("/nodes?nodes=1001")
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
      .group("Multi-node http response tests") {
        exec(
            http("1 missing 1 existing")
            .get("/nodes?nodes=1000,1001")
            .check(
              sha1.is("da39a3ee5e6b4b0d3255bfef95601890afd80709"),
              header("Content-Length").is("0"),
              header("Cache-Control").is("no-cache"),
              status.is(404)
            ))
        .exec(
            http("1 existing 1 missing")
            .get("/nodes?nodes=1001,1000")
            .check(
              sha1.is("da39a3ee5e6b4b0d3255bfef95601890afd80709"),
              header("Content-Length").is("0"),
              header("Cache-Control").is("no-cache"),
              status.is(404)
            ))
      }
      .group("Single-node content tests") {
        exec(
          http("attributes")
            .get("/nodes?nodes=1001")
            .check(
              xpath("""/osm/node[@id="1001"]/@version""").is("1"),
              xpath("""/osm/node[@id="1001"]/@changeset""").is("1001"),
              xpath("""/osm/node[@id="1001"]/@lat""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="1001"]/@lon""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="1001"]/@user""").is("user_1001"),
              xpath("""/osm/node[@id="1001"]/@uid""").is("1001"),
              xpath("""/osm/node[@id="1001"]/@visible""").is("true"),
              xpath("""/osm/node[@id="1001"]/@*""").count.is(9),
              xpath("""/osm/node[@id="1001"]""").count.is(1),
              xpath("""/osm/*""").count.is(1),
              status.is(200)
            ))
        .exec(
          http("different attributes")
            .get("/nodes?nodes=1002")
            .check(
              xpath("""/osm/node[@id="1002"]/@version""").is("2"),
              xpath("""/osm/node[@id="1002"]/@changeset""").is("1002"),
              xpath("""/osm/node[@id="1002"]/@lat""").transform(s => stripZero(s)).is("1.002"),
              xpath("""/osm/node[@id="1002"]/@lon""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="1002"]/@user""").is("user_1002"),
              xpath("""/osm/node[@id="1002"]/@uid""").is("1002"),
              xpath("""/osm/node[@id="1002"]/@visible""").is("true"),
              xpath("""/osm/node[@id="1002"]/@*""").count.is(9),
              xpath("""/osm/node[@id="1002"]""").count.is(1),
              xpath("""/osm/*""").count.is(1),
              status.is(200)
            ))
        .exec(
          http("multiple tags")
            .get("/nodes?nodes=1005")
            .check(
              xpath("""/osm/node[@id="1005"]/tag[@k="a"]/@v""").is("1"),
              xpath("""/osm/node[@id="1005"]/tag[@k="b"]/@v""").is("2"),
              xpath("""/osm/node[@id="1005"]/*""").count.is(2),
              xpath("""/osm/node[@id="1005"]/@version""").is("1"),
              xpath("""/osm/node[@id="1005"]/@changeset""").is("1001"),
              xpath("""/osm/node[@id="1005"]/@lat""").transform(s => stripZero(s)).is("1.005"),
              xpath("""/osm/node[@id="1005"]/@lon""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="1005"]/@user""").is("user_1001"),
              xpath("""/osm/node[@id="1005"]/@uid""").is("1001"),
              xpath("""/osm/node[@id="1005"]/@visible""").is("true"),
              xpath("""/osm/node[@id="1005"]/@*""").count.is(9),
              xpath("""/osm/node[@id="1005"]""").count.is(1),
              xpath("""/osm/*""").count.is(1),
              status.is(200)
            ))
        .exec(
          http("anonymous user")
            .get("/nodes?nodes=1003")
            .check(
              xpath("""/osm/node[@id="1003"]/@version""").is("1"),
              xpath("""/osm/node[@id="1003"]/@changeset""").is("1003"),
              xpath("""/osm/node[@id="1003"]/@lat""").transform(s => stripZero(s)).is("1.003"),
              xpath("""/osm/node[@id="1003"]/@lon""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="1003"]/@visible""").is("true"),
              xpath("""/osm/node[@id="1003"]/@*""").count.is(7),
              xpath("""/osm/node[@id="1003"]""").count.is(1),
              xpath("""/osm/*""").count.is(1),
              status.is(200)
            ))
      }
      .group("Multi-node content tests") {
        exec(
          http("multiple attributes")
            .get("/nodes?nodes=1001,1002")
            .check(
              xpath("""/osm/node[@id="1002"]/@version""").is("2"),
              xpath("""/osm/node[@id="1002"]/@changeset""").is("1002"),
              xpath("""/osm/node[@id="1002"]/@lat""").transform(s => stripZero(s)).is("1.002"),
              xpath("""/osm/node[@id="1002"]/@lon""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="1002"]/@user""").is("user_1002"),
              xpath("""/osm/node[@id="1002"]/@uid""").is("1002"),
              xpath("""/osm/node[@id="1002"]/@visible""").is("true"),
              xpath("""/osm/node[@id="1002"]/@*""").count.is(9),
              xpath("""/osm/node[@id="1002"]""").count.is(1),
              xpath("""/osm/node[@id="1001"]/@version""").is("1"),
              xpath("""/osm/node[@id="1001"]/@changeset""").is("1001"),
              xpath("""/osm/node[@id="1001"]/@lat""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="1001"]/@lon""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="1001"]/@user""").is("user_1001"),
              xpath("""/osm/node[@id="1001"]/@uid""").is("1001"),
              xpath("""/osm/node[@id="1001"]/@visible""").is("true"),
              xpath("""/osm/node[@id="1001"]/@*""").count.is(9),
              xpath("""/osm/node[@id="1001"]""").count.is(1),
              xpath("""/osm/*""").count.is(2),
              status.is(200)
            ))
        .exec(
          http("multiple tags")
            .get("/nodes?nodes=1005,1006")
            .check(
              xpath("""/osm/node[@id="1006"]/tag[@k="a"]/@v""").is("1"),
              xpath("""/osm/node[@id="1006"]/tag[@k="b"]/@v""").is("2"),
              xpath("""/osm/node[@id="1006"]/*""").count.is(2),
              xpath("""/osm/node[@id="1006"]/@version""").is("1"),
              xpath("""/osm/node[@id="1006"]/@changeset""").is("1001"),
              xpath("""/osm/node[@id="1006"]/@lat""").transform(s => stripZero(s)).is("1.006"),
              xpath("""/osm/node[@id="1006"]/@lon""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="1006"]/@user""").is("user_1001"),
              xpath("""/osm/node[@id="1006"]/@uid""").is("1001"),
              xpath("""/osm/node[@id="1006"]/@visible""").is("true"),
              xpath("""/osm/node[@id="1006"]/@*""").count.is(9),
              xpath("""/osm/node[@id="1006"]""").count.is(1),
              xpath("""/osm/node[@id="1005"]/tag[@k="a"]/@v""").is("1"),
              xpath("""/osm/node[@id="1005"]/tag[@k="b"]/@v""").is("2"),
              xpath("""/osm/node[@id="1005"]/*""").count.is(2),
              xpath("""/osm/node[@id="1005"]/@version""").is("1"),
              xpath("""/osm/node[@id="1005"]/@changeset""").is("1001"),
              xpath("""/osm/node[@id="1005"]/@lat""").transform(s => stripZero(s)).is("1.005"),
              xpath("""/osm/node[@id="1005"]/@lon""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="1005"]/@user""").is("user_1001"),
              xpath("""/osm/node[@id="1005"]/@uid""").is("1001"),
              xpath("""/osm/node[@id="1005"]/@visible""").is("true"),
              xpath("""/osm/node[@id="1005"]/@*""").count.is(9),
              xpath("""/osm/node[@id="1005"]""").count.is(1),
              xpath("""/osm/*""").count.is(2),
              status.is(200)
            ))
      }
    }
}
