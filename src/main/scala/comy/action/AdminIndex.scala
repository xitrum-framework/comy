package comy.action

import xt._

@GET("/admin")
class AdminIndex extends Application {
  def execute {
    renderView(<p>Admin page</p>)
  }
}
