package com.lisan.forumbackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * <p>
 * 板块表
 * </p>
 *
 * @author lisan
 * @since 2024-07-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sections")
@ApiModel(value="Sections", description="板块表")
public class Sections implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "板块ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "板块名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "板块描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "创建时间")
    @TableField("created_at")
    private Date createdAt;

    @ApiModelProperty(value = "更新时间")
    @TableField("updated_at")
    private Date updatedAt;


}
