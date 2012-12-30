package jp.yoshi_misa.gallery;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class MainActivity extends Activity {

    GridView mGrid;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // レコードの取得
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
                null);

        List<Bean> exifInterfaceList = new ArrayList<Bean>();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Images.Media._ID));

            Uri bmpUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            String path = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));

            Log.v("Uri", bmpUri.toString() + " : " + path);

            if (path.indexOf("Camera") != -1) {
                try {
                    Bean bean = new Bean();
                    bean.setBmpUri(bmpUri);
                    bean.setExifInterface(new ExifInterface(path));

                    exifInterfaceList.add(bean);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        cursor.close();
        cursor = null;

        /* Setting GridView */
        mGrid = (GridView) findViewById(R.id.gridView1);
        mGrid.setAdapter(new MyAdapter(exifInterfaceList));
    }

    // GridView用のCustomAdapter
    //
    public class MyAdapter extends BaseAdapter {
        private List<Bean> exifInterfaceList;
        ImageView imageView;

        public MyAdapter(List<Bean> exifInterfaceList) {
            this.exifInterfaceList = exifInterfaceList;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                imageView = new ImageView(MainActivity.this);
                imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            } else {
                imageView = (ImageView) convertView;
            }

            Bean bean = exifInterfaceList.get(position);
            ExifInterface mExifInterface = bean.getExifInterface();

            if (mExifInterface != null) {
                showExif(mExifInterface);
                Bitmap mBitmap = showThumbnail(mExifInterface);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                mBitmap.compress(CompressFormat.JPEG, 100, bos);

                mBitmap = getPreResizedImage(bos.toByteArray(), 200, 200);

                int orientation = mExifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL: {
                    imageView.setImageBitmap(mBitmap);

                    break;
                }
                case ExifInterface.ORIENTATION_ROTATE_90: {
                    Matrix mat = new Matrix();
                    mat.postRotate(90);

                    // 回転したビットマップを作成
                    Bitmap bmp = Bitmap.createBitmap(mBitmap, 0, 0,
                            mBitmap.getWidth(), mBitmap.getHeight(), mat, true);

                    imageView.setImageBitmap(bmp);

                    break;
                }
                case ExifInterface.ORIENTATION_ROTATE_180: {
                    Matrix mat = new Matrix();
                    mat.postRotate(180);

                    // 回転したビットマップを作成
                    Bitmap bmp = Bitmap.createBitmap(mBitmap, 0, 0,
                            mBitmap.getWidth(), mBitmap.getHeight(), mat, true);

                    imageView.setImageBitmap(bmp);

                    break;
                }
                case ExifInterface.ORIENTATION_ROTATE_270: {
                    Matrix mat = new Matrix();
                    mat.postRotate(270);

                    // 回転したビットマップを作成
                    Bitmap bmp = Bitmap.createBitmap(mBitmap, 0, 0,
                            mBitmap.getWidth(), mBitmap.getHeight(), mat, true);

                    imageView.setImageBitmap(bmp);

                    break;
                }
                }
            } else {
                try {
                    Bitmap mBitmap = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), bean.getBmpUri());

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    mBitmap.compress(CompressFormat.JPEG, 100, bos);

                    mBitmap = getPreResizedImage(bos.toByteArray(), 200, 200);

                    imageView.setImageBitmap(mBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return imageView;
        }

        private Bitmap showThumbnail(ExifInterface exifInterface) {
            byte[] data = exifInterface.getThumbnail();

            if (null == data) {
                return null;
            }

            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }

        private void showExif(ExifInterface ei) {
            // API 5
            String exifString = getExifString(ei, ExifInterface.TAG_DATETIME)
                    + "\n";
            exifString += getExifString(ei, ExifInterface.TAG_FLASH) + "\n";
            exifString += getExifString(ei, ExifInterface.TAG_GPS_LATITUDE)
                    + "\n";
            exifString += getExifString(ei, ExifInterface.TAG_GPS_LONGITUDE)
                    + "\n";
            exifString += getExifString(ei, ExifInterface.TAG_IMAGE_LENGTH)
                    + "\n";
            exifString += getExifString(ei, ExifInterface.TAG_IMAGE_WIDTH)
                    + "\n";
            exifString += getExifString(ei, ExifInterface.TAG_MAKE) + "\n";
            exifString += getExifString(ei, ExifInterface.TAG_MODEL) + "\n";
            exifString += getExifString(ei, ExifInterface.TAG_ORIENTATION)
                    + "\n";
            exifString += getExifString(ei, ExifInterface.TAG_WHITE_BALANCE)
                    + "\n";

            // API 8
            exifString += getExifString(ei, ExifInterface.TAG_FOCAL_LENGTH)
                    + "\n";
            exifString += getExifString(ei, ExifInterface.TAG_GPS_DATESTAMP)
                    + "\n";
            exifString += getExifString(ei,
                    ExifInterface.TAG_GPS_PROCESSING_METHOD) + "\n";
            exifString += getExifString(ei, ExifInterface.TAG_GPS_TIMESTAMP)
                    + "\n";

            // API 9
            exifString += getExifString(ei, ExifInterface.TAG_GPS_ALTITUDE)
                    + "\n";

            // Honeycomb
            // exifString +=getExifString(ei, ExifInterface.TAG_APERTURE) +
            // "\n";
            // exifString +=getExifString(ei, ExifInterface.TAG_EXPOSURE_TIME) +
            // "\n";
            // exifString +=getExifString(ei, ExifInterface.TAG_ISO) + "\n";

            Log.d("Pic", exifString);
            Log.d("Pic", "　　");
        }

        private String getExifString(ExifInterface ei, String tag) {
            return tag + ": " + ei.getAttribute(tag);
        }

        public final int getCount() {
            return exifInterfaceList.size();
        }

        public final Object getItem(int position) {
            return position;
        }

        public final long getItemId(int position) {
            return position;
        }

    }

    private Bitmap getPreResizedImage(byte[] imageData, int viewHeight,
            int viewWidth) {

        // Optionsインスタンスを取得
        BitmapFactory.Options options = new BitmapFactory.Options();

        // Bitmapを生成せずにサイズを取得する
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);

        if (viewHeight == 0 || viewWidth == 0) {
            // 等倍（リサイズしない）
            options.inSampleSize = 1;
        } else {
            // 設定するImageViewのサイズにあわせてリサイズ
            options.inSampleSize = Math.max(options.outHeight / viewHeight,
                    options.outWidth / viewWidth);
        }

        // 実際にBitmapを生成する
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length,
                options);

    }
}
