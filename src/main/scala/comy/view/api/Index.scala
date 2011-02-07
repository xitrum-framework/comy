package comy.view.api

import xt._

object Index extends View {
  def render(controller: Controller) = {
    <script type="text/javascript" src="/public/js/index.js"></script>

    <label for="url">URL:</label>
    <input id="url" type="text" value="http://mobion.jp/" tabindex="1" />
    <br />

    <label for="key">Key:</label>
    <input id="key" type="text" tabindex="2" />
    <span>(optional, a-z A-Z 0-9 _ -)</span>
    <br />

    <input id="submit" type="submit" value="Shorten" tabindex="3" />
    <br />
    <br />

    <hr />

    <label>Result:</label>
    <br />
    <span id="result"></span>
    <a id="open" href="" target="_blank">Open ↑</a>
    <br />

    <img id="qrcode" alt="QR code" width="150" height="150" src="" />
  }
}