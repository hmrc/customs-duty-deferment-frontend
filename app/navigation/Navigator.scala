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

package navigation

import com.google.inject.{Inject, Singleton}
import utils.Utils.emptyString

@Singleton
class Navigator @Inject()() {
  val dutyDefermentStatementPageId = "duty-deferment-statement"
  val dutyDefermentStatementNAPageId = "duty-deferment-statement-na"

  def backLinkUrlForServiceUnavailablePage(id: String,
                                           linkId: String = emptyString): Option[String] =
    id match {
      case pageId if pageId == dutyDefermentStatementPageId =>
        Some(controllers.routes.AccountController.showAccountDetails(linkId).url)
      case pageId if pageId == dutyDefermentStatementNAPageId =>
        Some(controllers.routes.AccountController.statementsUnavailablePage(linkId).url)
      case _ => None
    }
}
