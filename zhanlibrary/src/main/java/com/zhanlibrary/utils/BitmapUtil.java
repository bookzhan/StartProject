package com.zhanlibrary.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build.VERSION;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * 图片处理的工具类，主要针对bitmap进行处理
 * 
 */
public class BitmapUtil {

	private static final String TAG = "Blur";

	/**
	 * 模糊
	 * 
	 * @param radius
	 *            模糊半径
	 * @author Medivh
	 * @date 2014-7-17 上午9:39:51
	 */
	public static Bitmap fastblur(Context context,Bitmap sentBitmap, int radius) {

		if (VERSION.SDK_INT > 16) {
			Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

			final RenderScript rs = RenderScript.create(context);
			final Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
			final Allocation output = Allocation.createTyped(rs, input.getType());
			final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

			script.setRadius(radius /* e.g. 3.f */);
			script.setInput(input);
			script.forEach(output);
			output.copyTo(bitmap);
			return bitmap;
		}

		Bitmap bitmap = sentBitmap.copy(Config.ARGB_8888, true);

		if (radius < 1) {
			return (null);
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		bitmap.setPixels(pix, 0, w, 0, 0, w, h);
		return (bitmap);
	}

	/**
	 * 模糊
	 * 
	 * @param blurValue
	 *            模糊半径
	 * @author Medivh
	 * @date 2014-7-17 上午9:39:51
	 */
	public static Bitmap bluredBitmap(Context context,Bitmap srcBitmap, int blurValue) {

		return BitmapUtil.fastblur(context,srcBitmap, blurValue);
	}

	/**
	 * 模糊
	 * 
	 * @param blurValue
	 *            模糊半径
	 * @param contrastValue
	 *            对比度
	 * @author Medivh
	 * @date 2014-7-17 上午9:39:51
	 */
	public static Bitmap bluredBitmap(Context context,Bitmap srcBitmap, int blurValue, int contrastValue) {
		Bitmap dstBitmap = setContrast(srcBitmap, contrastValue);
		dstBitmap = BitmapUtil.fastblur(context,dstBitmap, blurValue);
		return dstBitmap;
	}

	/**
	 * 对比度
	 * 
	 * @author Medivh
	 * @date 2014-7-17 上午9:40:33
	 */
	public static Bitmap setContrast(Bitmap bitmap, int contrastValue) {

		Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		// int brightness = progress - 127;
		float contrast = (float) ((contrastValue + 64) / 128.0);
		ColorMatrix cMatrix = new ColorMatrix();
		cMatrix.set(new float[] { contrast, 0, 0, 0, 0, 0, contrast, 0, 0, 0,// �ı�Աȶ�
				0, 0, contrast, 0, 0, 0, 0, 0, 1, 0 });

		Paint paint = new Paint();
		paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

		Canvas canvas = new Canvas(bmp);
		canvas.drawBitmap(bitmap, 0, 0, paint);

		return bmp;
	}

	/**
	 * 设置图片亮度
	 * 
	 * @author Medivh
	 * @date 2014-7-17 上午9:53:06
	 */
	public static void setBrightness(Bitmap srcBitmap, int brightness) {
		ColorMatrix cMatrix = new ColorMatrix();
		cMatrix.set(new float[] { 1, 0, 0, 0, brightness, 0, 1, 0, 0, brightness, 0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 });
		Paint paint = new Paint();
		paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
		Canvas canvas = new Canvas(srcBitmap);
		canvas.drawBitmap(srcBitmap, 0, 0, paint);
	}

	/**
	 * 按比例缩放到高度为scaleValue
	 * 
	 * @param scaleValue
	 * @author Medivh
	 * @date 2014-7-17 上午9:41:53
	 */
	public static Bitmap scaleBitmap(Bitmap srcBitmap, float scaleValue) {

		Matrix matrix = new Matrix();
		float scaleY = scaleValue / (float) srcBitmap.getHeight();
		float scaleX = scaleY;
		matrix.setScale(scaleX, scaleY);
		return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
	}

	 public  static Bitmap reSizeBitmap(Bitmap bitmap, float scaleValue) {
		  Matrix matrix = new Matrix(); 
		  matrix.postScale(scaleValue, scaleValue); //长和宽放大缩小的比例
		  Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
		  return resizeBmp;
		 }
	public static Bitmap scaleBitmapFromWidth(Bitmap srcBitmap, float newWidth) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		srcBitmap.compress(CompressFormat.JPEG, 100, baos);
		// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
		if (baos.toByteArray().length / 1024 > 1024) {
			// 重置baos即清空baos
			baos.reset();
			// 这里压缩50%，把压缩后的数据存放到baos中
			srcBitmap.compress(CompressFormat.JPEG, 100, baos);
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());

		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;

		// 缩放比。由于是固定比例缩放，用宽进行计算
		int ration = 1;
		if (newOpts.outWidth > newWidth) {
			ration = Math.round(newOpts.outWidth / newWidth);
		} else {
			ration = Math.round(newWidth / newOpts.outWidth);
		}
		if (ration <= 0)
			ration = 1;

		// 设置缩放比例
		newOpts.inSampleSize = ration;

		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 100, baos);
		// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 90;
		// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
		while (baos.toByteArray().length / 1024 > 100) {
			// 重置baos即清空baos
			baos.reset();
			// 这里压缩options%，把压缩后的数据存放到baos中
			image.compress(CompressFormat.JPEG, options, baos);
			// 每次都减少10
			options -= 10;
		}
		// 把压缩后的数据baos存放到ByteArrayInputStream中
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		// 把ByteArrayInputStream数据生成图片
		return BitmapFactory.decodeStream(isBm, null, null);
	}

	/**
	 * @author YHC 通过压缩图片的尺寸来压缩图片大小
	 * 
	 * @param bitmap
	 *            要压缩图片
	 * @return 缩放后的图片
	 */
	public static Bitmap compressBySize(Context context,Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, baos);
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length, opts);
		// 得到图片的宽度、高度；  
		int imgWidth = opts.outWidth;

		// 分别计算图片宽度、高度与目标宽度、高度的比例；取大于该比例的最小整数；
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int widthRatio =display.getWidth() / imgWidth;
		GHSLogUtil.d("imgWidth:" + imgWidth + " Constants.screenWidth:" + display.getWidth() + "-------widthRatio:" + widthRatio);

		if (widthRatio > 1) {
			opts.inSampleSize = widthRatio;
		}
		// 设置好缩放比例后，加载图片进内存；  
		opts.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length, opts);
		return bitmap;
	}

	public static Bitmap compressBySize(Bitmap bitmap, int targetWidth, int targetHeight) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, baos);
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length, opts);
		// 得到图片的宽度、高度；  
		int imgWidth = opts.outWidth;
		int imgHeight = opts.outHeight;
		// 分别计算图片宽度、高度与目标宽度、高度的比例；取大于该比例的最小整数；  
		int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
		int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
		if (widthRatio > 1 && widthRatio > 1) {
			if (widthRatio > heightRatio) {
				opts.inSampleSize = widthRatio;
			} else {
				opts.inSampleSize = heightRatio;
			}
		}
		// 设置好缩放比例后，加载图片进内存；  
		opts.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length, opts);
		return bitmap;
	}

	/**
	 * 非比例缩放
	 * 
	 * @param xScaleTo
	 *            X轴缩放到该值
	 * @param yScaleTo
	 *            Y轴缩放到该值
	 * @author Medivh
	 * @date 2014-7-17 上午9:41:53
	 */
	public static Bitmap scaleBitmap(Bitmap srcBitmap, int xScaleTo, int yScaleTo) {

		Matrix matrix = new Matrix();
		float scaleY = yScaleTo / (float) srcBitmap.getHeight();
		float scaleX = xScaleTo / (float) srcBitmap.getWidth();
		matrix.setScale(scaleX, scaleY);
		return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
	}

	/**
	 * 非比例缩放
	 * 
	 * @param scaleX
	 *            X轴的缩放比例
	 * @param scaleY
	 *            Y轴的缩放比例
	 * @author Medivh
	 * @date 2014-7-17 上午9:41:53
	 */
	public static Bitmap scaleBitmap(Bitmap srcBitmap, float scaleX, float scaleY) {
		Matrix matrix = new Matrix();
		matrix.setScale(scaleX, scaleY);
		return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
	}

	/**
	 * 旋转图片
	 * 
	 * @param rotate
	 *            旋转角度
	 * @author Medivh
	 * @date 2014-7-17 上午9:52:35
	 */
	public static Bitmap rotateBitmap(Bitmap srcBitmap, int rotate) {
		Matrix matrix = new Matrix();
		matrix.setRotate(rotate);
		srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
		return srcBitmap;
	}

	/**
	 * 从原始图片正中央裁剪一张新图
	 * 
	 * @author Medivh
	 * @date 2014-7-17 上午9:53:06
	 */
	public static Bitmap centerCutBitmap(Bitmap bitmap, int newWidth, int newHeight) {

		try {

			Matrix matrix = new Matrix();
			float scaleWidth = (float) newWidth / bitmap.getWidth();
			float scaleHeight = (float) newHeight / bitmap.getHeight();

			if ((float) bitmap.getWidth() / (float) bitmap.getHeight() > (float) newWidth / (float) newHeight) {
				matrix.setScale(scaleHeight, scaleHeight);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				int offset = (bitmap.getWidth() - newWidth) / 2;
				bitmap = Bitmap.createBitmap(bitmap, offset, 0, newWidth, newHeight);
			} else {
				matrix.setScale(scaleWidth, scaleWidth);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				int offset = (bitmap.getHeight() - newHeight) / 2;
				bitmap = Bitmap.createBitmap(bitmap, 0, offset, newWidth, newHeight);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return bitmap;
	}




	/**
	 * 设置图片的透明度
	 * 
	 * @author Medivh
	 * @date 2014-7-17 上午9:58:05
	 */
	public static Bitmap makeReflectionBitmap(Bitmap srcBitmap) {
		int bmpWidth = srcBitmap.getWidth();
		int bmpHeight = srcBitmap.getHeight();
		int[] pixels = new int[bmpWidth * bmpHeight * 4];
		srcBitmap.getPixels(pixels, 0, bmpWidth, 0, 0, bmpWidth, bmpHeight);

		// get4NoToast reflection bitmap based on the reversed one
		srcBitmap.getPixels(pixels, 0, bmpWidth, 0, 0, bmpWidth, bmpHeight);
		Bitmap reflectionBitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Config.ARGB_8888);
		int alpha = 0x00000000;

		int i = 0x00000000;
		for (int y = bmpHeight - 1; y >= 0; y--) {
			for (int x = bmpWidth - 1; x >= 0; x--) {
				int index = y * bmpWidth + x;
				int r = (pixels[index] >> 16) & 0xff;
				int g = (pixels[index] >> 8) & 0xff;
				int b = pixels[index] & 0xff;

				pixels[index] = alpha | (r << 16) | (g << 8) | b;

				reflectionBitmap.setPixel(x, y, pixels[index]);
			}

			if (i < 5) {
				alpha = alpha + 0x03000000;
			} else if (i < 10) {
				alpha = alpha + 0x06000000;
			} else if (i < 20) {
				alpha = alpha + 0x09000000;
			} else if (i < 30) {
				alpha = alpha + 0x0c000000;
			}

			i++;
		}

		return reflectionBitmap;
	}

	/**
	 * 合并两张bitmap为一张
	 * 
	 * @param background
	 * @param foreground
	 * @return Bitmap
	 */
	public static Bitmap combineBitmap(Bitmap background, Bitmap foreground) {
		if (background == null) {
			return null;
		}
		int bgWidth = background.getWidth();
		int bgHeight = background.getHeight();
		int fgWidth = foreground.getWidth();
		int fgHeight = foreground.getHeight();

		final float rate = (float) fgWidth / fgHeight;
		int h = (int) (Math.sqrt(bgWidth * bgWidth / (rate * rate + 1)));
		int w = (int) (h * rate);

		foreground = scaleBitmap(foreground, w, h);
		fgWidth = foreground.getWidth();
		fgHeight = foreground.getHeight();

		Bitmap newmap = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(newmap);
		canvas.drawBitmap(background, 0, 0, null);
		canvas.drawBitmap(foreground, (bgWidth - fgWidth) / 2, (bgHeight - fgHeight) / 2, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return newmap;
	}

	public static Bitmap readBitmap(Context context, int resId) {

		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}
	public static Bitmap readBitmap(Context context, int resId,Config config) {
		
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = config;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}
	
	public static Bitmap readBitmap(Context context, String  path) {
		
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		try{
		InputStream is = new FileInputStream(new File(path));
		return BitmapFactory.decodeStream(is, null, opt);}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Bitmap convertViewToBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();

		return bitmap;
	}

	/**
	 * 添加阴影
	 * 
	 *
	 */
	public static Bitmap addShadow(Bitmap bitmap) {
		try {
			if (bitmap == null)
				return null;

			BlurMaskFilter blurFilter = new BlurMaskFilter(4, BlurMaskFilter.Blur.OUTER);
			Paint shadowPaint = new Paint();
			shadowPaint.setMaskFilter(blurFilter);

			int[] offsetXY = new int[2];
			Bitmap shadowImage = bitmap.extractAlpha(shadowPaint, offsetXY);
			shadowImage = shadowImage.copy(Config.ARGB_8888, true);
			Canvas c = new Canvas(shadowImage);
			c.drawBitmap(bitmap, -offsetXY[0], -offsetXY[1], null);
			return shadowImage;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap; // if error return the original bitmap
	}
	
	public static Bitmap getShaderBitmap(Bitmap src) {
		if (src == null) {
			return null;
		}

		int bgWidth = src.getWidth();
		int bgHeight = src.getHeight();

		final int shaderOffsetX = 7;
		final int shaderOffsetY = 7;

		Bitmap dst = Bitmap.createBitmap(bgWidth + shaderOffsetX, bgHeight + shaderOffsetY, Config.ARGB_4444);
		Canvas canvas = new Canvas(dst);

		Paint mPaint = new Paint();
		BlurMaskFilter bf = new BlurMaskFilter(7, BlurMaskFilter.Blur.INNER);
		mPaint.setColor(Color.GRAY);
		LinearGradient linearGradient = new LinearGradient(0, 0, src.getWidth(), src.getHeight(), new int[] { Color.parseColor("#333333"), Color.parseColor("#333333") }, null, Shader.TileMode.MIRROR);

		mPaint.setShader(linearGradient);
		mPaint.setMaskFilter(bf);

		canvas.drawBitmap(src.extractAlpha(mPaint, null), shaderOffsetX, shaderOffsetY, mPaint);
		canvas.drawBitmap(src, 0, 0, null);

		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return dst;
	}

	/**
	 * 处理图片
	 *
	 * @param bm
	 *            所要转换的bitmap
	 * @param newWidth 新的宽
	 * @param newHeight 新的高
	 * @return 指定宽高的bitmap
	 */
	public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片

		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
		return newbm;
	}


	/**
	 * 保存图片到指定的目录中
	 *
	 * @param bitmap
	 */
	public static void saveBitmapToSDcard(Bitmap bitmap, String path) {

		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(path);
			bitmap.compress(CompressFormat.JPEG, 100, outputStream);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据资源id来读取一张图片
	 * @param resources
	 * @param resId
	 * @param width 希望的宽
	 * @param height 希望的高
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources resources, int resId, int width, int height) {
		// 给定的BitmapFactory设置解码的参数
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 从解码器中获得原始图片的宽高，而避免申请内存空间
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(resources, resId, options);
		options.inSampleSize = calculateInSampleSize(options, width, height);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(resources, resId, options);
	}
	/**
	 * 计算缩放比例
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image load both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

}
