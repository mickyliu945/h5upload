package com.micky.uploader.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AliyunOSSClient {

    // log日志
    private Logger logger = Logger.getLogger(AliyunOSSClient.class);

    private OSSClient ossClient;

    private AliyunOSSConfig aliyunOSSConfig;

    private FileNameGenerator fileNameGenerator;

    private AliyunOSSClient() {
        fileNameGenerator = new FileNameGenerator() {
            @Override
            public String generateName() {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                return simpleDateFormat.format(new Date());
            }
        };
    }

    private static class AliyunOSSClientUtilHolder {
        private final static AliyunOSSClient INSTANCE = new AliyunOSSClient();
    }

    public static AliyunOSSClient getInstance() {
        return AliyunOSSClientUtilHolder.INSTANCE;
    }

    public void init(AliyunOSSConfig aliyunOSSConfig) throws Exception {
        this.aliyunOSSConfig = aliyunOSSConfig;
        if (aliyunOSSConfig == null) {
            throw new RuntimeException("文件服务配置不存在!");
        }
        if (StringUtils.isBlank(aliyunOSSConfig.getEndPoint())) {
            throw new InvalidParameterException("endPoint不能为空!");
        }

        if (StringUtils.isBlank(aliyunOSSConfig.getAccessKeyId())) {
            throw new InvalidParameterException("accessKeyId不能为空!");
        }

        if (StringUtils.isBlank(aliyunOSSConfig.getAccessKeySecret())) {
            throw new InvalidParameterException("accessKeySecret不能为空!");
        }

        if (StringUtils.isBlank(aliyunOSSConfig.getBucketName())) {
            throw new InvalidParameterException("backetName不能为空!");
        }

        if (StringUtils.isBlank(aliyunOSSConfig.getAccessUrl())) {
            throw new InvalidParameterException("accessUrl不能为空!");
        }

        ossClient = new OSSClient(aliyunOSSConfig.getEndPoint(), aliyunOSSConfig.getAccessKeyId(), aliyunOSSConfig.getAccessKeySecret());
    }

    public void setFileNameGenerator(FileNameGenerator fileNameGenerator) {
        this.fileNameGenerator = fileNameGenerator;
    }

    /**
     * 创建存储空间
     *
     * @param ossClient  OSS连接
     * @param bucketName 存储空间
     * @return
     */
    public String createBucketName(OSSClient ossClient, String bucketName) {
        // 存储空间
        final String bucketNames = bucketName;
        if (!ossClient.doesBucketExist(bucketName)) {
            // 创建存储空间
            Bucket bucket = ossClient.createBucket(bucketName);
            return bucket.getName();
        }
        return bucketNames;
    }

    /**
     * 删除存储空间buckName
     *
     * @param ossClient  oss对象
     * @param bucketName 存储空间
     */
    public void deleteBucket(OSSClient ossClient, String bucketName) {
        ossClient.deleteBucket(bucketName);
        logger.info("删除" + bucketName + "Bucket成功");
    }

    /**
     * 创建模拟文件夹
     *
     * @param ossClient  oss连接
     * @param bucketName 存储空间
     * @param folder     模拟文件夹名如"qj_nanjing/"
     * @return 文件夹名
     */
    public String createFolder(OSSClient ossClient, String bucketName, String folder) {
        // 文件夹名
        final String keySuffixWithSlash = folder;
        // 判断文件夹是否存在，不存在则创建
        if (!ossClient.doesObjectExist(bucketName, keySuffixWithSlash)) {
            // 创建文件夹
            ossClient.putObject(bucketName, keySuffixWithSlash, new ByteArrayInputStream(new byte[0]));
            logger.info("创建文件夹成功");
            // 得到文件夹名
            OSSObject object = ossClient.getObject(bucketName, keySuffixWithSlash);
            String fileDir = object.getKey();
            return fileDir;
        }
        return keySuffixWithSlash;
    }

    /**
     * 根据key删除OSS服务器上的文件
     *
     * @param ossClient  oss连接
     * @param bucketName 存储空间
     * @param folder     模拟文件夹名 如"qj_nanjing/"
     * @param key        Bucket下的文件的路径名+文件名 如："upload/cake.jpg"
     */
    public void deleteFile(OSSClient ossClient, String bucketName, String folder, String key) {
        ossClient.deleteObject(bucketName, folder + key);
        logger.info("删除" + bucketName + "下的文件" + folder + key + "成功");
    }

    /**
     * 上传图片至OSS的私有Bucket
     *
     * @param inputStream  文件输入流
     * @param destFileName 上传之后的文件名
     * @return
     */
    public String uploadPrvObject2OSS(InputStream inputStream, String destFileName) {
        String resultStr = null;
        try {
            // 创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            // 上传的文件的长度
            metadata.setContentLength(inputStream.available());
            // 指定该Object被下载时的网页的缓存行为
            metadata.setCacheControl("no-cache");
            // 指定该Object下设置Header
            metadata.setHeader("Pragma", "no-cache");
            // 指定该Object被下载时的内容编码格式
            metadata.setContentEncoding("utf-8");
            // 文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
            // 如果没有扩展名则填默认值application/octet-stream
            metadata.setContentType(getContentType(destFileName));
            // 指定该Object被下载时的名称（指示MINME用户代理如何显示附加的文件，打开或下载，及文件名称）
            metadata.setContentDisposition("filename/filesize=" + destFileName + "/" + inputStream.available() + "Byte.");
            // 上传文件 (上传文件流的形式)
            PutObjectResult putResult = ossClient.putObject(aliyunOSSConfig.getBucketName(), aliyunOSSConfig.getFolder() + destFileName, inputStream, metadata);
            // 解析结果
            if (!StringUtils.isBlank(putResult.getETag())) {
                resultStr = aliyunOSSConfig.getFolder() + destFileName;
                resultStr = getUrl(resultStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        }
        return resultStr;
    }

    /**
     * 上传图片至OSS的共有的Bucket
     *
     * @param inputStream  文件输入流
     * @param destFileName 上传之后的文件名
     * @return
     */
    public String uploadCommObject2OSS(InputStream inputStream, String destFileName) {
        String result = "";
        try {
            OSSClient ossClient = new OSSClient(aliyunOSSConfig.getEndPoint(), aliyunOSSConfig.getAccessKeyId(), aliyunOSSConfig.getAccessKeySecret());
            //创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(inputStream.available());
            metadata.setCacheControl("no-cache");
            metadata.setHeader("Pragma", "no-cache");
            metadata.setContentType(getContentType(destFileName));
            metadata.setContentDisposition("filename/filesize=" + destFileName + "/" + inputStream.available() + "Byte.");

            //上传文件
            ossClient.putObject(aliyunOSSConfig.getBucketName(), aliyunOSSConfig.getFolder() + destFileName, inputStream, metadata);
            result = aliyunOSSConfig.getAccessUrl() + "/" + aliyunOSSConfig.getFolder() + destFileName;
        } catch (Exception e) {
            logger.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        }
        return result;
    }

    /**
     * 通过文件名判断并获取OSS服务文件上传时文件的contentType
     *
     * @param fileName 文件名
     * @return 文件的contentType
     */
    public String getContentType(String fileName) {
        // 文件的后缀名
        if (fileName.contains(".")) {
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            if (".bmp".equalsIgnoreCase(fileExtension)) {
                return "image/bmp";
            }
            if (".gif".equalsIgnoreCase(fileExtension)) {
                return "image/gif";
            }
            if (".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension)
                    || ".png".equalsIgnoreCase(fileExtension)) {
                return "image/jpeg";
            }
            if (".html".equalsIgnoreCase(fileExtension)) {
                return "text/html";
            }
            if (".txt".equalsIgnoreCase(fileExtension)) {
                return "text/plain";
            }
            if (".vsd".equalsIgnoreCase(fileExtension)) {
                return "application/vnd.visio";
            }
            if (".ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
                return "application/vnd.ms-powerpoint";
            }
            if (".doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
                return "application/msword";
            }
            if (".xml".equalsIgnoreCase(fileExtension)) {
                return "text/xml";
            }
        }
        // 默认返回类型
        return "application/octet-stream";
    }

    /**
     * 获得url链接
     *
     * @param key
     * @return
     */
    public String getUrl(String key) {
        // 设置URL过期时间为10年 3600l* 1000*24*365*10

        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(aliyunOSSConfig.getBucketName(), key, expiration);
        if (url != null) {
            return url.toString();
        }
        return null;
    }

    public static abstract class FileNameGenerator {
        public abstract String generateName();

        public String getDestFileName(String localFileName) {
            String destFileName = generateName();
            if (localFileName != null && localFileName.contains(".") && !localFileName.endsWith(".")) {
                destFileName += localFileName.substring(localFileName.lastIndexOf("."));
            }
            return destFileName;
        }
    }

    /**
     * 上传到私有Bucket, 自动生成上传后的目标文件名字
     *
     * @param file
     * @return 文件访问url地址
     * @throws Exception
     */
    public String uploadPrv(File file, String folderName) throws Exception {
        String destFileName = fileNameGenerator.getDestFileName(file.getName());
        return uploadPrvWithDestName(file, destFileName);
    }

    /**
     * 上传到私有Bucket，指定上传后的目标文件名字
     *
     * @param file
     * @param destFileName 上传后的文件名
     * @return 文件访问url地址
     * @throws Exception
     */
    public String uploadPrvWithDestName(File file, String destFileName) throws Exception {
        return uploadPrv(new FileInputStream(file), destFileName);
    }

    /**
     * 上传到私有Bucket, 自动生成上传后的目标文件名字
     *
     * @param inputStream
     * @param localFileName 本地文件名
     * @return 文件访问url地址
     */
    public String uploadPrv(InputStream inputStream, String localFileName) {
        String destFileName = fileNameGenerator.getDestFileName(localFileName);
        return uploadPrvWithDestName(inputStream, destFileName);
    }

    /**
     * 上传到私有Bucket，指定上传后的目标文件名字
     *
     * @param inputStream
     * @param destFileName 上传后的文件名
     * @return 文件访问url地址
     */
    public String uploadPrvWithDestName(InputStream inputStream, String destFileName) {
        return uploadPrvObject2OSS(inputStream, destFileName);
    }

    /**
     * 上传到共有Bucket, 自动生成上传后的目标文件名字
     *
     * @param file
     * @return 文件访问url地址
     * @throws Exception
     */
    public String uploadComm(File file) throws Exception {
        String destFileName = fileNameGenerator.getDestFileName(file.getName());
        return uploadCommWithDestName(file, destFileName);
    }

    /**
     * 上传到共有Bucket，指定上传后的目标文件名字
     *
     * @param file
     * @param destFileName 上传后的文件名
     * @return 文件访问url地址
     * @throws Exception
     */
    public String uploadCommWithDestName(File file, String destFileName) throws Exception {
        return uploadComm(new FileInputStream(file), destFileName);
    }

    /**
     * 上传到公有Bucket, 自动生成上传后的目标文件名字
     *
     * @param inputStream
     * @param localFileName 本地文件名
     * @return 文件访问url地址
     */
    public String uploadComm(InputStream inputStream, String localFileName) {
        String destFileName = fileNameGenerator.getDestFileName(localFileName);
        return uploadCommWithDestName(inputStream, destFileName);
    }

    /**
     * 上传到公有Bucket，指定上传后的目标文件名字
     *
     * @param inputStream
     * @param destFileName 上传后的文件名
     * @return 文件访问url地址
     */
    public String uploadCommWithDestName(InputStream inputStream, String destFileName) {
        return uploadCommObject2OSS(inputStream, destFileName);
    }

    public static void main(String[] args) throws Exception {

//        AliyunOSSClient client = AliyunOSSClient.getInstance();
//        client.config();
//        String destFolder = "image/";
//
//        File file = new File("G:\\device-2017-06-23-235440.png");
//
//        String destUri = AliyunOSSClient.getInstance().uploadComm(file, destFolder);
//        System.out.println(destUri);
    }
}
