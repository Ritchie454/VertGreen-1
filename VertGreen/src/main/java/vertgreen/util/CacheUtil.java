package vertgreen.util;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CacheUtil {

    private static HashMap<String, File> cachedURLFiles = new HashMap<>();

    private CacheUtil() {
    }

    public static File getImageFromURL(String url) {
        if (cachedURLFiles.containsKey(url) && cachedURLFiles.get(url).exists()) {
            //Already cached
            return cachedURLFiles.get(url);
        } else {
            InputStream is;
            FileOutputStream fos;
            File tmpFile = null;
            try {
                Matcher matcher = Pattern.compile("(\\.\\w+$)").matcher(url);
                String type = matcher.find() ? matcher.group(1) : "";
                tmpFile = File.createTempFile(UUID.randomUUID().toString(), type);
                is = Unirest.get(url).asBinary().getRawBody();
                FileWriter writer = new FileWriter(tmpFile);
                fos = new FileOutputStream(tmpFile);

                byte[] buffer = new byte[1024 * 10];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                is.close();
                fos.close();

                cachedURLFiles.put(url, tmpFile);
                return tmpFile;
            } catch (IOException ex) {
                tmpFile.delete();
                throw new RuntimeException(ex);
            } catch (UnirestException ex) {
                tmpFile.delete();
                throw new RuntimeException(ex);
            }
        }
    }

}
