package comy.controller

import xt._

class Admin extends Application {
  @GET
  @POST
  @Path("/admin/login")
  def login {
    renderLayout(
      <form action="/admin/login" method="post">
        <label>Username:</label>
        <input type="text" name="username" />
        <input type="submit" value="Password »" />
      </form>)
  }

  @POST
  @Path("/admin/login")
  def doLogin {
    val username = param("username")
    if (username == "admin")
      redirectTo("index")
    else
      renderView
  }

  @GET
  @Path("/admin")
  def index {
    renderLayout(<p>Admin page</p>)
  }
}
