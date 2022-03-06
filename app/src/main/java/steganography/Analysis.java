package steganography;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;

import Utils.ByteConvertUtil;
import Utils.ByteUtils;

/**
 * @author ethan
 */
/*解析隐写信息类*/
public class Analysis {

	private static final String TAG = "Analysis";
	
	private String bmpFilePath;
	
	private int byteSize;

	private int type = -1;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Analysis(String bmpFilePath, int byteSize) {
		super();
		this.bmpFilePath = bmpFilePath;
		this.byteSize = byteSize;
	}
	
	//将隐写的bit值获取
//	private int getIRgbBit(int source) {
//		return (source & 1);
//
//	}

	//将隐写的bit值获取
	private byte getIRgbBit(int source) {
		return (byte) (source & 1);
	}
	
//	public int[] getBytes() {
//		int[] bitContainer = this.getBitsFromBmp();
//		int[] byteContainer = new int[bitContainer.length/8];
//		//计数器
//		int count = 0;
//		for(int i=0; i<byteContainer.length; i++) {
//			for(int j=0; j<8; j++) {
//				byteContainer[i] += (bitContainer[count]<<j);
//				count++;
//			}
//		}
//		return byteContainer;
//	}

	//得到隐藏数据的字节大小数值
	public int getDecodeContainerLength(byte[] bitLength){
//		int[] byteContainer = new int[bitLength.length/8];
//		//计数器
//		int count = 0;
//		for(int i=0; i<byteContainer.length; i++) {
//			for(int j=0; j<8; j++) {
//				byteContainer[i] += (bitLength[count]<<j);
//				count++;
//			}
//		}
//		int datalength = (byteContainer[0]<<24)|(byteContainer[1]<<16)|(byteContainer[2]<<8)|byteContainer[3];

		byte[] byteContainer = new byte[bitLength.length/8];
		//计数器
		int count = 0;
		for(int i=0; i<byteContainer.length; i++) {
			for(int j=0; j<8; j++) {
				byteContainer[i] += (bitLength[count]<<j);
				count++;
			}
		}
		Log.d(TAG, ByteConvertUtil.byte2hex(byteContainer));
//		将byteContainer转换为int数值
		int dataLength = ByteUtils.bytesToIntBig(byteContainer,0);
		Log.d(TAG,"dataLength:" + dataLength);
		return dataLength;
	}

	public byte[] getBytes() {
		byte[] bitContainer = this.getBitsFromBmp();
		byte[] byteContainer = new byte[bitContainer.length/8];
		//计数器
		int count = 0;
		for(int i=0; i<byteContainer.length; i++) {
			for(int j=0; j<8; j++) {
				byteContainer[i] += (bitContainer[count]<<j);
				count++;
			}
		}
		return byteContainer;
	}

	public byte[] getBitsFromBmp() {
		int curX = 0, curY = 0;
		int count = 0;
		byte[] bitContainer = null;
		byte[] bitLengthContainer = new byte[4*8];
		 type = -1;
		int decodeDataLength = 0;
//		if(byteSize !=0){
//			bitContainer = new byte[this.byteSize*8];
//		}
//		else{
//		}
		try {
			//取前面4*8个字节的最末尾拼成长度信息
			Bitmap image = BitmapFactory.decodeStream(new FileInputStream(bmpFilePath));
			for(curY = 0; curY < image.getHeight(); curY++) {
				
				for(curX = 0; curX < image.getWidth(); curX++) {
					
					//获取像素值
					int rgb = image.getPixel(curX, curY);
					//当前像素值分量
					int iRgb = 0;
					//分解像素值
					int R =(rgb & 0xff0000 ) >> 16 ;
					int G= (rgb & 0xff00 ) >> 8 ;
					int B= (rgb & 0xff );
					if(bitContainer != null){
						if(count >= bitContainer.length+4*8 +1) {
							return bitContainer;
						}
					}
					//将隐写文件位信息放入像素分量最低位
					while(true) {
						if(count == bitLengthContainer.length +1) {
							//取出隐藏数据的长度数值
							//得到bitLengthContainer
							decodeDataLength = getDecodeContainerLength(bitLengthContainer);
							Log.d(TAG,"decodeDataLength:"+decodeDataLength)  ;
							byteSize = decodeDataLength;
							bitContainer = new byte[this.byteSize*8];
						}
						if(bitContainer != null){
							if(count >= bitContainer.length+4*8 +1) {
								break;
							}
						}
						switch (iRgb) {
						case 0:

							if(count == 0){
								//获取类型
								type = this.getIRgbBit(R);
								Log.d(TAG,"type = " + type);
							}
							else if(count < 4*8+1){
								bitLengthContainer[count-1] =  this.getIRgbBit(R);
							}
							else{
								bitContainer[count-4*8-1] = this.getIRgbBit(R);
							}
							break;
							
						case 1:
							if(count == 0){
								//获取类型
								type = this.getIRgbBit(G);
							}
							else if(count < 4*8+1){
								bitLengthContainer[count-1] =  this.getIRgbBit(G);
							}
							else{
								bitContainer[count-4*8-1] = this.getIRgbBit(G);
							}
							break;
							
						case 2:
							if(count == 0){
								//获取类型
								type = this.getIRgbBit(B);
							}
							else  if(count < 4*8+1){
								bitLengthContainer[count-1] =  this.getIRgbBit(B);
							}
							else{
								bitContainer[count-4*8-1] = this.getIRgbBit(B);
							}
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
					
				}
			}
			
		} catch (IOException e) {
			
		}
		return bitContainer;
	}
}
