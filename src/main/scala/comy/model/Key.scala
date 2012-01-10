package comy.model

import xitrum.I18n
import xitrum.validator._

case class Key(value: String) {
  val ALLOW_CHARS = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890_-"

  def v(i18n: I18n) = {
    val invalidCharIncluded = value.exists(c => ALLOW_CHARS.indexOf(c) == -1)
    val erroro = if (invalidCharIncluded) Some(i18n.t("Key contains invalid character")) else None

    Required.v("Key", value) orElse
    MaxLength(32).v("Key", value) orElse
    erroro
  }
}
