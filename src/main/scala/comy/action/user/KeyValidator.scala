package comy.action.user

import scala.xml.Elem

import xitrum.Action
import xitrum.validation.Validator

object KeyValidator extends Validator {
  def render(action: Action, elem: Elem, paramName: String): Elem = {
    import action._

    jsAddToView("""
      $.validator.addMethod(
        "comyKey",
        function(value, element) {
          return value.match(/^[a-zA-Z0-9_\-]*$/);
        },'""" + t("Invalid.") + """'
      );
    """)

    val js = js$name(paramName) + "." + "rules('add', {comyKey: true})"
    jsAddToView(js)

    elem
  }

  def validate(action: Action, paramName: String): Boolean = {
    val value = action.param(paramName).trim
    """^[a-zA-Z0-9_\-]*$""".r.findFirstIn(value).isDefined
  }
}
