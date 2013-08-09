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

object NodesScenarios {
  def stripZero = (s:Option[String]) => s.map(_.replaceAll("0+$",""))

  val nodesScn = scenario("Nodes tests")
    .group("Ns tests") {
      group("Single accept header tests") {
        exec(
          http("*/*")
            .get("/nodes?nodes=1001")
            .header("Accept","*/*")
            .check(
              xpath("""/osm""").count.is(1),
              globalChecks.headerCache,
              globalChecks.contentType,
              status.is(200)
            ))
        .exec(
          http("text/*")
            .get("/nodes?nodes=1001")
            .header("Accept","text/*")
            .check(
              xpath("""/osm""").count.is(1),
              globalChecks.headerCache,
              globalChecks.contentType,
              status.is(200)
            ))
        .exec(
          http("text/xml")
            .get("/nodes?nodes=1001")
            .header("Accept","text/xml")
            .check(
              xpath("""/osm""").count.is(1),
              globalChecks.headerCache,
              globalChecks.contentType,
              status.is(200)
            ))
        .exec(
          http("invalid *")
            .get("/nodes?nodes=1001")
            .header("Accept","*")
            .check(
              xpath("""/osm""").count.is(1),
              globalChecks.headerCache,
              globalChecks.contentType,
              status.is(200)))
        .exec(
          http("invalid josm")
            .get("/nodes?nodes=1001")
            .header("Accept","text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2")
            .check(
              xpath("""/osm""").count.is(1),
              globalChecks.headerCache,
              globalChecks.contentType,
              status.is(200)
            ))
      }
      .group("Multi deleted tests") {
        exec(
            http("1 missing 1 existing")
            .get("/nodes?nodes=1000,1001")
            .check(
              globalChecks.isEmptyResponse,
              header("Content-Length").is("0"),
              globalChecks.headerNoCache,
              status.is(404)
            ))
        .exec(
            http("1 existing 1 missing")
            .get("/nodes?nodes=1001,1000")
            .check(
              globalChecks.isEmptyResponse,
              header("Content-Length").is("0"),
              globalChecks.headerNoCache,
              status.is(404)
            ))
      }
      .group("Syntax tests") {
        exec(
          http("Empty request")
            .get("/nodes")
            .check(
              globalChecks.headerNoCache,
              status.is(400)
          ))
        .exec(
          http("Empty nodes param")
            .get("/nodes?nodes")
            .check(
              globalChecks.headerNoCache,
              status.is(400)
          ))
        .exec(
          http("Empty nodes param")
            .get("/nodes?nodes=")
            .check(
              globalChecks.headerNoCache,
              status.is(400)
          ))
        .exec(
          http("Invalid nodes param")
            .get("/nodes?nodes=asdf")
            .check(
              globalChecks.headerNoCache,
              status.is(400)
          ))
        .exec(
          http("Invalid and valid nodes param")
            .get("/nodes?nodes=1001,asdf")
            .check(
              globalChecks.headerNoCache,
              status.is(400)
          ))
        .exec(
          http("Large node ID")
            .get("/nodes?nodes=20000000000000000000") //>2^64
            .check(
              globalChecks.headerNoCache,
              status.not(500),
              status.not(200)
          ))
      }
      .group("Single content tests") {
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
      .group("Multi content tests") {
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

  val nodesDiffScn = scenario("Nodes diff tests")
    .group("Ns diff tests") {
      group("Single tests") {
        exec(
          http("recreated")
            .get("/nodes?nodes=2002")
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
              globalChecks.contentType,
              status.is(200)
            ))
        .exec(
          http("recreated as untagged")
            .get("/nodes?nodes=2004")
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
              globalChecks.contentType,
              status.is(200)
            ))
        .exec(
          http("diff created")
            .get("/nodes?nodes=2005")
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
              globalChecks.contentType,
              status.is(200)
            ))
        .exec(
          http("diff created tagged")
            .get("/nodes?nodes=2006")
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
              globalChecks.contentType,
              status.is(200)
            ))
        .exec(
          http("moved")
            .get("/nodes?nodes=2007")
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
              globalChecks.contentType,
              status.is(200)
            ))
        .exec(
          http("moved tagged")
            .get("/nodes?nodes=2008")
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
              globalChecks.contentType,
              status.is(200)
            ))
      }
    }
    .group("Ns history tests") {
      group("Single content tests") {
        exec(
          http("deleted")
            .get("/nodes?nodes=2001")
            .check(
              xpath("""/osm/node[@id="2001"]/@version""").is("2"),
              xpath("""/osm/node[@id="2001"]/@changeset""").is("2101"),
              xpath("""/osm/node[@id="2001"]/@user""").is("user_2101"),
              xpath("""/osm/node[@id="2001"]/@uid""").is("2101"),
              xpath("""/osm/node[@id="2001"]/@visible""").is("false"),
              xpath("""/osm/node[@id="2001"]/@*""").count.is(7),
              xpath("""/osm/node[@id="2001"]""").count.is(1),
              xpath("""/osm/*""").count.is(1),
              globalChecks.contentType,
              status.is(200)
            ))
        .exec(
          http("deleted tagged")
            .get("/nodes?nodes=2003")
            .check(
              xpath("""/osm/node[@id="2003"]/*""").count.is(0),
              xpath("""/osm/node[@id="2003"]/@version""").is("2"),
              xpath("""/osm/node[@id="2003"]/@changeset""").is("2103"),
              xpath("""/osm/node[@id="2003"]/@user""").is("user_2103"),
              xpath("""/osm/node[@id="2003"]/@uid""").is("2103"),
              xpath("""/osm/node[@id="2003"]/@visible""").is("false"),
              xpath("""/osm/node[@id="2003"]/@*""").count.is(7),
              xpath("""/osm/node[@id="2003"]""").count.is(1),
              xpath("""/osm/*""").count.is(1),
              globalChecks.contentType,
              status.is(200)
            ))
      }
      .group("Multi content tests") {
        exec(
          http("1 created 1 deleted tagged")
            .get("/nodes?nodes=1001,2003")
            .check(
              xpath("""/osm/node[@id="1001"]/@version""").is("1"),
              xpath("""/osm/node[@id="1001"]/@changeset""").is("1001"),
              xpath("""/osm/node[@id="1001"]/@lat""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="1001"]/@lon""").transform(s => stripZero(s)).is("1.001"),
              xpath("""/osm/node[@id="1001"]/@user""").is("user_1001"),
              xpath("""/osm/node[@id="1001"]/@uid""").is("1001"),
              xpath("""/osm/node[@id="1001"]/@visible""").is("true"),
              xpath("""/osm/node[@id="1001"]/@*""").count.is(9),
              xpath("""/osm/node[@id="2003"]/*""").count.is(0),
              xpath("""/osm/node[@id="2003"]/@version""").is("2"),
              xpath("""/osm/node[@id="2003"]/@changeset""").is("2103"),
              xpath("""/osm/node[@id="2003"]/@user""").is("user_2103"),
              xpath("""/osm/node[@id="2003"]/@uid""").is("2103"),
              xpath("""/osm/node[@id="2003"]/@visible""").is("false"),
              xpath("""/osm/node[@id="2003"]/@*""").count.is(7),
              xpath("""/osm/node[@id="2003"]""").count.is(1),
              xpath("""/osm/*""").count.is(2),
              globalChecks.contentType,
              status.is(200)
            ))
      }
      .group("multi response tests") {
        exec(
            http("1 deleted 1 missing")
            .get("/nodes?nodes=1000,2001")
            .check(
              globalChecks.isEmptyResponse,
              header("Content-Length").is("0"),
              globalChecks.headerNoCache,
              status.is(404)
            ))
      }
    }
}
