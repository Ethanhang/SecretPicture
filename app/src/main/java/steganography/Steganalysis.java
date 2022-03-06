package steganography;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.bmptransformtest.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Utils.ByteConvertUtil;
import Utils.ByteUtils;
import Utils.ToastUtils;


/**
 *信息隐藏类
 * @author ethan
 */
public class Steganalysis {

	private static final String TAG = "Steganalysis";
	
	//需要隐写的文件路径
	private String sourceFilePath;
	//载体bmp的文件路径
	private String bmpFilePath;

	private EncodingCallBack encodingCallBack;

	private Context mContext;

	private String text;

	private SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	public Steganalysis(String sourceFilePath, String bmpFilePath,String text,EncodingCallBack encodingCallBack) {
		super();
		this.sourceFilePath = sourceFilePath;
		this.bmpFilePath = bmpFilePath;
		this.encodingCallBack = encodingCallBack;
		this.text = text;
		if(encodingCallBack instanceof MainActivity){
			this.mContext = (Context) encodingCallBack;
		}
	}

	/*获取文件信息 并将信息以二进制形式存储
	 * 1.使用bitmap
	 */
	public int[] getBit() throws IOException {
		 int[] bitContainer = null;
		 int[] bitType = new int[1];
		 int bitEncodeSize = 0;//隐藏数据的长度，单位为bit，对应的应该把这个4字节(32bit)的数值隐藏到原始数据的开头
		 Log.d(TAG,"text:" + text + ",sourceFilePath :" + sourceFilePath);
		 if(!TextUtils.isEmpty(text) && TextUtils.isEmpty(sourceFilePath)){
				//隐藏文字数据
			 byte[] textBytes = text.getBytes();
			 Log.d(TAG, "textBytes: " + ByteConvertUtil.byte2hex(textBytes));
			 //文字信息字节长度
			 int size = textBytes.length;
			 //将长度数值大端转换为字节
			 byte[] bitlenthBytes = ByteConvertUtil.getByteArrayBig(size);
			 Log.d(TAG, ByteConvertUtil.byte2hex(bitlenthBytes));
			 bitContainer = new int[size * 8 + bitlenthBytes.length *8];
			 //隐藏数据的总体长度包括帧头
			 bitEncodeSize = bitContainer.length;
			 //合并两个字节数组，数值数组在前
			 textBytes = ByteUtils.merger(bitlenthBytes,textBytes);
			 //合并后的字节长度
			 bitType[0] = 0;//0代表文字
			 //计数器
			 int count = 0;
			 for(int i=0; i<textBytes.length ; i++) {
				 //获取字节值
				 int curbyte = textBytes[i];
				 //从低到高依次获取bit
				 for(int j=0; j<8; j++) {
					 //获取当前bit
					 bitContainer[count] = (curbyte & 1);
					 //右移一位
					 curbyte >>= 1;
					 count++;
				 }
			 }
			 int[] mergeBitContainer  = ByteUtils.mergerIntArrary(bitType,bitContainer);
			 Log.d(TAG, ByteConvertUtil.byte2hexInt(mergeBitContainer) + ",size = " + mergeBitContainer.length);
//			 bitContainer = ByteUtils.mergerIntArrary(bitType,bitContainer);
			 return  mergeBitContainer;
		 }
		 else if (TextUtils.isEmpty(text) && !TextUtils.isEmpty(sourceFilePath)){
			 try {
				 FileInputStream fileInputStream = new FileInputStream(this.sourceFilePath);
				 //获取文件字节大小
				 int bytesize = fileInputStream.available();
				 //创建存储bit的容器
				 bitContainer = new int[bytesize * 8];
				 //将长度数值大端转换为字节
				 byte[] bitlenthBytes = ByteConvertUtil.getByteArrayBig(bytesize);
				 Log.d(TAG, ByteConvertUtil.byte2hex(bitlenthBytes));
				 int length = bitlenthBytes.length;
				 Log.d(TAG,"bytesize:" + bytesize);
				 int[] lengthContainer = new int[length*8];
				 int count0 = 0;

				 bitType[0] = 1;//1代表图片
				 for(int i=0; i<bitlenthBytes.length; i++) {
					 //获取字节值
					 int curbyte0 = bitlenthBytes[i];
					 //从低到高依次获取bit
					 for(int j=0; j<8; j++) {
						 //获取当前bit
						 lengthContainer[count0] = (curbyte0 & 1);
						 //右移一位
						 curbyte0 >>= 1;
						 count0++;
					 }
				 }
				 //计数器
				 //填充隐藏图片数据的值
				 int count = 0;
				 for(int i=0; i<bytesize; i++) {

					 //获取字节值
					 int curbyte = fileInputStream.read();
					 //从低到高依次获取bit
					 for(int j=0; j<8; j++) {
						 //获取当前bit
						 bitContainer[count] = (curbyte & 1);
						 //右移一位
						 curbyte >>= 1;
						 count++;
					 }
				 }
				 //将图片长度值的bit容器和图片数据bit容器合并
				 //合并两个字节数组，数值数组在前
				 int[] mergeBitContainer = ByteUtils.mergerIntArrary(bitType,ByteUtils.mergerIntArrary(lengthContainer,bitContainer));
				 Log.d(TAG,"mergeBitContainer:" + mergeBitContainer.length);
				 return mergeBitContainer;

			 } catch (FileNotFoundException e) {
				 Log.e(TAG,"读取隐写文件失败");
				 ToastUtils.showToast(mContext,"读取隐写文件失败");
			 }
		}
		 else {
		 	ToastUtils.showToast(mContext,"编码错误");
		 }
		 return bitContainer;
	}

