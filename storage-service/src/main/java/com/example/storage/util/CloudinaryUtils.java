package com.example.storage.util;

public class CloudinaryUtils {
    public static String buildCloudinaryUrl(String originalUrl, Integer width, Integer height, Double ratio) {
        if ((width == null && height == null && ratio == null)) {
            return originalUrl;
        }

        StringBuilder transformation = new StringBuilder("c_fill");

        if (ratio != null) {
            transformation.append(",ar_").append(ratio);
        } else {
            if (width != null) transformation.append(",w_").append(width);
            if (height != null) transformation.append(",h_").append(height);
        }

        return originalUrl.replace("/upload/", "/upload/" + transformation + "/");
    }


}
