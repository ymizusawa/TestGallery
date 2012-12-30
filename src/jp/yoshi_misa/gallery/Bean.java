package jp.yoshi_misa.gallery;

import android.media.ExifInterface;
import android.net.Uri;

public class Bean {

    private Uri bmpUri;
    private ExifInterface exifInterface;

    public void setExifInterface(ExifInterface exifInterface) {
        this.exifInterface = exifInterface;
    }

    public Uri getBmpUri() {
        return bmpUri;
    }

    public void setBmpUri(Uri bmpUri) {
        this.bmpUri = bmpUri;
    }

    public ExifInterface getExifInterface() {
        return exifInterface;
    }

}
