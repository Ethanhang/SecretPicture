package steganography;

import android.graphics.Bitmap;

public interface EncodingCallBack {

    void onStartEncoding();

    void onCompleteEncoding(Bitmap bitmap, String newbmpFilePath);
}
