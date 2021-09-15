package models

import play.api.{Logger, LoggerLike}
import play.api.libs.json.{Format, JsString, JsSuccess, JsValue}
import play.api.mvc.PathBindable

sealed abstract class FileRole(val name: String, val featureName: String, val transactionName: String, val messageKey: String)

object FileRole {

  case object DutyDefermentStatement extends FileRole("DutyDefermentStatement", "duty-deferment", "Download duty deferment statement", "duty-deferment")

  val log: LoggerLike = Logger(this.getClass)

  def apply(name: String): FileRole = name match {
    case "DutyDefermentStatement" => DutyDefermentStatement
    case _ => throw new Exception(s"Unknown file role: $name")
  }

  def unapply(fileRole: FileRole): Option[String] = Some(fileRole.name)

  implicit val fileRoleFormat = new Format[FileRole] {
    def reads(json: JsValue) = JsSuccess(apply(json.as[String]))
    def writes(obj: FileRole) = JsString(obj.name)
  }

  implicit val pathBinder: PathBindable[FileRole] = new PathBindable[FileRole] {
    override def bind(key: String, value: String): Either[String, FileRole] = {
      value match {
        case "duty-deferment" => Right(DutyDefermentStatement)
        case fileRole => Left(s"unknown file role: ${fileRole}")
      }
    }

    override def unbind(key: String, fileRole: FileRole): String = {
      fileRole match {
        case DutyDefermentStatement => "duty-deferment"
        case _ => "unsupported-file-role"
      }
    }
  }

}