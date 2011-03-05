package comy.action.user

import xitrum.action.annotation.GET
import xitrum.action.validation.{Validate, Required, MaxLength, URL}

import comy.action.Application

@GET("/")
class Index extends Application {
  override def execute {
    renderView(
      <form postback="submit" action={urlForPostback[Shorten]}>
        <table>
          <tr>
            <td><label for="url">URL:</label></td>
            <td>{<input id="url" type="text" name="url" value="http://mobion.jp/" tabindex="1" /> :: Validate(Required, URL)}</td>
          </tr>
          <tr>
            <td><label for="key">Key:</label></td>
            <td>
              {<input type="text" name="key" tabindex="2" /> :: Validate(KeyValidator, MaxLength(32))}
              <span>(optional, a-z A-Z 0-9 _ -)</span>
            </td>
          </tr>
        </table>

        <input type="submit" value="Shorten" tabindex="3" />
      </form>

      <div id="result"></div>
    )
  }
}
