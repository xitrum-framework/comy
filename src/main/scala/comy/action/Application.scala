package comy.action

import scala.collection.mutable.ArrayBuffer
import xitrum._

import comy.{Config => ComyConfig}
import comy.action.user.Index
import comy.action.admin.{Login, Logout}

trait Application extends Action {
  override def layout = Some(() =>
    DocType.xhtmlTransitional + "\n" +
    <html lang='en' xml:lang='en' xmlns='http://www.w3.org/1999/xhtml'>
      <head>
        <meta content="text/html; charset=utf-8" http-equiv="content-type" />
        <title>URL Shortener</title>

        <link rel="stylesheet" type="text/css" media="all" href="/public/css/960/reset.css" />
        <link rel="stylesheet" type="text/css" media="all" href="/public/css/960/text.css" />
        <link rel="stylesheet" type="text/css" media="all" href="/public/css/960/960_24_col.css" />
        <link rel="stylesheet" type="text/css" media="all" href="/public/css/smart_corners.css" />
        <link rel="stylesheet" type="text/css" media="all" href="/public/css/application.css" />
        <link rel="stylesheet" type="text/css" media="all" href="/public/css/index.css" />

        {jsHead}
      </head>

      <body>
        <div class="container_24">
          <div class="grid_15 prefix_3 suffix_6">
            <br />
            <br />

            <div id="csc">
              <span class="tr"></span>

              <h1 id="header"><a href={urlFor[Index]}>URL Shortener</a></h1>

              {if (ComyConfig.isAdminAllowed(remoteIp))
                <div>
                  {if (session("username").isEmpty)
                    <a href={urlFor[Login]}>Login</a>
                  else
                    <b>{session("username").get} </b>
                    <a href="#" postback="click" action={urlFor[Logout]}>Logout</a>
                  }
                </div>
              }

              <div id="flash">{if (flash.isDefined) flash.get}</div>

              {at("contentForLayout")}

              <div class="clear"></div>

              <span class="bl"></span>
              <span class="br"></span>
            </div>

            <h5 id="footer">&copy; 2010 GNT Inc.</h5>
          </div>
        </div>

        {jsForView}
      </body>
    </html>
  )
}
