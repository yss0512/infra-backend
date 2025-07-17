package app.tdds.awsbackend.controller;

import app.tdds.awsbackend.service.ImageService;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartRequest;

@Controller
public class ImageController {

  private ImageService imageService;

  @Autowired
  public ImageController(ImageService imageService) {

    this.imageService = imageService;
  }


  @PostMapping("/image/upload")
  @ResponseBody
  public Map<String, Object> imageUpload(MultipartRequest request) throws Exception {

    Map<String, Object> responseData = new HashMap<>();

    try {

      String s3Url = imageService.imageUpload(request);

      responseData.put("uploaded", true);
      responseData.put("url", s3Url);

      return responseData;

    } catch (IOException e) {
      responseData.put("uploaded", false);
      return responseData;
    }
  }
}