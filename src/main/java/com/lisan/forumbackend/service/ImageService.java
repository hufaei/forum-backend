package com.lisan.forumbackend.service;

import com.lisan.forumbackend.model.enums.TuccEnum;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface ImageService {
    String uploadImage(MultipartFile file, TuccEnum tuccEnum);
}