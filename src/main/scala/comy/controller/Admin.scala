package comy.controller

import xt._
import comy.view.admin._

class Admin extends Application {
  @GET
  @POST
  @Path("/admin/login")
  def login {
    renderView(Login)
  }

  @POST
  @Path("/admin/login")
  def doLogin {
    val username = param("username")
    if (username == "admin")
      redirectTo("index")
    else
      renderView(Login)
  }

  @GET
  @Path("/admin")
  def index {
    renderView(Index)
  }
}
