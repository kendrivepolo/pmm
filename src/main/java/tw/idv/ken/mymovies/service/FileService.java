/**
 * Implementations of file related functionalities.
 */
package tw.idv.ken.mymovies.service;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tw.idv.ken.imageencrypt.ImageEncryptorIF;

/**
 * @author ken
 *
 */
@Service
public class FileService implements FileServiceIF {
	/**
	 * Logger instance.
	 */
	private Logger Log = Logger.getLogger(FileService.class);
	/**
	 * ImageEncryptorIF instance to encrypt or decrypt image content.
	 */
	@Autowired
	private ImageEncryptorIF imageEncryptor;
	@Value( "${userdata.path}" )
	private String userdataPath;
	@Value("${cover_image.default}")
	private String defaultCoverImageBase64;
	@Value("${screenshot_image.default}")
	private String defaultScreenshotImageBase64;
	/**
	 * File name of cover image.
	 */
	private static final String COVER_IMAGE_NAME = "cover";
	/**
	 * Name of screenshot folder.
	 */
	private static final String SCREENSHOT_FOLDER_NAME = "screenshot";
	
	/* (non-Javadoc)
	 * @see tw.idv.ken.mymovies.service.FileServiceIF#getFilmImagePath(int)
	 */
	@Override
	public String getFilmImagePath(long filmID, boolean create) {
		String path = userdataPath + "/" + filmID + "/images";
		try {
			File folder = new File(path);
			if (!folder.exists() && create) {
				folder.mkdirs();
			}
		} catch (Exception e) {}
		return path;
	}

	@Override
	public byte[] getDefaultCoverImage() {
		Base64 codec = new Base64();
		return codec.decode(defaultCoverImageBase64);
	}

	@Override
	public int getFileNumber(String folder) {
		File dir = new File(folder);
		if (dir.isFile() || !dir.exists()) { return 0; }
		return dir.listFiles().length;
	}

	@Override
	public String getFilmScreenshotImagesPath(long filmID, boolean create) {
		String imgFolderPath = getFilmImagePath(filmID, create);
		File screenshotFolder = new File(imgFolderPath + "/" + SCREENSHOT_FOLDER_NAME);
		if (!screenshotFolder.exists() && create) {
			screenshotFolder.mkdirs();
		}
		return screenshotFolder.getAbsolutePath();
	}

	@Override
	public void saveFilmCoverImage(long filmID, byte[] image) {
		String imgFolderPath = getFilmImagePath(filmID, true);
		String imagePath = imgFolderPath + "/" + COVER_IMAGE_NAME;
		try {
			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(new File(imagePath)));
			stream.write(imageEncryptor.encrypt(image));
			stream.close();
		} catch (Exception e) {
			Log.error("save cover image fails !", e);
		}
	}

	@Override
	public void saveFilmScreenshotImages(long filmID, byte[][] images) {
		if (images == null || images.length <= 0) {
			return;
		}

		String scFolderPath = getFilmScreenshotImagesPath(filmID, true);
		int fileNumber = getFileNumber(scFolderPath);

		for (byte[] image : images) {
			try {
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(new File(scFolderPath + "/"
								+ fileNumber)));
				stream.write(imageEncryptor.encrypt(image));
				stream.close();
				fileNumber++;
				Log.debug("save screenshot[" + fileNumber + "] in "
						+ scFolderPath);
			} catch (Exception e) {
				continue;
			}
		}
	}

	@Override
	public byte[] getFilmCoverImage(long filmID) {
		String coverImagePath = getFilmImagePath(filmID, false)
				+ "/" + COVER_IMAGE_NAME;
		return getImage(coverImagePath);
	}

	@Override
	public byte[] getFilmCoverImage(long filmID,int x, int y) {
		String coverImagePath = getFilmImagePath(filmID, false)
				+ "/" + COVER_IMAGE_NAME;
		return getCroppedImage(coverImagePath, x, y);
	}

	@Override
	public String[] getFilmScreenshotFilenames(long filmID) {
		String screenshotImageFolder = getFilmScreenshotImagesPath(filmID, false);
		File dir = new File(screenshotImageFolder);
		if (!dir.exists()) return null;
		
		int i = 0;
		String[] filenames = new String[this.getFileNumber(screenshotImageFolder)];
		for (File file : dir.listFiles()) {
			filenames[i] = file.getName();
			i++;
		}
		Arrays.sort(filenames);
		return filenames;
	}

	@Override
	public byte[] getFilmScreenshotImage(long filmID, String filename) {
		String screenshotImageFolder = getFilmScreenshotImagesPath(filmID, false);
		return getImage(screenshotImageFolder + "/" + filename);
	}
	
	private byte[] getImage(final String imageFilePath) {
		File imageFile = new File(imageFilePath);
		byte[] imgByteArray = null;
		if (imageFile.exists()) {
			try {
				imgByteArray = FileUtils.readFileToByteArray(imageFile);
			} catch (IOException e) {
				Log.error("read image fails !", e);
				return null;
			}
			if (imgByteArray.length > 0) {
				return imageEncryptor.decrypt(imgByteArray);
			}
		}
		return null;
	}
	
	private byte[] getCroppedImage(final String imageFilePath, int xRatio, int yRatio) {
		File imageFile = new File(imageFilePath);
		byte[] imgByteArray = null;
		//x position of original images to crop image
		int xPos = 0;
		//y position of original images to crop image
		int yPos = 0;
		//width of the cropped image
		int width = 0;
		//height of cropped image
		int height = 0;
		//width of the original image
		int oWidth = 0;
		//height of the original image
		int oHeight = 0;
		
		
		if (!imageFile.exists())
			return null;

		try {
			imgByteArray = FileUtils.readFileToByteArray(imageFile);
			ByteArrayInputStream bais = new ByteArrayInputStream(
					imageEncryptor.decrypt(imgByteArray));
			BufferedImage oImg = ImageIO.read(bais);
		    oWidth = oImg.getWidth();
			oHeight = oImg.getHeight();
			
			double targetRatio = xRatio / ((double) yRatio);
			double originalRation = oWidth / ((double) oHeight);
			
			if (targetRatio > originalRation) { // keep width unchanged
				width = oWidth;
				height = (new Double(oWidth / targetRatio)).intValue();
			} else {
				width = (new Double(oHeight * targetRatio)).intValue();
				height = oHeight;
			}
			
			xPos = oWidth - width;
			yPos = 0;
			
			BufferedImage nImg = oImg.getSubimage(xPos, yPos, width, height);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write( nImg, "jpg", baos );
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			baos.close();
			return imageInByte;
		} catch (IOException e) {
			Log.error("read image fails !", e);
			return null;
		}
	}

	@Override
	public byte[] getDefaultScreenshotImage() {
		Base64 codec = new Base64();
		return codec.decode(defaultScreenshotImageBase64);
	}

	@Override
	public void deleteFilmFolder(long filmId) {
		String path = userdataPath + "/" + filmId;
		File folder = new File(path);
		try {
			FileUtils.deleteDirectory(folder);
		} catch (IOException e) {
			Log.warn("delete folder of film " + filmId + " fails", e);
		}
	}

}
