package comy

import java.util.{Date, Calendar}
import java.text.SimpleDateFormat

object Utils {
  
  def formatDate(date: Date): String = {
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyyMMdd")
	dateFormat.format(date)
  }
  
  def getExpirationDateString(expirationDate: Int): String = {
    val currentDate: Date = new Date
	val cal:Calendar = Calendar.getInstance();
	cal.setTime(currentDate)
	cal.add(Calendar.DATE, - expirationDate)
	formatDate(cal.getTime).toString
  }
}