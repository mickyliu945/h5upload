package com.micky.uploader.controller;


import com.micky.uploader.model.UploadResult;
import com.micky.uploader.utils.AliyunOSSClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UploadController {

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public Object upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        return upload(file);
    }

    @RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
    @ResponseBody
    public Object uploadImage(@RequestParam("files") MultipartFile file, HttpServletRequest request) {
       UploadResult result = upload(file);
       if (result.getCode() != 200) {
           throw new RuntimeException("图片上传失败");
       }
       return result;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Object delete(@RequestParam(value = "url", required = false, defaultValue = "") String url, HttpServletRequest request) {
        boolean b = false;
        if (!StringUtils.isBlank(url)) {
            b = AliyunOSSClient.getInstance().deleteFileByUrl(url);
        }
        UploadResult uploadResult = new UploadResult();
        uploadResult.setCode(b ? 200 : 500);
        uploadResult.setSrc(url);
        return uploadResult;
    }

    private UploadResult upload(MultipartFile file) {
        UploadResult uploadResult = new UploadResult();
        try {
            String result = AliyunOSSClient.getInstance().uploadComm(file.getInputStream(), file.getOriginalFilename());
            uploadResult.setSrc(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        uploadResult.setCode(StringUtils.isBlank(uploadResult.getSrc()) ? 500 : 200);
        return uploadResult;
    }
}
