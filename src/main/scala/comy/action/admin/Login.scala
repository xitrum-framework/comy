package comy.action.admin

import xitrum._
import xitrum.vc.validator._

import comy.action.Application

@GET("/admin/login")
class Login extends Application {
  def execute {
    renderView(
      <form post2="submit">
        <div id="error"></div>

        <label>Username:</label> {<input type="text" name="username" />.validate(new Required)}
        <br />
        <input type="submit" value="Password »" />
      </form>
    )
  }
}
