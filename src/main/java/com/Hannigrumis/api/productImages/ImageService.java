package com.Hannigrumis.api.productImages;

import java.io.File;
import java.io.IOException;

import org.aspectj.util.FileUtil;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Component
public class ImageService {

    public String createImageFile(MultipartFile file) {

        String basePath = new File(".").getAbsolutePath();
        String uploadPath = basePath + File.separator + "uploads";
        File dir = new File(uploadPath);

        String fileName = file.getOriginalFilename();

        if (!dir.exists()) {
            dir.mkdir();
        }

        try {
            File newImage = new File(uploadPath + File.separator + fileName);
            file.transferTo(newImage);
            return fileName;
        }
        catch (IOException e) {
            return null;
        }

    }


    public byte[] getImage(String filename) {
        String basePath = new File(".").getAbsolutePath();
        String uploadPath = basePath + File.separator + "uploads";

        File file = new File(uploadPath + File.separator + filename);
        if (!file.exists()) {
            String defaultPath = new File("src/main/resources/static/images").getAbsolutePath();
            file = new File(defaultPath + File.separator + "default.png");
        }
        try{
            return FileUtil.readAsByteArray(file);
        }
        catch (IOException e) {
            return null;
        }

    }

    public Boolean validateFormat(String fileName) {
        String[] nameParts = fileName.split(".");

        System.out.println(nameParts);

        if (nameParts[1].equalsIgnoreCase("jpg")) {
            return true;
        }

        return false;
    }

}
