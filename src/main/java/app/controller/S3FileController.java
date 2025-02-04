package app.controller;

import app.service.S3Service;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class S3FileController {
    // rest controller ; page (html, ..) 없이 바로
    // => Postman 이용 ; 확인


    private final S3Service s3Service;

    @PostMapping(value = "/api/s3/files/win")
    public void uploadS3FileWindows(@RequestPart(value = "file", required = false) MultipartFile file) { // @RequestPart(value="file", required=false) ; 파일 없어도 문제 x
        System.out.println("S3FileController - /api/s3/files - uploadS3FileWindows()");
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getSize());

        try {
            s3Service.uploadS3FileWindows(file);
        } catch (Exception e) {
            // "file" 이 없으면 예외 발생 => 처리
            e.printStackTrace();
        }
    }

    @PostMapping(value = "/api/s3/files/li")
    public void uploadS3FileLinux(@RequestPart(value = "file", required = false) MultipartFile file) { // @RequestPart(value="file", required=false) ; 파일 없어도 문제 x
        System.out.println("S3FileController - /api/s3/files - uploadS3FileLinux()");
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getSize());

        try {
            s3Service.uploadS3FileLinux(file);
        } catch (Exception e) {
            // "file" 이 없으면 예외 발생 => 처리
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/api/s3/files/{fileNo}")
    public ResponseEntity<Resource> downloadS3File(@PathVariable("fileNo") long fileNo) throws Exception {
        return s3Service.downloadS3File(fileNo);
    }

    @GetMapping("/api/s3/files/delete/{fileNo}")
    public void deleteS3File(@PathVariable("fileNo") long fileNo) throws Exception {
        s3Service.deleteS3File(fileNo);
    }
}
