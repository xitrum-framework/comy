package comy.action.user

import xitrum.action.annotation.GET
import xitrum.action.validation.{Required, MaxLength, URL}

import comy.action.Application

@GET("/")
class Index extends Application {
  def execute {
    renderView(
      <form post2="submit" action={urlFor[Shorten]}>
        <table>
          <tr>
            <td><label for="url">URL:</label></td>
            <td>{<input id="url" type="text" name="url" value="http://mobion.jp/" tabindex="1" />.validate(new Required, new URL)}</td>
          </tr>
          <tr>
            <td><label for="key">Key:</label></td>
            <td>
              {<input type="text" name="key" tabindex="2" />.validate(new KeyValidator, new MaxLength(32))}
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
