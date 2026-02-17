package com.jorge.ecommerce.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file, String folder) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "ecommerce/" + folder,
                        "resource_type", "image"
                )
        );
        return (String) uploadResult.get("secure_url");
    }

    public void deleteImage(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isEmpty()) return;
        String publicId = extractPublicId(imageUrl);
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    private String extractPublicId(String imageUrl) {
        String[] parts = imageUrl.split("/");
        String filename = parts[parts.length - 1];
        String folder = parts[parts.length - 2];
        String subfolder = parts[parts.length - 3];
        return subfolder + "/" + folder + "/" + filename.split("\\.")[0];
    }

    public List<String> getGalleryImages(String folder) throws Exception {
        Map result = cloudinary.api().resources(
                ObjectUtils.asMap(
                        "type", "upload",
                        "prefix", "ecommerce/gallery/" + folder,
                        "max_results", 50
                )
        );

        List<Map> resources = (List<Map>) result.get("resources");
        return resources.stream()
                .map(r -> (String) r.get("secure_url"))
                .collect(java.util.stream.Collectors.toList());
    }
}