	//将图片图片转换为可操作对象
	public Bitmap getBitmap() {
		Bitmap image = null;
		try {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inMutable = true;
			image = BitmapFactory.decodeFile(bmpFilePath,options);
//			options.inBitmap = image;
//			image = BitmapFactory.decodeStream(new FileInputStream(this.bmpFilePath));
		} catch (Exception e) {
			Log.e(TAG,"载体图片未找到");
			ToastUtils.showToast(mContext,"载体图片未找到");
			e.printStackTrace();
		}
		return image;
	}

	//bit值(0或1)放在old的最低位
	private int swapBit(int old,int bit) {
		old = (old & 0xFE);
		old |= bit;
		return old;
	}


	public void testToBmp(String newbmpFilePath){
		Bitmap image = this.getBitmap();
//		image = image.copy(Bitmap.Config.ARGB_8888, true);
		String newFilepath1 = newbmpFilePath + sDateFormat.format(new Date()) + ".bmp";
		this.saveToBmp(image, newFilepath1);
	}
	
	//将信息隐写在图片中，并保存为新图片
	public void datasourceToBMP(String newbmpFilePath) {
		if(TextUtils.isEmpty(newbmpFilePath)){
			Log.e(TAG,"datasourceToBMP --->null");
			return;
		}
		File directory = new File(newbmpFilePath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		Bitmap image = this.getBitmap();
//		image = image.copy(Bitmap.Config.ARGB_8888, true);

		//当前像素点坐标
		int curX,curY;
		//计数器
		int count = 0;
		try {
			int[] bitContainer = this.getBit();
			if(bitContainer == null || bitContainer.length <= 0){
				return;
			}
			//比较隐写信息和载体的大小，如果大于隐写失败
			if(bitContainer.length > image.getWidth()*image.getHeight()*3) {
					Log.d(TAG,"隐写信息过大");
				ToastUtils.showToast(mContext,"隐藏信息过大");
				return;
			}
			//先行后列
			for(curY=0;curY<image.getHeight();curY++) {
				
				for(curX=0;curX<image.getWidth();curX++) {
					//获取像素值
					int rgb = image.getPixel(curX, curY);
					//当前像素值分量
					int iRgb = 0;
					//分解像素值
					int R =(rgb & 0xff0000 ) >> 16 ;
					int G= (rgb & 0xff00 ) >> 8 ;
					int B= (rgb & 0xff );
					//判断信息是否隐写完毕
					if(count >= bitContainer.length) {
						//将隐写文件的当前时间作为文件名
						String newFilepath = newbmpFilePath + sDateFormat.format(new Date()) + ".bmp";
						this.saveToBmp(image, newFilepath);
						//将合成的图片显示到界面
						if(encodingCallBack != null){
							encodingCallBack.onCompleteEncoding(image,newFilepath);
						}
						return;
					}
					//将隐写文件位信息放入像素分量最低位
					while(true) {
						if(count >= bitContainer.length) {
							break;
						}
						switch (iRgb) {
						case 0:
							R = this.swapBit(R, bitContainer[count]);
							break;
							
						case 1:
							G = this.swapBit(G, bitContainer[count]);
							break;
							
						case 2:
							B = this.swapBit(B, bitContainer[count]);
							break;

						default:
							break;
						}
						count++;
						if(iRgb ==2 ) {
							break;
						}
						iRgb++;
					}
					//重组rgb像素值
					rgb = (R << 16) | (G << 8) | B;
					//重设rgb像素值
					image.setPixel(curX, curY, rgb);
				}
			}
			
			
		} catch (IOException e) {
			
		}
	}

	protected void writeWord(FileOutputStream stream, int value) throws IOException {
		byte[] b = new byte[2];
		b[0] = (byte) (value & 0xff);
		b[1] = (byte) (value >> 8 & 0xff);
		stream.write(b);
	}

	protected void writeDword(FileOutputStream stream, long value) throws IOException {
		byte[] b = new byte[4];
		b[0] = (byte) (value & 0xff);
		b[1] = (byte) (value >> 8 & 0xff);
		b[2] = (byte) (value >> 16 & 0xff);
		b[3] = (byte) (value >> 24 & 0xff);
		stream.write(b);
	}

	protected void writeLong(FileOutputStream stream, long value) throws IOException {
		byte[] b = new byte[4];
		b[0] = (byte) (value & 0xff);
		b[1] = (byte) (value >> 8 & 0xff);
		b[2] = (byte) (value >> 16 & 0xff);
		b[3] = (byte) (value >> 24 & 0xff);
		stream.write(b);
	}
	
	//将图片保存为bmp格式
	private void saveToBmp(Bitmap bitmap,String newbmpFilePath) {
		if (bitmap == null){
			return;
		}
		// 位图大小
		int nBmpWidth = bitmap.getWidth();
		int nBmpHeight = bitmap.getHeight();
		// 图像数据大小
		int bufferSize = nBmpHeight * (nBmpWidth * 3 + nBmpWidth % 4);
		try {
			// 存储文件名
			String filename = newbmpFilePath;
			File file = new File(filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fileos = new FileOutputStream(filename);
			// bmp文件头
			int bfType = 0x4d42;
			long bfSize = 14 + 40 + bufferSize;
			int bfReserved1 = 0;
			int bfReserved2 = 0;
			long bfOffBits = 14 + 40;
			// 保存bmp文件头
			writeWord(fileos, bfType);
			writeDword(fileos, bfSize);
			writeWord(fileos, bfReserved1);
			writeWord(fileos, bfReserved2);
			writeDword(fileos, bfOffBits);
			// bmp信息头
			long biSize = 40L;
			long biWidth = nBmpWidth;
			long biHeight = nBmpHeight;
			int biPlanes = 1;
			int biBitCount = 24;
			long biCompression = 0L;
			long biSizeImage = 0L;
			long biXpelsPerMeter = 0L;
			long biYPelsPerMeter = 0L;
			long biClrUsed = 0L;
			long biClrImportant = 0L;
			// 保存bmp信息头
			writeDword(fileos, biSize);
			writeLong(fileos, biWidth);
			writeLong(fileos, biHeight);
			writeWord(fileos, biPlanes);
			writeWord(fileos, biBitCount);
			writeDword(fileos, biCompression);
			writeDword(fileos, biSizeImage);
			writeLong(fileos, biXpelsPerMeter);
			writeLong(fileos, biYPelsPerMeter);
			writeDword(fileos, biClrUsed);
			writeDword(fileos, biClrImportant);
			// 像素扫描
			byte bmpData[] = new byte[bufferSize];
			int wWidth = (nBmpWidth * 3 + nBmpWidth % 4);
			for (int nCol = 0, nRealCol = nBmpHeight - 1; nCol < nBmpHeight; ++nCol, --nRealCol) {
				for (int wRow = 0, wByteIdex = 0; wRow < nBmpWidth; wRow++, wByteIdex += 3) {
					int clr = bitmap.getPixel(wRow, nCol);
					bmpData[nRealCol * wWidth + wByteIdex] = (byte) Color.blue(clr);
					bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) Color.green(clr);
					bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) Color.red(clr);
				}
			}

			fileos.write(bmpData);
			fileos.flush();
			fileos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
