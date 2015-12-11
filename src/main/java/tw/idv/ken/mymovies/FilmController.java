package tw.idv.ken.mymovies;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "size", required = false, defaultValue = "0") int size) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		List<Film> result = new LinkedList<Film>();
		if (page > 0 && size > 0) {
			result = Film.loadFilmsByPage(page, size);
		} else {
			result = Film.findAllFilms();
		}
		return new ResponseEntity<String>(Film.toJsonArray(result), headers,
				HttpStatus.OK);
	}
	
	@RequestMapping(value = "/{id}/cover", method = RequestMethod.GET)
	HttpEntity<byte[]> loadCoverImage(@PathVariable Long id) throws Throwable {
		// send it back to the client
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.IMAGE_JPEG);

		byte[] imgByteArray = fileService.getFilmCoverImage(id);
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
	HttpEntity<String> getTotoalFilmNumber() throws Throwable {
		// send it back to the client
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");

		long filmNumber = Film.countFilms();
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
}