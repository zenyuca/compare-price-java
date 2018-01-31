package com.wzd.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * 图片处理类<br>
 * 
 * @author redstorm
 * @version 2014-8-22
 * 
 */
@SuppressWarnings({"rawtypes","unused"})
public class ImageUtils {

	private static Logger logger = LogManager.getLogger(ImageUtils.class);

	/**
	 * 将图片缩放处理方法<br>
	 * 
	 * @param source
	 *            源图片对象
	 * @param targetW
	 *            转换后的宽度
	 * @param targetH
	 *            转换后的高度
	 * @param sameScale
	 *            是否等比例缩放
	 * @return BufferedImage
	 */
	public static BufferedImage resize(BufferedImage source, int targetW, int targetH, boolean sameScale) { // targetW，targetH分别表示目标长和宽
		int type = source.getType();
		BufferedImage target = null;
		double sx = (double) targetW / source.getWidth();
		double sy = (double) targetH / source.getHeight();
		if (sameScale) { // 需要等比例缩放
			if (sx > sy) {
				sx = sy;
				targetW = (int) (sx * source.getWidth());
			} else {
				sy = sx;
				targetH = (int) (sy * source.getHeight());
			}
		}
		if (type == BufferedImage.TYPE_CUSTOM) { // handmade
			ColorModel cm = source.getColorModel();
			WritableRaster raster = cm.createCompatibleWritableRaster(targetW, targetH);
			boolean alphaPremultiplied = cm.isAlphaPremultiplied();
			target = new BufferedImage(cm, raster, alphaPremultiplied, null);
		} else {
			target = new BufferedImage(targetW, targetH, type);
		}
		Graphics2D g = target.createGraphics();
		// smoother than exlax:
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
		g.dispose();
		return target;
	}

	/**
	 * 将图片放大与缩小<br>
	 * 
	 * @param sourceByte
	 *            源图片的字节数组
	 * @param width
	 *            转换后的宽度
	 * @param height
	 *            转换后的高度
	 * @param sameScale
	 *            是否等比例缩放
	 * @return byte[]
	 */
	public static byte[] convertImageSize(byte[] sourceByte, int width, int height, boolean sameScale)
			throws Exception {
		byte[] returnValue = null;
		if (sourceByte != null && sourceByte.length > 0) {
			ByteArrayInputStream in = new ByteArrayInputStream(sourceByte);
			// BufferedImage srcImage = getReadImage(in);
			BufferedImage srcImage = null;
			try {
				srcImage = ImageIO.read(in); // RGB
			} catch (Exception e) {
				String fileName = UUID.randomUUID().toString(); // 写入临时文件
				File file = new File(fileName);
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(sourceByte);
				fos.close();
				srcImage = getReadImage(file);
				file.delete();
			}
			if (srcImage != null) {
				BufferedImage srcImageTarget = resize(srcImage, width, height, sameScale);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				boolean flag = ImageIO.write(srcImageTarget, "JPEG", out);
				returnValue = out.toByteArray();
			}
		}
		return returnValue;
	}

	/**
	 * 将图片放大与缩小后另存<br>
	 * 
	 * @param fromFileStr
	 *            来源文件名
	 * @param saveToFileStr
	 *            目标文件名
	 * @param width
	 *            转换后的宽度
	 * @param height
	 *            转换后的高度
	 * @param sameScale
	 *            是否等比例缩放
	 */
	public static void saveImageAsJpg(String fromFileStr, String saveToFileStr, int width, int height,
			boolean sameScale) throws Exception {
		String imgType = "JPEG";
		if (fromFileStr.toLowerCase().endsWith(".png")) {
			imgType = "PNG";
		}
		File saveFile = new File(saveToFileStr);
		File fromFile = new File(fromFileStr);
		BufferedImage srcImage = getReadImage(fromFile);
		if (srcImage != null) {
			if (width > 0 || height > 0) {
				srcImage = resize(srcImage, width, height, sameScale);
			}
			ImageIO.write(srcImage, imgType, saveFile);
		}
	}

