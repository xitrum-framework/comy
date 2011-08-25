package comy.action

import xitrum.Action
import xitrum.view.DocType

import comy.{Config => ComyConfig}
import comy.action.user.{Index => UserIndex}
import comy.action.admin.{Index => AdminIndex, Login, Logout}

trait AppAction extends Action {
  override def layout = DocType.xhtmlTransitional(
    <html lang='en' xml:lang='en' xmlns='http://www.w3.org/1999/xhtml'>
      <head>
        {antiCSRFMeta}
        {xitrumCSS}

        <meta content="text/html; charset=utf-8" http-equiv="content-type" />
        <title>URL Shortener</title>

        <link rel="stylesheet" type="text/css" media="all" href={urlForPublic("css/960/reset.css")} />
        <link rel="stylesheet" type="text/css" media="all" href={urlForPublic("css/960/text.css")} />
        <link rel="stylesheet" type="text/css" media="all" href={urlForPublic("css/960/960_24_col.css")} />
        <link rel="stylesheet" type="text/css" media="all" href={urlForPublic("css/smart_corners.css")} />
        <link rel="stylesheet" type="text/css" media="all" href={urlForPublic("css/application.css")} />
        <link rel="stylesheet" type="text/css" media="all" href={urlForPublic("css/index.css")} />
      </head>

      <body>
        <div class="container_24">
          <div class="grid_15 prefix_3 suffix_6">
            <br />
            <br />

            <div id="csc">
              <span class="tr"></span>

              <h1 id="header"><a href={urlFor[UserIndex]}>URL Shortener</a></h1>

              {if (ComyConfig.isAdminAllowed(remoteIp))
                if (!SVar.username.isDefined)
                  <a href={urlFor[Login]}>Login</a>
                else
                  <xml:group>
                    <b>{SVar.username.get}</b>
                    <a href={urlFor[AdminIndex]}>Admin</a> |
                    <a href="#" postback="click" action={urlForPostback[Logout]}>Logout</a>
                  </xml:group>
              }

              <div id="flash">{jsFlash}</div>

              {renderedView}

              <div class="clear"></div>

              <span class="bl"></span>
              <span class="br"></span>
            </div>

            <h5 id="footer">Powered by <a href="https://github.com/ngocdaothanh/comy">Comy</a></h5>
          </div>
        </div>

        {jsAtBottom}
      </body>
    </html>
  )
}
