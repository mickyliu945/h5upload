package com.micky.uploader.controller;


import com.micky.uploader.model.UploadResult;
import com.micky.uploader.utils.AliyunOSSClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UploadController {

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public Object upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        UploadResult uploadResult = new UploadResult();
        try {
            String result = AliyunOSSClient.getInstance().uploadComm(file.getInputStream(), file.getOriginalFilename());
            uploadResult.setCode(200);
            uploadResult.setSrc(result);
        } catch (Exception e) {
            uploadResult.setCode(500);
        }
        return uploadResult;
    }
}
