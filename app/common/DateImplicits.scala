package common

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import scala.util.Try

object DateImplicits {

    val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    implicit class LocalDateTimeExtensions(dateTime: LocalDateTime) {

        def formatted: Option[String] = {
            Try(dateTime.format(formatter)).toOption
        }

    }

    implicit class OptionLocalDateTimeExtensions(dateTime: Option[LocalDateTime]) {

        def formatted: Option[String] = {
            dateTime.flatMap(_.formatted)
        }

    }

    implicit class StringExtensions(dateString: String) {

        def parseAsDateTime: Option[LocalDateTime] = {
            Try(LocalDateTime.from(
                formatter.parse(dateString)
            )).toOption
        }

    }

}
