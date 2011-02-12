package comy.action

import xt._

class AdminIndex extends Application {
  @GET("/admin")
  def execute {
    renderView(<p>Admin page</p>)
  }
}
