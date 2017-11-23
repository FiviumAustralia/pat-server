package fivium.pat.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@Deprecated
public class LegacyInternalServerUtils {

	public static final String DECODED_PATIENT_ID_KEY = "__DECDOED__p_id";
	//To do --  Fix the JWT_KEY to be something else.
	public static final byte[] JWT_KEY = "RNS".getBytes();

	private static Cipher cipher = null;
    private static final Random RANDOM = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    public static final String RNS_ROOT_PATH = System.getProperty("user.home") + File.separatorChar + "rns" + File.separatorChar;
    public static final String RNS_PROPS_PATH = RNS_ROOT_PATH + "props" + File.separatorChar;

    private static Log logger = LogFactory.getLog(LegacyInternalServerUtils.class);
  
	private static final List<String> SUPER_USER_GRAPHQL_QUERIES = Arrays.asList(
			"AddClinician",
			"DeleteClinician"
			);
	
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

	private static Cipher initCipher() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {
		
		BigInteger n = new BigInteger("7d7d66bb124b2f13c76fbb60fff378127773cbec78671da8a413e34a23f37cf627b992d67c20bb5bf6eb1d2cf6e0779f3da7a1bbdc78d3265b999fcd1537e71e4c635050032ff33c4e6a9521434f0130ead4ddbaf5ab02f9988ca1372aa3ac22e217c790fd76b684f96f12a64a2517121d7548b3d6aa1416984a96cd2792324c6ab82a8542fc15f988dd76085732663f471f95bc6ca8df510cecaf67d60f95d665e083e5b981dee7b9222014b4a2557ed6cc9aca78e15ce4c33c27e9c4b3f0f1ce23bc8b7d943206ac3f0fb4f041341644cd94048b0045e9cddb171934dccbaa51842832e2adc24f998e6469324937cda328ee5c60fd23d2fb2ae6516290951f", 16);
		BigInteger e = new BigInteger("010001", 16);
		BigInteger d = new BigInteger("64fd749d7c7d712793cb019b70a079b332dac7f4487983cee22cf730057ac0c840f4018e075b382d2cabce510f655a1f9738beda36f62dc7a2d4215ca484d6c4f568bf8b84e523f5168e83f1beff67cffc4ccf1cd4f4897261285bddcfbce49d362339022059574df0874914f67a9ef47f2c9213d8e00b469bccfb4ba35aee8a8e3b56b8fde8efe66289767d6259fe98b105e5589cb10b2d3d70cf2cbb3998af5e944bc0fa7598d68779e69f86dd2c5fed953cda7790286e89e94993da8a8221621b8d71ad9f852423b16e939b0fc045b05290d007131bf69feef3f79cff6cebc87f29dd9ef999f3be4da3abf2140cf537d93f1c03884884c1d8fa21c89ea6b1", 16);
		BigInteger p = new BigInteger("f58679913e97a6c60fb00496bfea8c2eaa64a3f865730cbdb9299b2b569d6e7de016ebb0d85e0ecdbc075dfba1ee8e476540ca99d579ab5d57d750bd83429176efda6e53c46bbdb0e05ecd09e594193aac6f45d6855868d5715b60343d1627f5f8d3b7875422195c497c35541436cb8d0888a2a19307cd9fdf71c950fe3c6ec5", 16);
		BigInteger q = new BigInteger("82d7f300985dba3221aef01fcccfc387b78f2e3b6c13007dc0fb7cb3cffa95d0606a2aba36779a570c556e3f05ce9f9d2196a2983c73db6ef6dd44252d9e3d0730e4cdf94ec6a5751976251cf17080de4a80915d0965b86f3c27b49c40c5b53b0870fac4628673c17b751ad59a04e02cbcffa6355c3b630238082726eaf8b293", 16);
		BigInteger dmp1 = new BigInteger("69ebbeac5355774440e64dc47f3cff86ee49869795a4a19d83b1185904ffdbf7ffe1c3824285a95b463fe362b844432b37da50cd36d44b82746fd64c3bc14f22016db964f7fb0715b466118cb07748bf103edce1241bb4f647f604b5e498975abd23112d17b5b23d2712a7ab03217484fe667bcabab48617ef7c963e13c01c85", 16);
		BigInteger dmq1 = new BigInteger("7463134d1c3b833cc4a98fff3978363dab1239d5b1b4d563071ead366572498129c2466f8db3a61e786225c6f9ab6a5fa3591318c7ed66ebb5bbecba05721467291eddf93e11856c3c51d3818362c7bef416993760fc92aa9f9fd2108bad522048c9f6c669bd9f8a23ff6ef74221c427079780894699ace074670c2c9a26cef1", 16);
		BigInteger coeff = new BigInteger("c6b2820ecd212dc2e683dd13907600e314cc4001146494484f211171b255a9a8439168cedaee745eb75d4c7210a483f48a8a31dbc0067c611e8bf15150fde41cb039f28f4d58d4374b1581136e5ff9a49bfe809d50bf342df1ae6917fb39cd65bb8f7a6dda0e0158cd83fc4dd313547b063ee1cd07e2867a5308ac1d06080f59", 16);
		
		KeySpec privateKeySpec = new RSAPrivateCrtKeySpec(n, e, d, p, q, dmp1, dmq1, coeff);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher;
	}

