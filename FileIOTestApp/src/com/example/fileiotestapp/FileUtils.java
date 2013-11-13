package com.example.fileiotestapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

/**
 * Fileの読み書き用汎用クラス
 * 
 * 
 * Memo:
 * Contextの
 * 		openFileInput(filename, mode),
 * 		openFileOutput(filename)
 * メソッドを使うと  data/data/[パッケージ名]/files/filename  に保存される
 * 
 * File file = new File(filepath)
 * でfileにアクセスしたいのならば
 * 		filepath = this.getFilesDir().getPath() + "/" + filename
 * のようにする
 */

public class FileUtils {
	// ==============File Utils================

	/**
	 * ファイルへバイナリデータを書き込み
	 * 
	 * @param context
	 * @param data
	 *            バイトデータ
	 * @param fileName
	 *            ファイル名
	 */
	public static void writeBinaryFile(Context context, byte[] data,
			String fileName) {
		OutputStream out = null;
		try {
			out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			out.write(data, 0, data.length);
		} catch (Exception e) {
			// 必要に応じて
			// throw e;
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * ファイルからバイナリデータを読み込む
	 * 
	 * @param context
	 * @param fileName
	 *            ファイル名
	 * @return バイトデータ ファイルがない場合はnull
	 */
	public static byte[] readBinaryFile(Context context, String fileName) {
		// ファイルの存在チェック
		if (!(new File(context.getFilesDir().getPath() + "/" + fileName)
				.exists())) {
			return null;
		}

		int size;
		byte[] data = new byte[1024];
		InputStream in = null;
		ByteArrayOutputStream out = null;

		try {
			in = context.openFileInput(fileName);
			out = new ByteArrayOutputStream();
			while ((size = in.read(data)) != -1) {
				out.write(data, 0, size);
			}
			return out.toByteArray();
		} catch (Exception e) {
			// エラーの場合もnullを返すのでここでは何もしない
		} finally {
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (Exception e) {
			}
		}

		return null;
	}

	/**
	 * ファイルへ文字列を書き込み
	 * 
	 * @param context
	 * @param str
	 *            ファイル出力文字列
	 * @param fileName
	 *            ファイル名
	 */
	public static void writeStringFile(Context context, String str,
			String fileName) {
		writeBinaryFile(context, str.getBytes(), fileName);
	}

	/**
	 * ファイルから文字列を読み込む
	 * 
	 * @param context
	 * @param fileName
	 *            ファイル名
	 * @return 文字列 ファイルがない場合はnull
	 */
	public static String readStringFile(Context context, String fileName) {
		String str = null;
		byte[] data = readBinaryFile(context, fileName);
		if (data != null) {
			str = new String(data);
		}
		return str;
	}

	/**
	 * ファイルからビットマップ画像を読み込む
	 * 
	 * @param fileName
	 *            ファイル名
	 * @return バイトデータ ファイルがない場合はnull
	 */
	public static Bitmap readBitmapFile(Context context, String fileName) {
		InputStream in = null;
		try {
			in = context.openFileInput(fileName);
			Bitmap bm = BitmapFactory.decodeStream(in);
			return bm;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * ファイルへビットマップ画像を書き込み
	 * 
	 * @param context
	 * @param fileName
	 *            ファイル名
	 */
	public static void writeBitmapFile(Context context, Bitmap bitmap,
			String fileName) {
		// Bitmapをファイルとして保存
		OutputStream out = null;
		try {
			out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			bitmap.compress(CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception e) {
			}
		}
	}

}
