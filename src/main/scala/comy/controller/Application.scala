package comy.controller

import xt._
import comy.Config

class Application extends Controller {
  protected def renderLayout(content: Any) {
    val s = docType + "\n" + layout(content)
    renderText(s)
  }

  protected val docType = """<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">"""

  protected def layout(content: Any) =
    <html lang='en' xml:lang='en' xmlns='http://www.w3.org/1999/xhtml'>
      <head>
        <meta content="text/html; charset=utf-8" http-equiv="content-type" />
        <title>URL Shortener</title>

        <link rel="stylesheet" type="text/css" media="all" href="/public/css/reset.css" />
        <link rel="stylesheet" type="text/css" media="all" href="/public/css/text.css" />
        <link rel="stylesheet" type="text/css" media="all" href="/public/css/960_24_col.css" />
        <link rel="stylesheet" type="text/css" media="all" href="/public/css/smart_corners.css" />
        <link rel="stylesheet" type="text/css" media="all" href="/public/css/index.css" />

        <script type="text/javascript" src="/public/js/jquery-1.4.2.min.js"></script>
      </head>

      <body>
        <div class="container_24">
          <div class="grid_15 prefix_3 suffix_6">
            <br />
            <br />

            <div id="csc">
              <span class="tr"></span>

              <h1 id="header"><a href="/">URL Shortener</a></h1>

              {if (Config.isAdminAllowed(remoteIp))
                <div>
                  {if (session("user") == null)
                    <a href="/admin/login">Login</a>
                  else
                    <b>hehe</b>
                  }
                </div>
              }

              {content}

              <div class="clear"></div>

              <span class="bl"></span>
              <span class="br"></span>
            </div>

            <h5 id="footer">&copy; 2010 GNT Inc.</h5>
          </div>
        </div>
      </body>
    </html>
}
