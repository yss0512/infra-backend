package app.tdds.awsbackend.service;

import app.tdds.awsbackend.config.S3Config;
import com.amazonaws.services.s3.model.ObjectMetadata; // ObjectMetadata import 추가
import com.amazonaws.services.s3.model.PutObjectRequest;
// import java.io.File; // 더 이상 필요 없음
import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

@Service
public class ImageService {

  private S3Config s3Config;

  @Autowired
  public ImageService(S3Config s3Config) {
    this.s3Config = s3Config;
  }

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  // 로컬 저장 경로는 더 이상 필요 없습니다.
  // private String localLocation = "C:\\Users\\dbtnt\\S3TEST\\";

  /**
   * 이미지를 S3에 직접 업로드하고 S3 URL을 반환합니다.
   * 로컬 임시 파일 저장 없이 MultipartFile의 InputStream을 사용합니다.
   *
   * @param request MultipartRequest 객체 (업로드된 파일 포함)
   * @return 업로드된 이미지의 S3 URL
   * @throws IOException 파일 처리 중 발생할 수 있는 예외
   */
  public String imageUpload(MultipartRequest request) throws IOException {

    // "upload"라는 이름의 파일을 MultipartRequest에서 가져옵니다.
    // Postman에서 Key를 "upload"로 설정해야 합니다.
    MultipartFile file = request.getFile("upload");

    // 파일이 없는 경우 예외 처리
    if (file == null || file.isEmpty()) {
      throw new IOException("업로드할 파일이 없습니다.");
    }

    String fileName = file.getOriginalFilename();
    // 파일 확장자 추출
    String ext = "";
    if (fileName != null && fileName.contains(".")) { // fileName이 null이 아닌지 확인 추가
      ext = fileName.substring(fileName.lastIndexOf("."));
    }

    // S3에 저장할 고유한 파일명 생성 (UUID 사용)
    String uuidFileName = UUID.randomUUID().toString() + ext;

    // S3에 업로드할 객체의 메타데이터 설정
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize()); // 파일 크기 설정
    metadata.setContentType(file.getContentType()); // 파일 타입 설정 (예: image/jpeg)

    // S3에 객체 업로드 요청 생성
    // MultipartFile의 InputStream을 직접 사용합니다.
    s3Config.amazonS3Client().putObject(new PutObjectRequest(bucket, uuidFileName, file.getInputStream(), metadata));

    // 업로드된 파일의 S3 URL 가져오기
    String s3Url = s3Config.amazonS3Client().getUrl(bucket, uuidFileName).toString();

    // 로컬 임시 파일 삭제 로직은 더 이상 필요 없습니다.

    return s3Url;
  }
}
