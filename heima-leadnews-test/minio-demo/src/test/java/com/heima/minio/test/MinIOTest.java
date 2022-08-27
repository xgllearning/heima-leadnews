package com.heima.minio.test;

import com.heima.file.service.FileStorageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MinIOTest {

    /**
     * 把模板文件上传到minio中
     * @param args
     */
    public static void main(String[] args) {

        FileInputStream fileInputStream = null;
        try {
            //读取本地文件
            fileInputStream =  new FileInputStream("D:\\02-list.html");;

            //1.获取minio的链接信息，创建minio客户端
            MinioClient minioClient = MinioClient.builder().credentials("minio", "minio123")
                    .endpoint("http://192.168.200.130:9000").build();
            //2.上传
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object("newList.html")//文件名
                    .contentType("text/html")//文件类型
                    .bucket("leadnews")//桶名词  与minio创建的名词一致
                    .stream(fileInputStream, fileInputStream.available(), -1) //文件流
                    .build();
            minioClient.putObject(putObjectArgs);

            System.out.println("http://192.168.200.130:9000/leadnews/newList.html");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



}