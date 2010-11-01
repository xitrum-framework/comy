package comy.controller

import xt.framework.Controller

object Errors {
  val routes = Map(
    "404" -> "Errors#error404",
    "500" -> "Errors#error500")
}

class Errors extends Controller {
  def error404 {
    renderText("Not Found")
  }

  def error500 {
    renderText("Internal Server Error")
  }
}
