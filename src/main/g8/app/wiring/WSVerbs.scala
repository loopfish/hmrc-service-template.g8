package wiring

import uk.gov.hmrc.play.http.hooks.HttpHook
import uk.gov.hmrc.play.http.ws._

class WSVerbs extends WSHttp {
  override val hooks: Seq[HttpHook] = NoneRequired
}