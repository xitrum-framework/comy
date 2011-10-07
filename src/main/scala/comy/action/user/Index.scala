package comy.action.user

import xitrum.annotation.GET
import xitrum.validation.{Required, MaxLength, URL}

import comy.action.AppAction

@GET("/")
class Index extends AppAction {
  override def execute {
    renderView(
      <form postback="submit" action={urlForPostback[Shorten]}>
        <table>
          <tr>
            <td><label for="url">{t("URL")}:</label></td>
            <td>{<input id="url" type="text" name="url" value="http://mobion.jp/" tabindex="1" /> :: Required :: URL}</td>
          </tr>
          <tr>
            <td><label for="key">{t("Key")}:</label></td>
            <td>
              {<input type="text" name="key" tabindex="2" /> :: MaxLength(32) :: KeyValidator}
              <span>{t("(optional, a-z A-Z 0-9 _ -)")}</span>
            </td>
          </tr>
        </table>

        <input type="submit" value={t("Shorten")} tabindex="3" />
      </form>

      <div id="result"></div>
    )
  }
}
