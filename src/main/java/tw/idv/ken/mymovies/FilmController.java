package tw.idv.ken.mymovies;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import tw.idv.ken.imageencrypt.ImageEncryptorIF;
import tw.idv.ken.mymovies.model.Film;
import tw.idv.ken.mymovies.model.Studio;
import tw.idv.ken.mymovies.service.FileServiceIF;
import tw.idv.ken.mymovies.service.SearchServiceIF;

@RooWebJson(jsonObject = Film.class)
@Controller
@RequestMapping("/films")
public class FilmController {
	/**
	 * Logger instance.
	 */
	private Logger Log = Logger.getLogger(FilmController.class);
	@Autowired
	private FileServiceIF fileService;
	@Autowired
	private SearchServiceIF searchService;
	/**
	 * ImageEncryptorIF instance to encrypt or decrypt image content.
	 */
	@Autowired
	private ImageEncryptorIF imageEncryptor;
	/**
	 * File name of cover image.
	 */
	private static final String COVER_IMAGE_NAME = "cover";

	@RequestMapping(headers = "Accept=application/json")
    @ResponseBody
	public ResponseEntity<String> listJson(
			@RequestParam(value = "ownerid", required = true) String ownerId, 
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = "0") int size) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		List<Film> result = new LinkedList<Film>();
		if (page > 0 && size > 0) {
			result = Film.loadFilmsByPage(ownerId, page, size);
		} else {
			result = Film.findAllFilms(ownerId);
		}
		return new ResponseEntity<String>(Film.toJsonArray(result), headers,
				HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}/cover", method = RequestMethod.GET)
	HttpEntity<byte[]> loadCoverImage(@PathVariable Long id,
			@RequestParam(value = "x", required = false, defaultValue = "1") int x,
			@RequestParam(value = "y", required = false, defaultValue = "1") int y) throws Throwable {
		byte[] imgByteArray = null;
		// send it back to the client
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.IMAGE_JPEG);

		if(x > 0 && y > 0) {
			imgByteArray = fileService.getFilmCoverImage(id ,x , y);
		} else {
		    imgByteArray = fileService.getFilmCoverImage(id);
		}
		if (imgByteArray != null && imgByteArray.length > 0) {
			return new ResponseEntity<byte[]>(imgByteArray, httpHeaders,
					HttpStatus.OK);
		}

		return new ResponseEntity<byte[]>(fileService.getDefaultCoverImage(),
				httpHeaders, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}/screenshots", method = RequestMethod.GET)
	public ResponseEntity<String> getScreenshotFilenames(
			@PathVariable Long id) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		String[] filenames = fileService.getFilmScreenshotFilenames(id);
		return new ResponseEntity<String>(screenshotFilenamesToJson(filenames), headers,
				HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}/screenshots/{filename}", method = RequestMethod.GET)
	HttpEntity<byte[]> loadScreenshotImage(@PathVariable Long id, @PathVariable String filename) throws Throwable {
		// send it back to the client
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.IMAGE_JPEG);

		byte[] imgByteArray = fileService.getFilmScreenshotImage(id, filename);
		if (imgByteArray != null && imgByteArray.length > 0) {
			return new ResponseEntity<byte[]>(imgByteArray, httpHeaders,
					HttpStatus.OK);
		}

		return new ResponseEntity<byte[]>(fileService.getDefaultScreenshotImage(),
				httpHeaders, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}/cover", method = RequestMethod.POST, headers = "content-type=multipart/form-data")
	ResponseEntity<String> uploadCoverImage(
			@PathVariable Long id,
			@RequestParam(value = "cover", required = false) MultipartFile coverImage,
			UriComponentsBuilder uriBuilder) throws Throwable {
		if (coverImage != null && !coverImage.isEmpty()) {
			fileService.saveFilmCoverImage(id, coverImage.getBytes());
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		RequestMapping a = (RequestMapping) getClass().getAnnotation(
				RequestMapping.class);
		headers.add("Location",
				uriBuilder.path(a.value()[0] + "/" + id.toString()).build()
						.toUriString());
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/{id}/screenshots", method = RequestMethod.POST, headers = "content-type=multipart/form-data")
	ResponseEntity<String> uploadScreenshotImages(
			@PathVariable Long id,
			@RequestParam(value = "screenshots", required = false) MultipartFile[] screenshots,
			UriComponentsBuilder uriBuilder) throws Throwable {
		Log.info("screenshots size: "
				+ ((screenshots == null) ? 0 : screenshots.length));
		if (screenshots != null && screenshots.length > 0) {
			fileService.saveFilmScreenshotImages(id,
					getByteArrayByMultipartFiles(screenshots));
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		RequestMapping a = (RequestMapping) getClass().getAnnotation(
				RequestMapping.class);
		headers.add("Location",
				uriBuilder.path(a.value()[0] + "/" + id.toString()).build()
						.toUriString());
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

	/**
	 * Get total film numbers, used for pagination.
	 * @return a josn string of film number
	 * @throws Throwable
	 */
	@RequestMapping(value = "/number", method = RequestMethod.GET)
	HttpEntity<String> getTotoalFilmNumber(
			@RequestParam(value = "ownerid", required = true) String ownerId) throws Throwable {
		// send it back to the client
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");

		long filmNumber = Film.countFilms(ownerId);
		return new ResponseEntity<String>("{\"totalFilmNumber\" : " + filmNumber
				+ "}", headers, HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.POST, headers = "content-type=multipart/form-data")
	public ResponseEntity<String> createFilm(
			@RequestParam("body") String json,
			@RequestParam(value = "cover", required = false) MultipartFile coverImage,
			@RequestParam(value = "screenshots", required = false) MultipartFile[] screenshots,
			UriComponentsBuilder uriBuilder) {
		Film film = Film.fromJsonToFilm(json);
		Studio studio = film.getStudio();

		if (studio != null && studio.getId() > 0) {
			studio = Studio.findStudio(studio.getId());
			film.setStudio(null);
			film.persist();
			film.setStudio(studio);
			film.merge();
		} else {
			//we can't just go to persist because Studio in json has an invalid
			//studio.id as -1
			Studio nStudio = new Studio();
			nStudio.setName(studio.getName());
			film.setStudio(nStudio);
			film.persist();
		}

		if (coverImage != null && !coverImage.isEmpty()) {
			try {
				fileService.saveFilmCoverImage(film.getId(),
						coverImage.getBytes());
			} catch (IOException e) {
				Log.error("read cover image fails !", e);
			}
		}

		Log.info("screenshots size: "
				+ ((screenshots == null) ? 0 : screenshots.length));
		if (screenshots != null) {
			fileService.saveFilmScreenshotImages(film.getId(),
					getByteArrayByMultipartFiles(screenshots));
		}
		
		//create Lucene indexes
		searchService.createSearchIndex(film);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		RequestMapping a = (RequestMapping) getClass().getAnnotation(
				RequestMapping.class);
		headers.add("Location",
				uriBuilder.path(a.value()[0] + "/" + film.getId().toString())
						.build().toUriString());
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

	/**
	 * Get the path of cover image of a film.
	 * @param filmID film id
	 * @param create true to create folder
	 * @return path of cover image file
	 */
	private String screenshotFilenamesToJson(String[] filenames) {
		if (filenames == null) {
			return "";
		}
		int i = 0;
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"filenames\" : [");
		for (String filename : filenames) {
			sb.append(filename);
			if (i < (filenames.length - 1)) {
				sb.append(",");
			}
			i++;
		}
		sb.append("]}");
		return sb.toString();
	}

	/**
	 * Convert MultipartFile array to byte array.
	 * @param files uploaded multipart files
	 * @return a 2 dimensional byte array
	 */
	private byte[][] getByteArrayByMultipartFiles(MultipartFile[] files) {
		if (files == null || files.length <= 0) {
			return null;
		}
		byte[][] result = new byte[files.length][];
		int i = 0;
		for (MultipartFile file : files) {
			try {
				result[i] = file.getBytes();
			} catch (IOException e) {
				Log.warn("read file content fails !", e);
				continue;
			}
			i++;
		}
		return result;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<String> updateFromJson(@RequestBody String json, @PathVariable("id") Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        try {
            Film film = Film.fromJsonToFilm(json);
            film.setId(id);
            if (film.merge() == null) {
                return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
            }
            
            //update Lucene indexes
    		searchService.updateSearchIndex(film);
            return new ResponseEntity<String>(headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("{\"ERROR\":"+e.getMessage()+"\"}", headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
