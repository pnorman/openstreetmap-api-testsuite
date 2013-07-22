package osmapi
/*
This file is part of openstreetmap-api-testsuite

Copyright (c) 2013 Paul Norman, released under the MIT license.

Defines various test cases for nodes with diff processing
*/
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import bootstrap._

object NodeDiffScenario {
  def stripZero = (s:Option[String]) => s.map(_.replaceAll("0+$",""))

  val nodeDiffScn = scenario("Node diff tests")
    .group("Node diff tests") {
      group("Status tests") {
        exec(
          http("recreated")
            .get("/node/2002")
            .check(
              xpath("""/osm/node[@id="2002"]/@version""").is("3"),
              xpath("""/osm/node[@id="2002"]/@changeset""").is("2202"),
              xpath("""/osm/node[@id="2002"]/@lat""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="2002"]/@lon""").transform(s => stripZero(s)).is("1.102"),
              xpath("""/osm/node[@id="2002"]/@user""").is("user_2202"),
              xpath("""/osm/node[@id="2002"]/@uid""").is("2202"),
              xpath("""/osm/node[@id="2002"]/@visible""").is("true"),
              xpath("""/osm/node[@id="2002"]/@*""").count.is(9),
              xpath("""/osm/node[@id="2002"]""").count.is(1),
              xpath("""/osm/*""").count.is(1), 
              header("Content-Type").is("text/xml; charset=utf-8"),
              status.is(200)
            ))
        .exec(
          http("recreated as untagged")
            .get("/node/2004")
            .check(
              xpath("""/osm/node[@id="2004"]/@*""").count.is(9),
              xpath("""/osm/node[@id="2004"]/@version""").is("3"),
              xpath("""/osm/node[@id="2004"]/@changeset""").is("2204"),
              xpath("""/osm/node[@id="2004"]/@lat""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="2004"]/@lon""").transform(s => stripZero(s)).is("1.104"),
              xpath("""/osm/node[@id="2004"]/@user""").is("user_2204"),
              xpath("""/osm/node[@id="2004"]/@uid""").is("2204"),
              xpath("""/osm/node[@id="2004"]/@visible""").is("true"),
              xpath("""/osm/node[@id="2004"]/*""").count.is(0), // untagged
              xpath("""/osm/node[@id="2004"]""").count.is(1),
              xpath("""/osm/*""").count.is(1), 
              header("Content-Type").is("text/xml; charset=utf-8"),
              status.is(200)
            ))
        .exec(
          http("diff created")
            .get("/node/2005")
            .check(
              xpath("""/osm/node[@id="2005"]/@version""").is("1"),
              xpath("""/osm/node[@id="2005"]/@changeset""").is("2005"),
              xpath("""/osm/node[@id="2005"]/@lat""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="2005"]/@lon""").transform(s => stripZero(s)).is("1.105"),
              xpath("""/osm/node[@id="2005"]/@user""").is("user_2005"),
              xpath("""/osm/node[@id="2005"]/@uid""").is("2005"),
              xpath("""/osm/node[@id="2005"]/@visible""").is("true"),
              xpath("""/osm/node[@id="2005"]/@*""").count.is(9),
              xpath("""/osm/node[@id="2005"]""").count.is(1),
              xpath("""/osm/*""").count.is(1), 
              header("Content-Type").is("text/xml; charset=utf-8"),
              status.is(200)
            ))
        .exec(
          http("diff created tagged")
            .get("/node/2006")
            .check(
              xpath("""/osm/node[@id="2006"]/tag[@k="a"]/@v""").is("2006"),
              xpath("""/osm/node[@id="2006"]/*""").count.is(1),
              xpath("""/osm/node[@id="2006"]/@version""").is("1"),
              xpath("""/osm/node[@id="2006"]/@changeset""").is("2006"),
              xpath("""/osm/node[@id="2006"]/@lat""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="2006"]/@lon""").transform(s => stripZero(s)).is("1.106"),
              xpath("""/osm/node[@id="2006"]/@user""").is("user_2006"),
              xpath("""/osm/node[@id="2006"]/@uid""").is("2006"),
              xpath("""/osm/node[@id="2006"]/@visible""").is("true"),
              xpath("""/osm/node[@id="2006"]/@*""").count.is(9),
              xpath("""/osm/node[@id="2006"]""").count.is(1),
              xpath("""/osm/*""").count.is(1), 
              header("Content-Type").is("text/xml; charset=utf-8"),
              status.is(200)
            ))
        .exec(
          http("moved")
            .get("/node/2007")
            .check(
              xpath("""/osm/node[@id="2007"]/@version""").is("2"),
              xpath("""/osm/node[@id="2007"]/@changeset""").is("2107"),
              xpath("""/osm/node[@id="2007"]/@lat""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="2007"]/@lon""").transform(s => stripZero(s)).is("1.1071"),
              xpath("""/osm/node[@id="2007"]/@user""").is("user_2107"),
              xpath("""/osm/node[@id="2007"]/@uid""").is("2107"),
              xpath("""/osm/node[@id="2007"]/@visible""").is("true"),
              xpath("""/osm/node[@id="2007"]/@*""").count.is(9),
              xpath("""/osm/node[@id="2007"]""").count.is(1),
              xpath("""/osm/*""").count.is(1), 
              header("Content-Type").is("text/xml; charset=utf-8"),
              status.is(200)
            ))
        .exec(
          http("moved tagged")
            .get("/node/2008")
            .check(
              xpath("""/osm/node[@id="2008"]/tag[@k="a"]/@v""").is("2008"),
              xpath("""/osm/node[@id="2008"]/*""").count.is(1),
              xpath("""/osm/node[@id="2008"]/@version""").is("2"),
              xpath("""/osm/node[@id="2008"]/@changeset""").is("2108"),
              xpath("""/osm/node[@id="2008"]/@lat""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="2008"]/@lon""").transform(s => stripZero(s)).is("1.1081"),
              xpath("""/osm/node[@id="2008"]/@user""").is("user_2108"),
              xpath("""/osm/node[@id="2008"]/@uid""").is("2108"),
              xpath("""/osm/node[@id="2008"]/@visible""").is("true"),
              xpath("""/osm/node[@id="2008"]/@*""").count.is(9),
              xpath("""/osm/node[@id="2008"]""").count.is(1),
              xpath("""/osm/*""").count.is(1), 
              header("Content-Type").is("text/xml; charset=utf-8"),
              status.is(200)
            ))

      }
    }
    .group("Node history tests") {
        exec(
          http("deleted")
            .get("/node/2001")
            .check(
              status.is(410),
              header("Cache-Control").is("no-cache"),
              header("Content-Length").is("0"),
              sha1.is("da39a3ee5e6b4b0d3255bfef95601890afd80709")
            ))
        .exec(
          http("deleted tagged")
            .get("/node/2003")
            .check(
              sha1.is("da39a3ee5e6b4b0d3255bfef95601890afd80709"),
              header("Content-Length").is("0"),
              header("Cache-Control").is("no-cache"),
              status.is(410)
            ))
    }
}
