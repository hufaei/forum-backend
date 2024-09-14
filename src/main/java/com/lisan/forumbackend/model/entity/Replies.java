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
 * 回复表
 * @author lisan
 * @since 2024-07-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("replies")
@ApiModel(value="Replies", description="回复表")
public class Replies implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "回复ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "所属评论ID")
    @TableField("comment_id")
    private Long commentId;

    @ApiModelProperty(value = "回复用户ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "回复内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "创建时间")
    @TableField("created_at")
    private Date createdAt;

    @ApiModelProperty(value = "更新时间")
    @TableField("updated_at")
    private Date updatedAt;


}
