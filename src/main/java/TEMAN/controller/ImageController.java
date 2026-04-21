package TEMAN.controller;

import TEMAN.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/images")
public class ImageController {
    private final S3Service s3Service;

    @PostMapping
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile multipartFile) {
        String imageUrl = s3Service.uploadImage(multipartFile);
        return ResponseEntity.ok(imageUrl);
    }
}
