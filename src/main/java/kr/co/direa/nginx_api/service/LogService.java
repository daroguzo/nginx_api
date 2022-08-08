package kr.co.direa.nginx_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class LogService {

    @Value("${nginx.log.path.status}")
    private String logPath;

    // json 형태의 nginx status 파일 쓰기
    public void writeLog(String status) {

        try {
            File file = new File(logPath);
//            File file = new File("C:\\Users\\MSI\\Desktop\\nginx\\logs\\nginx_status.log");
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(status);
            bw.newLine();
            bw.flush();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
