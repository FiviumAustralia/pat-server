package fivium.pat.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class PatUtils {
	
	private static Log logger = LogFactory.getLog(PatUtils.class);

	public static String getCurrentFormattedDate() {
		logger.info("Entering getCurrentFormattedDate...");
		// get today's date
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String today = df.format(date);
		String lastDate = df.format(date);
		return lastDate;
	}

	public static String getFormattedDate(String dateToBeFormatted) {
		logger.info("Entering getFormattedDate...");
		// get today's date
		try {
			DateFormat format = new SimpleDateFormat( "yyyy-MM-dd",Locale.US);
			Date date;
			date = format.parse(dateToBeFormatted);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String lastDate = df.format(date);
			return lastDate;
		} catch (ParseException e) {
			logger.error("Parse exeption occurred while formatting the date. "+e);
		}
		return null;
	}

	public static String getUserIdFromJWT(String token) {
		Jws<Claims> claims = Jwts.parser().setSigningKey(Constants.JWT_KEY).parseClaimsJws(token);
		Claims claimsBody = claims.getBody();
		String subject = claimsBody.getSubject();
		return subject;
	}
	public static String returnLatestTimeStamp(String lastSyncDate, String lastSyncTime) {
		Timestamp ts = Timestamp.valueOf(lastSyncDate);
		Timestamp ts2 = Timestamp.valueOf(lastSyncTime);
		if(ts.after(ts2)) {
			return ts.toString();
		}
		return ts2.toString();
	}
}