	public static void readData(String id, String x) {
		Properties prop = new Properties();
		OutputStream outputStream = null;

		try {
			String filePath = System.getProperty("user.dir") + "data.properties";
			File file = new File(filePath);
			outputStream = new FileOutputStream(file);
			prop.setProperty(id, x);
			prop.store(outputStream, null);

		} catch (IOException io) {
			logger.error("Unexpected IO error.", io);
			io.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					logger.error("Unexpected IO error.", e);
				}
			}

		}
	}

	public static String decrypt(String encryptedData) throws Exception {
		
		if (cipher == null) {
			cipher = initCipher();
		}

		byte[] dec = cipher.doFinal(hexStringToByteArray(encryptedData));
		return new String(dec, "UTF-8");

	}

	private static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];

		s = s.toUpperCase();
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
 
		return data;
	}


	public static void set400Reponse(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.getWriter().write(message);
	}
	
	/**
	 * Verifies the decoded user from the supplied token exists in the database
	 * @param jwt_Token token to decode
	 * @throws Exception When token cannot be decoded or user doesn't exist 
	 * @return role of user
	 */
	public static String verifyToken(String jwt_Token) throws Exception {		
		
		// do some basic validation before attempting to decode token
		if (null == jwt_Token || jwt_Token == "") {
			throw new Exception("Missing jwt_token");
		}

		// Setup JWT decoder
		Jws<Claims> claims = Jwts.parser().setSigningKey(LegacyInternalServerUtils.JWT_KEY).parseClaimsJws(jwt_Token);
		Claims claimsBody = claims.getBody();
		
		// get decoded clinician email
		String subject = claimsBody.getSubject();
		
		// fetch the clinician's role
		Object[] queryArgs = new Object[] { subject };
		Collection<Map<String, String>> result = PAT_DAO.executeStatement("SELECT Role from clinicians WHERE Email=?",queryArgs);
		String role = result.iterator().next().get("Role");
		
		return role;

	}
	
	public static boolean isValidRole(String role, String graphql_QueryContent) {
		
		// if request comes from a super user, always return true
		if ("superuser".equals(role)) {
			return true;
			
	    // request came from a non superuser and attempting to execute a non superuser query, so return true
		} else if (!SUPER_USER_GRAPHQL_QUERIES.contains(parseGraphQL_QueryName(graphql_QueryContent))) {
			return true;
			
		// request came from a non superuser and attempting to execute a superuser query, so return false
		} else {
			logger.warn("Non superuser just attempted to execute a superuser query!");
			return false;
		}
		
	}
	
	private static String parseGraphQL_QueryName (String graphQL_Query) {
		
		String queryName = null;
		
		if(graphQL_Query.contains("(")){
			queryName = graphQL_Query.substring(graphQL_Query.indexOf("{") + 1);
			queryName = queryName.substring(0, queryName.indexOf("(")).trim();
        } else{
        	queryName = graphQL_Query.substring(graphQL_Query.indexOf("{") + 1);
        	queryName = queryName.substring(0, queryName.indexOf("{")).trim();
        }
		
		return queryName;
		
	}
	
}
