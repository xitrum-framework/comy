package comy.controller

import org.jboss.netty.handler.codec.http._
import HttpMethod._

object Admin {
  val routes = List(
    (GET,  "/login", "Admin#login"),
    (POST, "/login", "Admin#login"),
    (GET,  "/admin", "Admin#index"))
}

class Admin extends Application {
  def login {
    if (env.method == GET)
      renderView
    else {
      val username = param("username")
      if (username == "admin")
        redirectTo("index")
      else
        renderView
    }
  }

  def index {
    renderView
  }
}
