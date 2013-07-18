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

  val httpProtocol = http
    .baseURL("http://localhost:3000/api/0.6")
    .acceptHeader("text/xml")
    .disableFollowRedirect

  setUp(NodeScenario.nodeScn.inject(atOnce(1 users)))
  .protocols(httpProtocol)
  .assertions(global.failedRequests.count.is(0))
}