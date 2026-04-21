package TEMAN.service;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadImage(MultipartFile file) {
        // 파일이 비었는지 확인
        if (file.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 존재하지 않습니다.");
        }

        // 원본 파일명 추출
        String originalFilename = file.getOriginalFilename();

        // UUID(랜덤 난수)를 붙여서 새로운 이름 생성
        String s3FileName = UUID.randomUUID().toString() + "_" + originalFilename;

        // S3에 올리기 - 스트림
        try (InputStream inputStream = file.getInputStream()) {
            s3Template.upload(bucket, s3FileName, inputStream, ObjectMetadata.builder().contentType(file.getContentType()).build());
        } catch (IOException e) {
            throw new RuntimeException("S3 이미지 업로드에 실패했습니다.", e);
        }

        // 업로드 이미지의 공용 URL 반환
        return "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + s3FileName;
    }
}