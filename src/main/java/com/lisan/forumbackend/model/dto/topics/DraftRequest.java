package com.lisan.forumbackend.model.dto.topics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
public class DraftRequest implements Serializable{
    @JsonProperty("content")
    private String content;
    @JsonProperty("sectionId")
    private Long sectionId;
    @JsonProperty("imageUrls")
    private List<String> imageUrls;

    // tell me 咋解决这个问题啊我去了我也不想写这个但是json数据对不上
    // 带一个字符串参数的构造函数
    public DraftRequest(String jsonString) {

        try {
            // 替换中文逗号为英文逗号
            String correctedJsonString = jsonString.replace('，', ',');
            // 使用 Jackson 解析 JSON 字符串
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(correctedJsonString);

            // 提取各个字段
            this.content = rootNode.path("content").asText();
            this.sectionId = rootNode.path("sectionId").asLong();
            this.imageUrls = objectMapper.convertValue(rootNode.path("imageUrls"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            // 处理 JSON 解析异常
        }
    }
    private static final long serialVersionUID = 1L;

}