package com.alipay.automation.utils;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class ImageUtils {
	/**
	 * Compare two image by pixel
	 * 
	 * @param sourceFile
	 *            image 1
	 * @param targetFile
	 *            image 2
	 * @param percent
	 *            similarity 0 ~ 1.0
	 */
	public static boolean sameAs(File sourceFile, File targetFile, float percent) {
		if (sourceFile == null || targetFile == null || percent > 1
				|| percent < 0) {
			return false;
		}

		BufferedImage sourceImage;
		BufferedImage targetImage;
		try {
			sourceImage = ImageIO.read(sourceFile);
			targetImage = ImageIO.read(targetFile);

			if (sourceImage.getWidth() != targetImage.getWidth()) {
				return false;
			}
			if (sourceImage.getHeight() != targetImage.getHeight()) {
				return false;
			}

			int width = targetImage.getWidth();
			int height = targetImage.getHeight();

			int numDiffPixels = 0;

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (targetImage.getRGB(x, y) != sourceImage.getRGB(x, y)) {
						numDiffPixels++;
					}
				}
			}
			double numberPixels = (height * width);
			double diffPercent = numDiffPixels / numberPixels;

			return percent <= 1.0 - diffPercent;
		} catch (Exception e) {
			return false;
		}
	}
}
