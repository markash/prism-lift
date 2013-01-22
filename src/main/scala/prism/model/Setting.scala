package prism.model;

import net.liftweb.mapper._
import net.liftweb.sitemap.{Menu, Loc}
import net.liftweb.sitemap.Loc._
import prism.lib.UserMustBeLoggedIn
import net.liftweb.http.S._
import net.liftweb.http.{LiftResponse, RedirectResponse, S}
import net.liftweb.common.{Full, Box, Empty}
import javax.mail.{PasswordAuthentication, Authenticator}
import net.liftweb.util.Mailer

object Settings {
	
	private val mailSmtpStartTLS = "mail.smtp.starttls.enable";
	private val mailSmtpSSL = "mail.smtp.ssl.enable";
	private val mailSmtpHost = "mail.smtp.host";
	private val mailSmtpPort = "mail.smtp.port";
	private val mailSmtpAuth = "mail.smtp.auth";
	private val mailSmtpUser = "mail.smtp.user";
	private val mailSmtpPwd = "mail.smtp.pwd";
	private val mailSmtpFrom = "mail.smtp.from"
	private val mailDebug = "mail.debug"

	def settings = Setting.findAll

	def settingFilteredByName(name: String ): List[Setting] = for (setting <- settings; if setting.name == name) yield setting
	
	def setting(name: String): Option[String] = {
		val s = settingFilteredByName(name)
		if (s.length == 1) Some(s(0).value) else None
	}

	def emailFrom:Option[String] = setting(mailSmtpFrom)

	def setupEmail() = {
		System.setProperty(mailSmtpStartTLS, setting(mailSmtpStartTLS).getOrElse("false"));
		System.setProperty(mailSmtpSSL, setting(mailSmtpSSL).getOrElse("false"))
		System.setProperty(mailSmtpHost, setting(mailSmtpHost).getOrElse("localhost"))
		System.setProperty(mailSmtpPort, setting(mailSmtpPort).getOrElse("25"))
		System.setProperty(mailSmtpAuth, setting(mailSmtpAuth).getOrElse("false"))
		System.setProperty(mailSmtpFrom, setting(mailSmtpFrom).getOrElse("noreply" + S.hostName))
		System.setProperty(mailDebug, setting(mailDebug).getOrElse("false"))

		Mailer.authenticator = Full(new Authenticator {
	      override def getPasswordAuthentication =
	        new PasswordAuthentication(setting(mailSmtpUser).getOrElse("user"), setting(mailSmtpPwd).getOrElse("password"))
	    })
	}
}

object Setting extends Setting with LongKeyedMetaMapper[Setting] {
	override def dbTableName = "setting"
}

class Setting extends LongKeyedMapper[Setting] with IdPK with CreatedUpdated with OneToMany[Long, Setting] {
	def getSingleton = Setting
	object name extends MappedString(this, 150)
	object value extends MappedString(this, 150)
}
