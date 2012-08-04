package prb.creations.chiringuito.ARviewer.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class IOUtils {
//    public static Bitmap getBitmapFromURL(String src) {
//        try {
//            URL url = new URL(src);
//            HttpURLConnection connection = (HttpURLConnection) url
//                    .openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            Bitmap myBitmap = BitmapFactory.decodeStream(input);
//            return myBitmap;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
    public static Bitmap getBitmapFromURL(String str_url) {
        Bitmap bitmap = null;
        InputStream in = null;
        OutputStream out = null;

        
        try {
          URL url = new URL(str_url);  
          in = new BufferedInputStream(url.openStream(), 4 * 1024);

          final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
          out = new BufferedOutputStream(dataStream, 4 * 1024);
          copy(in, out);
          out.flush();

          final byte[] data = dataStream.toByteArray();
          bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
          // Log.e(LOG_TAG, "bitmap returning something");
          return bitmap;
        } catch (IOException e) {
          // Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        } finally {
          closeStream(in);
          closeStream(out);
        }
        // Log.e(LOG_TAG, "bitmap returning null");
        return null;
      }



      private static void copy(InputStream in, OutputStream out)
          throws IOException {
        byte[] b = new byte[4 * 1024];
        int read;
        while ((read = in.read(b)) != -1) {
          out.write(b, 0, read);
        }
      }

      private static void closeStream(Closeable stream) {
        if (stream != null) {
          try {
            stream.close();
          } catch (IOException e) {
            // Log.e(LOG_TAG, e.getMessage());
          }
        }
      }
}
