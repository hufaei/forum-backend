package com.lisan.forumbackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 话题表
 * @author lisan
 * @since 2024-07-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("topics")
@ApiModel(value="Topics", description="话题表")
public class Topics implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "话题ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "所属板块ID")
    @TableField("section_id")
    private Long sectionId;

    @ApiModelProperty(value = "发布用户ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "话题内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "图片URL")
    @TableField("image")
    private String image;

    @ApiModelProperty(value = "创建时间")
    @TableField("created_at")
    private Date createdAt;

    @ApiModelProperty(value = "更新时间")
    @TableField("updated_at")
    private Date updatedAt;


}
