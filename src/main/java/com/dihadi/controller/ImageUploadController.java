package com.dihadi.controller;

import java.io.File;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dihadi.config.CloudinaryConfig;

public class ImageUploadController {

    public String imageUpload(File file) {
        Cloudinary cloudinary = CloudinaryConfig.getCloudinary();
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader()
                    .upload(file, ObjectUtils.asMap("resource_type", "image"));
            System.out.println("Cloudinary Upload Result: " + result);
            String url = String.valueOf(result.get("secure_url"));
            System.out.println("Uploaded Image URL: " + url);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
