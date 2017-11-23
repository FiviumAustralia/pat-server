package fivium.pat.utils;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@Deprecated
public class RnsUtils {

	private static Log logger = LogFactory.getLog(RnsUtils.class);

	public static Map<String, Object> parseJsonRequest(HttpServletRequest httpRequest) throws IOException {

		Gson gson = new Gson();
		Map<String, Object> jsonRequestObject = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String s;
		while ((s = httpRequest.getReader().readLine()) != null) {
			sb.append(s);
		}
		String json = sb.toString();
		jsonRequestObject = gson.fromJson(json, Map.class);
		return jsonRequestObject;
	}

	public static void set400Reponse(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.getWriter().write(message);
	}

	public static byte[] getNextSalt() {
		byte[] salt = new byte[16];
		Constants.RANDOM.nextBytes(salt);
		System.out.println(salt);
		return salt;
	}

	public static byte[] hash(char[] password, byte[] salt) {
		PBEKeySpec spec = new PBEKeySpec(password, salt, Constants.ITERATIONS, Constants.KEY_LENGTH);
		Arrays.fill(password, Character.MIN_VALUE);
		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			return skf.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
		} finally {
			spec.clearPassword();
		}
	}

	public static boolean isExpectedPassword(char[] password, byte[] salt, byte[] expectedHash) {
		byte[] pwdHash = hash(password, salt);
		Arrays.fill(password, Character.MIN_VALUE);
		if (pwdHash.length != expectedHash.length)
			return false;
		for (int i = 0; i < pwdHash.length; i++) {
			if (pwdHash[i] != expectedHash[i])
				return false;
		}
		return true;
	}

	public static boolean verifyToken(String jwt_Token, String table) {
		if (null != jwt_Token && jwt_Token != "") {
			try {
				Jws<Claims> claims = Jwts.parser().setSigningKey(Constants.JWT_KEY).parseClaimsJws(jwt_Token);
				Claims claimsBody = claims.getBody();
				String subject = claimsBody.getSubject();
				logger.debug("Authenticating the user " + subject);
				Object[] queryArgs = new Object[] { subject, jwt_Token };
				Collection<Map<String, String>> result;
				if ("patient".equals(table)) {
					result = PAT_DAO
							.executeStatement("SELECT p_id, Active, Token from $tableName WHERE p_id= ? AND Token = ? and Active like 'Active'"
									.replace("$tableName", table), queryArgs);
				} else {
					result = PAT_DAO.executeStatement("SELECT * from $tableName WHERE User=?".replace("$tableName", table),
							queryArgs);
				}

				if (!result.isEmpty()) {
					return true;
				}
			} catch (Exception e) {
				logger.debug("verification error: " + e.getLocalizedMessage());
				logger.error("Password Verification Error", e);
				return false;
			}
		}
		return false;
	}
}
