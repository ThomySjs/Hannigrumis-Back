package com.Hannigrumis.api.productImages;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import io.github.cdimascio.dotenv.Dotenv;



@Component
public class ImageService {

    public String upload(MultipartFile multipartFile) {
        Dotenv dotenv = Dotenv.configure()
                        .ignoreIfMissing()
                        .load();

        Map params = ObjectUtils.asMap(
            "use_filename", true,
            "unique_filename", false,
            "overwrite", true
        );

        Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
        try {
            File tempFile = File.createTempFile("upload-", multipartFile.getOriginalFilename());
            multipartFile.transferTo(tempFile);

            Map uploadResult = cloudinary.uploader().upload(tempFile, params);
            tempFile.delete();

            return (String) uploadResult.get("secure_url");
        }
        catch (IOException e) {
            return null;
        }
    }

}