	/**
	 * 根据文件取得bufferedImage对象（自动识别RGB与CMYK）<br>
	 * 
	 * @param file
	 *            来源文件名
	 * @return BufferedImage
	 */
	private static BufferedImage getReadImage(File file) throws Exception {
		BufferedImage srcImage = null;
		ImageInputStream input = ImageIO.createImageInputStream(file); // 只能接收File或FileInputStream，ByteArrayInputStream无效
		Iterator readers = ImageIO.getImageReaders(input);
		if (readers == null || !readers.hasNext()) {
			throw new RuntimeException("1 No ImageReaders found");
		}
		ImageReader reader = (ImageReader) readers.next();
		reader.setInput(input);
		String format = reader.getFormatName();
		if ("JPEG".equalsIgnoreCase(format) || "JPG".equalsIgnoreCase(format)) {
			try {
				srcImage = ImageIO.read(file); // RGB
			} catch (Exception e) {
				Raster raster = reader.readRaster(0, null);// CMYK
				srcImage = createJPEG4(raster);
			}
			input.close();
		}
		return srcImage;
	}

	/**
	 * RGB彩色空间转换为CMYK<br>
	 * 
	 * @param raster
	 * @return BufferedImage
	 */
	private static BufferedImage createJPEG4(Raster raster) {
		int w = raster.getWidth();
		int h = raster.getHeight();
		byte[] rgb = new byte[w * h * 3]; // 彩色空间转换
		float[] Y = raster.getSamples(0, 0, w, h, 0, (float[]) null);
		float[] Cb = raster.getSamples(0, 0, w, h, 1, (float[]) null);
		float[] Cr = raster.getSamples(0, 0, w, h, 2, (float[]) null);
		float[] K = raster.getSamples(0, 0, w, h, 3, (float[]) null);
		for (int i = 0, imax = Y.length, base = 0; i < imax; i++, base += 3) {
			float k = 220 - K[i], y = 255 - Y[i], cb = 255 - Cb[i], cr = 255 - Cr[i];
			double val = y + 1.402 * (cr - 128) - k;
			val = (val - 128) * .65f + 128;
			rgb[base] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);
			val = y - 0.34414 * (cb - 128) - 0.71414 * (cr - 128) - k;
			val = (val - 128) * .65f + 128;
			rgb[base + 1] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);
			val = y + 1.772 * (cb - 128) - k;
			val = (val - 128) * .65f + 128;
			rgb[base + 2] = val < 0.0 ? (byte) 0 : val > 255.0 ? (byte) 0xff : (byte) (val + 0.5);
		}
		raster = Raster.createInterleavedRaster(new DataBufferByte(rgb, rgb.length), w, h, w * 3, 3,
				new int[] { 0, 1, 2 }, null);
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel cm = new ComponentColorModel(cs, false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		return new BufferedImage(cm, (WritableRaster) raster, true, null);
	}

	/**
	 * 在图片中添加水印文本<br>
	 * 
	 * @param fromFileStr
	 *            来源文件名
	 * @param saveToFileStr
	 *            目标文件名
	 * @param markText1
	 *            文字说明1
	 * @param markText2
	 *            文字说明2（可为空，不为空时另起一行在下方）
	 * @param fontName
	 *            字体名（默认为宋体）
	 * @param fontStyle
	 *            字体样式（0=正常;1＝加粗;2=斜体;3=加粗斜体）
	 * @param fontColor
	 *            字体颜色
	 * @param fontSize
	 *            字体大小（默认为12）
	 * @param positionLeft
	 *            添加的左右位置（1=左对齐;2=居中;3=右对齐）默认为1
	 * @param positionTop
	 *            添加的上下位置（1=顶部;2=底部）默认为2
	 */
	public static byte[] setImageWatermark(byte[] sourceByte, String markText1, String markText2, String fontName,
			int fontStyle, int fontColor, int fontSize, String positionLeft, String positionTop) throws Exception {
		byte[] returnValue = null;
		if (sourceByte != null && sourceByte.length > 0) {
			ByteArrayInputStream in = new ByteArrayInputStream(sourceByte);
			BufferedImage srcImage = null;
			try {
				srcImage = ImageIO.read(in); // RGB
			} catch (Exception e) {
				String fileName = UUID.randomUUID().toString(); // 写入临时文件
				File file = new File(fileName);
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(sourceByte);
				fos.close();
				srcImage = getReadImage(file);
				file.delete();
			}
			if (srcImage != null) {
				String markText = StringUtils.defaultString(markText1) + StringUtils.defaultString(markText2);
				if (srcImage != null && !StringUtils.isBlank(markText)) {
					Graphics2D g = srcImage.createGraphics();
					int imgWidth = srcImage.getWidth(null);
					int imgHeight = srcImage.getHeight(null);
					// g.drawImage(srcImage, 0, 0, width, height, null);
					if (StringUtils.isBlank(fontName)) {
						fontName = "宋体";
					}
					if (fontSize == 0) {
						fontSize = 12;
					}
					long markTextLength1 = StringUtils.length(markText1);
					long markTextLength2 = StringUtils.length(markText2);
					int textTop1 = 0;
					int textTop2 = 0;
					int textLeft1 = 0;
					int textLeft2 = 0;
					int rectangleLeft = 0;
					int rectangleTop = 0;
					int rectangleWidth = 0;
					int rectangleHeight = 0;
					int textCount = 0; // 文本个数
					if (markTextLength1 == 0 || markTextLength2 == 0) {
						textCount = 1;
					} else {
						textCount = 2;
					}
					long markTextLength = 0;
					if (markTextLength1 > markTextLength2) {
						markTextLength = markTextLength1;
					} else {
						markTextLength = markTextLength2;
					}
					rectangleHeight = (int) Math.round(fontSize * 1.5 * textCount);
					if (textCount == 1) {
						rectangleHeight += 3;
					}
					if (fontStyle == 1 || fontStyle == 3) { // 粗体
						rectangleWidth = (int) (markTextLength * 1.1 * fontSize / 2) + 20;
					} else {
						rectangleWidth = (int) (markTextLength * fontSize / 2) + 20;
					}
					if ("1".equals(positionTop)) {
						rectangleTop = 10;
					} else {
						rectangleTop = imgHeight - rectangleHeight - 10;
					}
					if ("2".equals(positionLeft)) {
						rectangleLeft = (int) (imgWidth - rectangleWidth) / 2;
					} else if ("3".equals(positionLeft)) {
						rectangleLeft = (int) (imgWidth - rectangleWidth) - 12;
					}
					if (rectangleLeft <= 12) {
						rectangleLeft = 12;
					}
					textTop1 = rectangleTop + fontSize + 4;
					textLeft1 = rectangleLeft + 10;
					textTop2 = textTop1 + (int) Math.round(fontSize * 1.5) - 2;
					textLeft2 = textLeft1;

					g.setColor(new Color(204, 204, 204));
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f));
					g.fillRoundRect(rectangleLeft - 1, rectangleTop - 1, rectangleWidth + 3, rectangleHeight + 3, 15,
							15); // 填充圆角边矩形框（外边立体效果）
					g.setColor(new Color(240, 255, 255));
					RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(rectangleLeft, rectangleTop,
							rectangleWidth, rectangleHeight, 10, 10);
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.6f));
					g.draw(roundedRectangle); // 添加圆角边矩形框
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f));
					g.fillRoundRect(rectangleLeft, rectangleTop, rectangleWidth, rectangleHeight, 10, 10); // 填充圆角边矩形框

					// g.drawRect(rectangleLeft, rectangleTop, rectangleWidth,
					// rectangleHeight); // 添加直角矩形框
					// g.fillRect(rectangleLeft, rectangleTop, rectangleWidth,
					// rectangleHeight);
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1f));
					g.setColor(new Color(fontColor));
					g.setFont(new Font(fontName, fontStyle, fontSize));
					if (StringUtils.isBlank(markText1)) {
						g.drawString(markText2, textLeft1, textTop1); // 添加文本
					} else {
						g.drawString(markText1, textLeft1, textTop1); // 添加文本
						if (!StringUtils.isBlank(markText2)) {
							g.drawString(markText2, textLeft2, textTop2); // 添加文本
						}
					}
					g.dispose();
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					boolean flag = ImageIO.write(srcImage, "JPEG", out);
					returnValue = out.toByteArray();
				}
			}
		}
		return returnValue;
	}

	/**
	 * 在图片中添加水印文本<br>
	 * 
	 * @param fromFileStr
	 *            来源文件名
	 * @param saveToFileStr
	 *            目标文件名
	 * @param markText1
	 *            文字说明1
	 * @param markText2
	 *            文字说明2（可为空，不为空时另起一行在下方）
	 * @param fontName
	 *            字体名（默认为宋体）
	 * @param fontStyle
	 *            字体样式（0=正常;1＝加粗;2=斜体;3=加粗斜体）
	 * @param fontColor
	 *            字体颜色
	 * @param fontSize
	 *            字体大小（默认为12）
	 * @param positionLeft
	 *            添加的左右位置（1=左对齐;2=居中;3=右对齐）默认为1
	 * @param positionTop
	 *            添加的上下位置（1=顶部;2=底部）默认为2
	 */
	public static void setImageWatermark(String fromFileStr, String saveToFileStr, String markText1, String markText2,
			String fontName, int fontStyle, int fontColor, int fontSize, String positionLeft, String positionTop)
					throws Exception {
		String imgType = "JPEG";
		if (fromFileStr.toLowerCase().endsWith(".png")) {
			imgType = "PNG";
		}
		File saveFile = new File(saveToFileStr);
		File fromFile = new File(fromFileStr);
		BufferedImage srcImage = getReadImage(fromFile);
		String markText = StringUtils.defaultString(markText1) + StringUtils.defaultString(markText2);
		if (srcImage != null && !StringUtils.isBlank(markText)) {
			Graphics2D g = srcImage.createGraphics();
			int imgWidth = srcImage.getWidth(null);
			int imgHeight = srcImage.getHeight(null);
			// g.drawImage(srcImage, 0, 0, width, height, null);
			if (StringUtils.isBlank(fontName)) {
				fontName = "宋体";
			}
			if (fontSize == 0) {
				fontSize = 12;
			}
			long markTextLength1 = StringUtils.length(markText1);
			long markTextLength2 = StringUtils.length(markText2);
			int textTop1 = 0;
			int textTop2 = 0;
			int textLeft1 = 0;
			int textLeft2 = 0;
			int rectangleLeft = 0;
			int rectangleTop = 0;
			int rectangleWidth = 0;
			int rectangleHeight = 0;
			int textCount = 0; // 文本个数
			if (markTextLength1 == 0 || markTextLength2 == 0) {
				textCount = 1;
			} else {
				textCount = 2;
			}
			long markTextLength = 0;
			if (markTextLength1 > markTextLength2) {
				markTextLength = markTextLength1;
			} else {
				markTextLength = markTextLength2;
			}
			rectangleHeight = (int) Math.round(fontSize * 1.5 * textCount);
			if (textCount == 1) {
				rectangleHeight += 3;
			}
			if (fontStyle == 1 || fontStyle == 3) { // 粗体
				rectangleWidth = (int) (markTextLength * 1.1 * fontSize / 2) + 20;
			} else {
				rectangleWidth = (int) (markTextLength * fontSize / 2) + 20;
			}
			if ("1".equals(positionTop)) {
				rectangleTop = 10;
			} else {
				rectangleTop = imgHeight - rectangleHeight - 10;
			}
			if ("2".equals(positionLeft)) {
				rectangleLeft = (int) (imgWidth - rectangleWidth) / 2;
			} else if ("3".equals(positionLeft)) {
				rectangleLeft = (int) (imgWidth - rectangleWidth) - 12;
			}
			if (rectangleLeft <= 12) {
				rectangleLeft = 12;
			}
			textTop1 = rectangleTop + fontSize + 4;
			textLeft1 = rectangleLeft + 10;
			textTop2 = textTop1 + (int) Math.round(fontSize * 1.5) - 2;
			textLeft2 = textLeft1;

			g.setColor(new Color(204, 204, 204));
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f));
			g.fillRoundRect(rectangleLeft - 1, rectangleTop - 1, rectangleWidth + 3, rectangleHeight + 3, 15, 15); // 填充圆角边矩形框（外边立体效果）
			g.setColor(new Color(240, 255, 255));
			RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(rectangleLeft, rectangleTop, rectangleWidth,
					rectangleHeight, 10, 10);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.6f));
			g.draw(roundedRectangle); // 添加圆角边矩形框
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f));
			g.fillRoundRect(rectangleLeft, rectangleTop, rectangleWidth, rectangleHeight, 10, 10); // 填充圆角边矩形框

			// g.drawRect(rectangleLeft, rectangleTop, rectangleWidth,
			// rectangleHeight); // 添加直角矩形框
			// g.fillRect(rectangleLeft, rectangleTop, rectangleWidth,
			// rectangleHeight);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1f));
			g.setColor(new Color(fontColor));
			g.setFont(new Font(fontName, fontStyle, fontSize));
			if (StringUtils.isBlank(markText1)) {
				g.drawString(markText2, textLeft1, textTop1); // 添加文本
			} else {
				g.drawString(markText1, textLeft1, textTop1); // 添加文本
				if (!StringUtils.isBlank(markText2)) {
					g.drawString(markText2, textLeft2, textTop2); // 添加文本
				}
			}
			g.dispose();
			ImageIO.write(srcImage, imgType, saveFile);
		}
	}

	public static void main(String argv[]) {
		try {
			// ImageUtils.saveImageAsJpg("C:/test1.jpg", "c:/test2.jpg", 179,
			// 220, true);
			String imgTitle1 = "照片情况说明";
			String imgTitle2 = "上传日期：" + DateUtil.dateToString(new Date(), DateUtil.P_TIMESTAMP);
			// imgTitle1 = "";
			// imgTitle2 = "";
			ImageUtils.setImageWatermark("C:/imgTest1.jpg", "c:/imgTest1_2.jpg", imgTitle1, imgTitle2, "宋体", 0,
					new Color(26, 26, 26).getRGB(), 16, "3", "2");
			ImageUtils.setImageWatermark("C:/imgTest2.jpg", "c:/imgTest2_2.jpg", imgTitle1, imgTitle2, "宋体", 0,
					new Color(26, 26, 26).getRGB(), 16, "3", "2");
			ImageUtils.setImageWatermark("C:/imgTest3.jpg", "c:/imgTest3_2.jpg", imgTitle1, imgTitle2, "宋体", 0,
					new Color(26, 26, 26).getRGB(), 16, "3", "2");

			File fromFile = new File("C:/imgTest1.jpg");
			FileInputStream fileIn = new FileInputStream(fromFile);
			byte sourceByte[] = new byte[(int) fromFile.length()]; // 创建合适文件大小的数组
			fileIn.read(sourceByte); // 读取文件中的内容到b[]数组
			fileIn.close();
			byte[] targetByte = ImageUtils.setImageWatermark(sourceByte, imgTitle1, imgTitle2, "宋体", 0,
					new Color(26, 26, 26).getRGB(), 26, "3", "2");
			if (targetByte != null && targetByte.length > 0) {
				OutputStream out = new FileOutputStream("C:/imgTest1_3.jpg");
				OutputStream outBuffer = new BufferedOutputStream(out);
				outBuffer.write(targetByte);
				outBuffer.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("finish!");
	}
}
