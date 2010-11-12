package comy.controller

import xt.vc._

import org.jboss.netty.handler.codec.http.HttpMethod

class Admin extends Application {
  @Path("/admin/login")
  def login {
    if (request.getMethod == HttpMethod.GET)
      renderView
    else {
      val username = param("username")
      if (username == "admin")
        redirectTo("index")
      else
        renderView
    }
  }

  @Path("/admin")
  def index {
    renderView
  }
}
