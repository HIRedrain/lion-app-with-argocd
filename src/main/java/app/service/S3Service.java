package app.service;

import app.entity.AttachmentFile;
import app.repository.AttachmentFileRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class S3Service {

    private final AmazonS3 amazonS3;
    private final AttachmentFileRepository fileRepository;
    private final DefaultSslBundleRegistry sslBundleRegistry;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final String DIR_NAME = "s3_data";

    // 파일 업로드
    public void uploadS3FileWindows(MultipartFile file) throws Exception {

        // C:/CE/97.data/s3_data에 파일 저장 -> S3 전송 및 저장 (putObject)
        // D://CE//97.data//s3_data
        if (file == null) {
            throw new Exception("파일 전달 오류 발생");
        }

        String filePath = "D://CE//97.data//" + DIR_NAME;
        String fileName = file.getOriginalFilename(); // attachmentOriginalFileName
        UUID uuid = UUID.randomUUID(); // uuid 난수 생성
        String saveName = uuid.toString() + "_" + fileName; // attachmentFileName
        Long fileSize = file.getSize(); // attachmentFileSize

        // 새로운 entity 객체 생성
        AttachmentFile attachmentFile = AttachmentFile.builder()
                .filePath(filePath)
                .attachmentOriginalFileName(fileName)
                .attachmentFileName(saveName)
                .attachmentFileSize(fileSize)
                .build();

        Long fileNo = fileRepository.save(attachmentFile).getAttachmentFileNo(); // DB 에 저장
        if (fileNo != null) {
            // DB 에 제대로 저장된 것 => application 통해서 s3 에 해당 파일 업로드 ; 로컬에 먼저 저장해서 s3 로 업로드
            System.out.println("S3Service - uploadS3File - file Number = " + fileNo);

            File uploadFile = new File(attachmentFile.getFilePath() + "//" + saveName);
            file.transferTo(uploadFile); // 물리적으로 로컬에 저장 - 이거 주석 처리 하면? => error 발생 (s3 에 업로드 불가)

            // S3 전송 => 저장 ; amazonS3.putObject(bucket, key, file)
            // bucket ; bucket 이름
            // key ; bucket 내부에 객체가 저장되는 경로 + 파일명
            // file ; 올릴 파일
            String s3Key = DIR_NAME + "/" + uploadFile.getName();
            amazonS3.putObject(bucketName, s3Key, uploadFile);

            if (uploadFile.exists()) { // 올린 파일 로컬에 여전히 존재 => 삭제
                uploadFile.delete();

                //amazonS3.deleteObject(bucketName, s3Key); // bucket 에 올린 것 삭제
            }
        }

    }

    public void uploadS3FileLinux(MultipartFile file) throws Exception {

        // C:/CE/97.data/s3_data에 파일 저장 -> S3 전송 및 저장 (putObject)
        // D://CE//97.data//s3_data
        if (file == null) {
            throw new Exception("파일 전달 오류 발생");
        }

        String filePath = "/usr/local/s3_data/" + DIR_NAME;
        String fileName = file.getOriginalFilename(); // attachmentOriginalFileName
        UUID uuid = UUID.randomUUID(); // uuid 난수 생성
        String saveName = uuid.toString() + "_" + fileName; // attachmentFileName
        Long fileSize = file.getSize(); // attachmentFileSize

        // 새로운 entity 객체 생성
        AttachmentFile attachmentFile = AttachmentFile.builder()
                .filePath(filePath)
                .attachmentOriginalFileName(fileName)
                .attachmentFileName(saveName)
                .attachmentFileSize(fileSize)
                .build();

        Long fileNo = fileRepository.save(attachmentFile).getAttachmentFileNo(); // DB 에 저장
        if (fileNo != null) {
            // DB 에 제대로 저장된 것 => application 통해서 s3 에 해당 파일 업로드 ; 로컬에 먼저 저장해서 s3 로 업로드
            System.out.println("S3Service - uploadS3File - file Number = " + fileNo);

            File uploadFile = new File(attachmentFile.getFilePath() + "//" + saveName);
            file.transferTo(uploadFile); // 물리적으로 로컬에 저장 - 이거 주석 처리 하면? => error 발생 (s3 에 업로드 불가)

            // S3 전송 => 저장 ; amazonS3.putObject(bucket, key, file)
            // bucket ; bucket 이름
            // key ; bucket 내부에 객체가 저장되는 경로 + 파일명
            // file ; 올릴 파일
            String s3Key = DIR_NAME + "/" + uploadFile.getName();
            amazonS3.putObject(bucketName, s3Key, uploadFile);

            if (uploadFile.exists()) { // 올린 파일 로컬에 여전히 존재 => 삭제
                uploadFile.delete();

                //amazonS3.deleteObject(bucketName, s3Key); // bucket 에 올린 것 삭제
            }
        }

    }

    // 파일 다운로드
    public ResponseEntity<Resource> downloadS3File(long fileNo) {
        AttachmentFile attachmentFile = null;
        Resource resource = null;

        // DB에서 파일 검색 -> S3의 파일 가져오기 (getObject) -> 전달

        try {
            // DB 에서 파일 정보 get
            attachmentFile = fileRepository.findById(fileNo).orElseThrow(() -> new NoSuchElementException("파일 존재 x"));

            // S3 에서 파일 get ; amazonS3.getObject(bucket, key)
            // bucket ; bucket 이름
            // key ; 경로 + 파일명
            String key = DIR_NAME + "/" + attachmentFile.getAttachmentFileName();
            S3Object s3Object = amazonS3.getObject(bucketName, key);
            S3ObjectInputStream s3Is = s3Object.getObjectContent(); // 자동 매핑
            resource = new InputStreamResource(s3Is); // resource 로 매핑
        } catch (Exception e) {
            return new ResponseEntity<Resource>(resource, null, HttpStatus.NO_CONTENT);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition
                .builder("attachment")
                .filename(attachmentFile.getAttachmentOriginalFileName()) // 이렇게 적으면 uuid 값 없이 저장 (원래 파일 이름으로)
                //.filename("Hello,World!")
                .build());

        return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
    }

    // 파일 삭제
    public void deleteS3File(long fileNo) {
        AttachmentFile attachmentFile = null;


        attachmentFile = fileRepository.findById(fileNo).orElseThrow(() -> new NoSuchElementException("파일 존재 x"));
        if (attachmentFile == null) {
            System.out.println("fileNo = " + fileNo + " - not exist!");

            return;
        }

        try {
            // DB 에서 파일 정보 get

            // S3 에서 파일 get ; amazonS3.getObject(bucket, key)
            // bucket ; bucket 이름
            // key ; 경로 + 파일명
            String key = DIR_NAME + "/" + attachmentFile.getAttachmentFileName();
            amazonS3.deleteObject(bucketName, key);

            // DB 에서도 지워야 함
            fileRepository.deleteById(fileNo);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
