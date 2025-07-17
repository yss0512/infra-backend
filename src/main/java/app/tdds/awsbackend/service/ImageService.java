package app.tdds.awsbackend.service;

import app.tdds.awsbackend.config.S3Config;
// import com.amazonaws.services.s3.model.CannedAccessControlList; // ACL 관련 import는 더 이상 필요 없으므로 주석 처리 또는 삭제
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
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

  // 로컬 저장 경로 (임시 파일 저장용)
  private String localLocation = "C:\\Users\\dbtnt\\S3TEST\\";

  /**
   * 이미지를 S3에 업로드하고 S3 URL을 반환합니다.
   *
   * @param request MultipartRequest 객체 (업로드된 파일 포함)
   * @return 업로드된 이미지의 S3 URL
   * @throws IOException 파일 처리 중 발생할 수 있는 예외
   */
  public String imageUpload(MultipartRequest request) throws IOException {

    // "upload"라는 이름의 파일을 MultipartRequest에서 가져옵니다.
    // Postman에서 Key를 "upload"로 설정해야 합니다.
    MultipartFile file = request.getFile("upload");

    // 파일이 없는 경우 예외 처리 (선택 사항, 필요에 따라 추가)
    if (file == null || file.isEmpty()) {
      throw new IOException("업로드할 파일이 없습니다.");
    }

    String fileName = file.getOriginalFilename();
    // 파일 확장자 추출
    String ext = "";
    if (fileName.contains(".")) {
      ext = fileName.substring(fileName.lastIndexOf("."));
    }

    // S3에 저장할 고유한 파일명 생성 (UUID 사용)
    String uuidFileName = UUID.randomUUID().toString() + ext;
    // 로컬 임시 파일 경로
    String localPath = localLocation + uuidFileName;

    File localFile = new File(localPath);
    // 로컬 임시 파일에 업로드된 파일 저장
    file.transferTo(localFile);

    // S3에 객체 업로드 요청 생성
    // ACL 설정을 제거했습니다. (withCannedAcl 부분 삭제)
    // 최신 S3 버킷은 기본적으로 ACL이 비활성화되어 있으므로,
    // ACL을 설정하려고 하면 "AccessControlListNotSupported" 오류가 발생합니다.
    // 공개 접근이 필요하다면, S3 버킷 정책을 통해 설정해야 합니다.
    s3Config.amazonS3Client().putObject(new PutObjectRequest(bucket, uuidFileName, localFile));

    // 업로드된 파일의 S3 URL 가져오기
    String s3Url = s3Config.amazonS3Client().getUrl(bucket, uuidFileName).toString();

    // 로컬에 저장했던 임시 파일 삭제
    localFile.delete();

    return s3Url;
  }
}