/**
 * 
 */
package tw.idv.ken.mymovies;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tw.idv.ken.mymovies.model.MediaType;

/**
 * MVC controller of MediaType enum class to list all media types.
 * @author ken
 *
 */
@Controller
@RequestMapping("/mediatypes")
public class MediaTypeController {
	/**
	 * List all media types.
	 * @return a json sting containing all media types.
	 */
	@RequestMapping(headers = "Accept=application/json")
    @ResponseBody
	public ResponseEntity<String> listMediaTypes() {
    	StringBuilder sb =new StringBuilder();
    	int count = 0;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        sb.append("[");
        for (MediaType type: MediaType.values()) {
        	sb.append("\"");
        	sb.append(type);
        	sb.append("\"");
        	if (count < (MediaType.values().length - 1)) {sb.append(","); }
        	count ++;
        }
        sb.append("]");
        return new ResponseEntity<String>(sb.toString(), headers, HttpStatus.OK);
    }
}
