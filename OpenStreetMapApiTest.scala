package osmapi
/*
This file is part of openstreetmap-api-testsuite

Copyright (c) 2013 Paul Norman, released under the MIT license.

Specifies the tests to run and pulls it all together
*/

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.Headers.Names._
import scala.concurrent.duration._
import bootstrap._
import assertions._

class ApiSimulation extends Simulation {
  // Depending on how your API is deployed you may need to change the baseURL
  // For a rails port running as ``bundle exec rails server`` it should be
  // http://localhost:3000/api
  private val ApiBase = "http://localhost:3000/api"

  // Do not edit below here
  val httpProtocol = http
    .baseURL(ApiBase + "/0.6")
    .acceptHeader("text/xml")
    .disableFollowRedirect

  setUp(
    NodeScenarios.nodeScn.inject(atOnce(1 users)),
    NodeScenarios.nodeDiffScn.inject(nothingFor(250 milliseconds), atOnce(1 users)),
    NodesScenarios.nodesScn.inject(nothingFor(500 milliseconds), atOnce(1 users)),
    NodesScenarios.nodesDiffScn.inject(nothingFor(750 milliseconds), atOnce(1 users)),
    WayScenarios.wayScn.inject(nothingFor(1000 milliseconds), atOnce(1 users)),
    WayScenarios.wayDiffScn.inject(nothingFor(1250 milliseconds), atOnce(1 users)),
    WaysScenarios.waysScn.inject(nothingFor(1500 milliseconds), atOnce(1 users)),
    WaysScenarios.waysDiffScn.inject(nothingFor(1750 milliseconds), atOnce(1 users)),
    RelationScenarios.relationScn.inject(nothingFor(2000 milliseconds), atOnce(1 users)),
    RelationScenarios.relationDiffScn.inject(nothingFor(2250 milliseconds), atOnce(1 users))
  )
  .protocols(httpProtocol)
  .assertions(global.failedRequests.count.is(0))
}

object checks {
  def headerCache = headerRegex("Cache-Control",
    """(max-age=0, ?(private, ?must-revalidate)|(must-revalidate, ?private))|(private, ?(max-age=0, ?must-revalidate)|(must-revalidate, ?max-age=0))|(must-revalidate, ?(private, ?max-age=0)|(max-age=0, ?private))|(no-cache)""").exists
  def headerNoCache = header("Cache-Control").is("no-cache")
  def contentType = header("Content-Type").is("text/xml; charset=utf-8")
  def isEmptyResponse = sha1.is("da39a3ee5e6b4b0d3255bfef95601890afd80709")

  def rootIsOsm = xpath("""/osm""").count.is(1)
  object osm {
    def license = xpath("""/osm/@license""").is("http://opendatacommons.org/licenses/odbl/1-0/")
    def attribution = xpath("""/osm/@attribution""").is("http://www.openstreetmap.org/copyright")
    def copyright = xpath("""/osm/@copyright""").is("OpenStreetMap and contributors")
    def version = xpath("""/osm/@version""").is("0.6")
  }
}

