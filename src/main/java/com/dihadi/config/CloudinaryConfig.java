package com.dihadi.config;

import java.util.HashMap;
import java.util.Map;

import com.cloudinary.Cloudinary;

public class CloudinaryConfig {

    public static Cloudinary cloudinary;

    public static Cloudinary getCloudinary() {
        if (cloudinary == null) {
            Map<String, Object> config = new HashMap<>();
            config.put("cloud_name", "khkrdkxe");
            config.put("api_key", "837339818453156");
            config.put("api_secret", "hFUsVvdqeklA8KRGYJwbD62BH4w");
            config.put("secure", true);

            cloudinary = new Cloudinary(config);
        }
        return cloudinary;
    }
}
