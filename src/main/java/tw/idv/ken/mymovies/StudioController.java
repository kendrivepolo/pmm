package tw.idv.ken.mymovies;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tw.idv.ken.mymovies.model.Studio;

@RooWebJson(jsonObject = Studio.class)
@Controller
@RequestMapping("/studios")
public class StudioController {

	@RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public ResponseEntity<String> listJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        List<Studio> result = Studio.findAllStudios("name", //sortFieldName
        		"ASC"//sortOrder
        		);
        return new ResponseEntity<String>(Studio.toJsonArray(result), headers, HttpStatus.OK);
    }
}
