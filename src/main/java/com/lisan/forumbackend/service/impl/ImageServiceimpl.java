package com.lisan.forumbackend.service.impl;

import com.lisan.forumbackend.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.lisan.forumbackend.model.enums.TuccEnum;
import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class ImageServiceimpl implements ImageService {

    private static final String UPLOAD_URL = "http://api.tucang.cc/api/v1/upload";

    @Resource
    private RestTemplate restTemplate;

    @Override
    public String uploadImage(MultipartFile file,TuccEnum tuccEnum) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 构造请求体
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();


            // 将 MultipartFile 转换为 ByteArrayResource
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename(); // 返回文件的原始名称
                }
            };

            // 参数构造——详见文档：http://doc.tucang.cc/web/#/633676868/168509282
            body.add("token", TuccEnum.TOKEN.getValue());
            body.add("file", resource);
            body.add("folderId",tuccEnum.getValue());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity((String) TuccEnum.UPLOAD_URL.getValue(), requestEntity, Map.class);

            // 检查响应
            if (response.getStatusCode() == HttpStatus.OK && Boolean.TRUE.equals(response.getBody().get("success"))) {
                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                return Optional.ofNullable((String) data.get("url")).orElseThrow(() ->
                        new RuntimeException("上传失败，未获取到图片URL"));
            } else {
                throw new RuntimeException("图片上传失败: " + response.getBody().get("msg"));
            }

        } catch (Exception e) {
            log.error("图片上传失败", e);
            throw new RuntimeException("图片上传失败: " + e.getMessage());
        }
    }
}
