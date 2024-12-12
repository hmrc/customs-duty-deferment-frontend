/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils

import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.hmrcfrontend.views.html.components.HmrcNewTabLink
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.newtablink.NewTabLink
import utils.Utils.emptyString
import views.html.components.{caption, h1, h2, inset, link, p}

object ViewUtils {

  val emptyH2Component: h2     = new h2()
  val emptyPComponent: p       = new p()
  val emptyLinkComponent: link = new link()

  def h1Component(msgKey: String, id: Option[String], classes: String = "govuk-heading-xl")(implicit
    messages: Messages
  ): HtmlFormat.Appendable =
    new h1().apply(msg = messages(msgKey), id = id, classes = classes)

  def h2Component(msg: String, id: Option[String] = None, h2Class: Option[String] = None): HtmlFormat.Appendable =
    new h2().apply(msg = msg, id = id, h2Class = h2Class)

  def linkComponent(input: LinkComponentValues)(implicit messages: Messages): HtmlFormat.Appendable =
    new link().apply(
      linkMessage = input.linkMessageKey,
      location = input.location,
      linkId = input.linkId,
      linkClass = input.linkClass,
      pWrapped = input.pWrapped,
      linkSentence = input.linkSentence,
      preLinkMessage = input.preLinkMessageKey,
      postLinkMessage = input.postLinkMessageKey,
      pId = input.pId,
      pClass = input.pClass
    )

  def pComponent(
    content: Html,
    id: Option[String] = None,
    classes: Option[String] = None,
    link: Option[Html] = None,
    tabLink: Option[Html] = None
  )(implicit messages: Messages): HtmlFormat.Appendable =
    new p().apply(content = content, id = id, classes = classes, link = link, tabLink = tabLink)

  def insetComponent(msg: String, id: Option[String] = None, classes: Option[String] = None)(implicit
    messages: Messages
  ): HtmlFormat.Appendable =
    new inset().apply(msg = msg, id = id, classes = classes)

  def captionComponent(msg: String, id: Option[String] = None, classes: String): HtmlFormat.Appendable =
    new caption().apply(msg = msg, id = id, classes = classes)

  def hmrcNewTabLinkComponent(
    text: String = emptyString,
    href: Option[String] = None,
    language: Option[String] = None,
    classList: Option[String] = None
  ): HtmlFormat.Appendable =
    new HmrcNewTabLink().apply(
      NewTabLink(
        language = language,
        classList = classList,
        href = href,
        text = text
      )
    )

  case class LinkComponentValues(
    linkMessageKey: String,
    location: String,
    linkId: Option[String] = None,
    linkClass: String = "govuk-link",
    pWrapped: Boolean = true,
    linkSentence: Boolean = false,
    preLinkMessageKey: Option[String] = None,
    postLinkMessageKey: Option[String] = None,
    pId: Option[String] = None,
    pClass: String = "govuk-body"
  )
}
