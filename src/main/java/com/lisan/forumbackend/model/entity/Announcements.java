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
 * 通告表
 * @author lisan
 * @since 2024-07-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("announcements")
@ApiModel(value="Announcements", description="通告表")
public class Announcements implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "通告ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "通告标题")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "通告内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "删除标志，0为未删除，1为已删除")
    @TableField("isDelete")
    private int isDelete;

    @ApiModelProperty(value = "创建时间")
    @TableField("created_at")
    private Date createdAt;

    @ApiModelProperty(value = "更新时间")
    @TableField("updated_at")
    private Date updatedAt;


}
