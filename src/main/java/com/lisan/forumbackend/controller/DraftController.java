package com.lisan.forumbackend.controller;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.lisan.forumbackend.common.BaseResponse;
import com.lisan.forumbackend.common.ResultUtils;
import com.lisan.forumbackend.model.dto.topics.DraftRequest;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/drafts")
public class DraftController {

    @PostMapping("/save")
    public BaseResponse<String> saveDraft(@RequestBody DraftRequest draftRequest) {
        // 获取当前用户的 SaSession
        SaSession session = StpUtil.getSession();

        // 存储草稿数据
        session.set("draft_"+StpUtil.getLoginId(), draftRequest);

        return ResultUtils.success("保存成功");
    }

    @GetMapping("/get")
    public BaseResponse<Object> getDraft() {
        // 获取当前用户的 SaSession
        SaSession session = StpUtil.getSession();
        // 从会话中获取草稿数据
        Object draft = session.get("draft_"+StpUtil.getLoginId());
        System.out.println(session.getTimeout());

        return ResultUtils.success(draft);
    }
}
