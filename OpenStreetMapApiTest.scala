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
    NodeScenarios.nodeDiffScn.inject(nothingFor(1 seconds), atOnce(1 users)),
    NodesScenarios.nodesScn.inject(nothingFor(2 seconds), atOnce(1 users)),
    NodesScenarios.nodesDiffScn.inject(nothingFor(2 seconds), atOnce(1 users))
  )
  .protocols(httpProtocol)
  .assertions(global.failedRequests.count.is(0))
}
