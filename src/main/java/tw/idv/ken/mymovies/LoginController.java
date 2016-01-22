/**
 * 
 */
package tw.idv.ken.mymovies;

import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import flexjson.JSONDeserializer;
import tw.idv.ken.mymovies.model.Owner;

/**
 * @author ken Controller to handle log in.
 */
@Controller
@RequestMapping("/accesstoken")
public class LoginController {
	/**
	 * Logger instance.
	 */
	private Logger Log = Logger.getLogger(LoginController.class);

	@RequestMapping(method = RequestMethod.POST, 
			headers = "Accept=application/json", 
			produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> login(/*@RequestParam("userid") String userId,
			@RequestParam("password") String passwd*/
			@RequestBody String json
			) {
		Credential cred = Credential.fromJsonToCredential(json);
		String userId = cred.getUserId();
		String passwd = cred.getPasswd();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		Log.debug(String.format("validate user(%s) with password(%s)", userId,
				passwd));
		try {
			Owner owner = Owner.findOwnerByOwnerId(userId);
			Log.debug("User(" + userId + ") found");
			if (owner.getPassword().equalsIgnoreCase(passwd)) {
				return new ResponseEntity<String>("{\"authenticated\":"
						+ Boolean.TRUE.toString() + ",\"userkey\":" + 
						owner.getId() + "}", headers,
						HttpStatus.OK);
			}
			Log.debug(String.format("password not match. input(%s), in db(%s)",
					passwd, owner.getPassword()));
		} catch (Exception e) {
			Log.warn("validate user fails", e);
			return new ResponseEntity<String>("{\"authenticated\":"
					+ Boolean.FALSE.toString() + ",\"userkey\":-1}", headers,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<String>("{\"authenticated\":"
				+ Boolean.FALSE.toString() + ",\"userkey\":-1}", headers,
				HttpStatus.OK);
	}
	
	static class Credential{
		private String userId;
		private String passwd;
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getPasswd() {
			return passwd;
		}
		public void setPasswd(String passwd) {
			this.passwd = passwd;
		}
		
		public static Credential fromJsonToCredential(String json) {
			return new JSONDeserializer<Credential>()
			        .use(null, Credential.class).deserialize(json);
		}
		
	}
}
