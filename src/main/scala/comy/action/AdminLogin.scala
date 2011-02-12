package comy.action

import xt._

@GET("/admin/login")
class AdminLogin extends Application {
  def execute {
    renderView(
      <form action="/admin/login" method="post">
        <label>Username:</label>
        <input type="text" name="username" />
        <input type="submit" value="Password »" />
      </form>
    )
  }

//  @POST("/admin/login")
  def doLogin {
  }
}
