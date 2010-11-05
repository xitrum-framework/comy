package comy

import xt.server.Server
import xt.framework.XTApp
import xt.middleware.{
  App,
  Failsafe, Session, EhcacheSessionStore, Cookie, Dispatcher, MethodOverride, ParamsParser, Static}

import comy.controller._

object Boot {
  def main(args: Array[String]) {
    // Avoid error when creating QRCode when running in console-only environment
    System.setProperty("java.awt.headless", "true")

    val routes      = Admin.routes ++ Api.routes
    val errorRoutes = Errors.routes

    val controllerPaths = List("comy.controller")
    val viewPaths       = List("comy.view")

    var app: App = new XTApp
    app = Failsafe.wrap(app)
    app = Session.wrap(app, new EhcacheSessionStore)
    app = Cookie.wrap(app)
    app = Dispatcher.wrap(app, routes, errorRoutes, controllerPaths, viewPaths)
    app = MethodOverride.wrap(app)
    app = ParamsParser.wrap(app)
    app = Static.wrap(app)

    val http = new Server(app)
    http.start
  }
}
