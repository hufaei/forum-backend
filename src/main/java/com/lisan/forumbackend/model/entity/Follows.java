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
 * 关注表
 * </p>
 *
 * @author lisan
 * @since 2024-07-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("follows")
@ApiModel(value="Follows", description="关注表")
public class Follows implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "关注ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "关注者ID")
    @TableField("follower_id")
    private Long followerId;

    @ApiModelProperty(value = "被关注者ID")
    @TableField("followee_id")
    private Long followeeId;

    @ApiModelProperty(value = "创建时间")
    @TableField("created_at")
    private Date createdAt;


}
