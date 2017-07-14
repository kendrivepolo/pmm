package tw.idv.ken.mymovies;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import flexjson.JSONSerializer;
import tw.idv.ken.mymovies.model.FilmSearchResult;
import tw.idv.ken.mymovies.service.SearchServiceIF;

@Controller
@RequestMapping("/search")
public class SearchController {
	@Autowired
	private SearchServiceIF searchService;

	@RequestMapping(value="/{ownerId}/{keyword}", method=RequestMethod.GET)
    @ResponseBody
	public ResponseEntity<String> searchFilms(@PathVariable String ownerId,
			@PathVariable String keyword) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		List<FilmSearchResult> result = searchService.searchFilms(ownerId, keyword);
		String jsonString = new JSONSerializer().exclude("*.class").serialize(
				result);

		return new ResponseEntity<String>(jsonString, headers, HttpStatus.OK);
	}
	
	private String conv2UTF8(String source){
        String result = "";
        try{
            result = new String(source.getBytes("iso8859-1"),"UTF-8");
        }catch(Exception e){
        }
        return result ;
    }
}
