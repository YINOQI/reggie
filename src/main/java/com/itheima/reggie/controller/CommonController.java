package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;


/**
 * 文件上传和下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String path;


    /**
     * 上传图片
     * @param file
     * @return
     */
    // 参数名必须为file，并且file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //原始文件名
        String originalFilename = file.getOriginalFilename();

        //获取文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，避免文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString();

        //判断目录是否存在
        File dir = new File(path);
        if (!dir.exists()) {
            //目录不存在，新建目录
            dir.mkdir();
        }

        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(path + fileName + suffix));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return R.success(fileName + suffix);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {

        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(path + name);

            //输出流，通过输出流将文件写回浏览器，在浏览器显示图片
            ServletOutputStream outputStream = response.getOutputStream();

            //设置文件格式
            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
