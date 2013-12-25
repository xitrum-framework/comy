package comy.action

trait AppAction extends SetLanguage {
  override def layout = renderViewNoLayout[AppAction]()
}
