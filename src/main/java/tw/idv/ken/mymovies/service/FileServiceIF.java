/**
 * Interface of all file related functionalities.
 */
package tw.idv.ken.mymovies.service;

/**
 * @author ken
 *
 */
public interface FileServiceIF {
	/**
	 * Compute the path to store all images of a film, includes cover and screenshot images,
	 * folders will be created if the path does not exist.
	 * @param filmID film id
	 * @param create create the folder or not
	 * @return
	 */
	String getFilmImagePath(final long filmID, boolean create);

	/**
	 * Compute the path to store all screenshots of a film, folders will be created if the path does not
	 *  exist.
	 * @param filmID film id
	 * @param create create the folder or not
	 * @return
	 */
	String getFilmScreenshotImagesPath(final long filmID, boolean create); 

	/**
	 *return a default image if the specified cover image file is missing. 
	 * @return a default cover image
	 */
	byte[] getDefaultCoverImage();

	/**
	 * return a default image if the specified screenshot file is missing. 
	 * @return a default screenshot image
	 */
	byte[] getDefaultScreenshotImage();

	/**
	 * Get how many files under specified path.
	 * @param folder full path of a existed folder
	 * @return file number as an integer
	 */
	int getFileNumber(final String folder);

	/**
	 * Save cover image of a film.
	 * @param filmID film id
	 * @param image cover image content as a byte array
	 */
	void saveFilmCoverImage(long filmID, byte[] image) ;

	/**
	 * Save screenshot images of a film.
	 * @param filmID film id
	 * @param images screenshot images.
	 */
	void saveFilmScreenshotImages(long filmID, byte[][] images) ;

	/**
	 * Get cover image of a film.
	 * @param filmID film id
	 * @return cover image as byte array
	 */
	byte[] getFilmCoverImage(final long filmID);

	/**
	 * Get cropped cover image of a film by x:y ratio.
	 * @param filmID film id
	 * @param x horizontal ratio
 	 * @param y vertical ratio
	 * @return cover image as byte array
	 */
	byte[] getFilmCoverImage(final long filmID,final int x, final int y);
	
	/**
	 * Get a screenshot image of a film.
	 * @param filmID film id
	 * @param filename screenshot file name
	 * @return a screenshot image as byte array
	 */
	byte[] getFilmScreenshotImage(final long filmID, final String filename);

	/**
	 * Get screenshot image file names of a film.
	 * @param filmID film id
	 * @return screenshot image file names
	 */
	String[] getFilmScreenshotFilenames(final long filmID);
}
