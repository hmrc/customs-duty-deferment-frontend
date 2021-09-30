/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package util

import models.{ContactDetailsUserAnswers, UserAnswers}
import play.api.libs.json.Json

trait TestUserAnswers {
  this: TestContactDetails =>

  val contactDetailsUserAnswers: ContactDetailsUserAnswers = ContactDetailsUserAnswers(
    validDan,
    Some("Example Name"),
    "Example Road",
    None,
    None,
    None,
    None,
    "GB",
    Some("United Kingdom"),
    Some("11111 222333"),
    None,
    Some("example@email.com")
  )

  val userAnswersId = "id"

  def emptyUserAnswers: UserAnswers = UserAnswers(userAnswersId, Json.obj())

  def someUserAnswers: UserAnswers = UserAnswers("answer", Json.obj())

}
