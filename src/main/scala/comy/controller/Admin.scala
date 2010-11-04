package comy.controller

import xt.framework.Controller

import org.jboss.netty.handler.codec.http._
import HttpMethod._

object Admin {
  val routes = List(
    (GET, "/admin", "Admin#index"))
}

class Admin extends Controller {
  def index {
    render
  }
}
