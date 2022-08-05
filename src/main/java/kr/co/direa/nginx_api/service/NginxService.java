package kr.co.direa.nginx_api.service;

import kr.co.direa.nginx_api.vo.MonitoringStatus;
import kr.co.direa.nginx_api.vo.NginxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
public class NginxService {

    @Value("${nginx.service_path}")
    private String nginxPath;

    @Value("${nginx.status_cmd}")
    private String statusCmd;

    @Value("${nginx.conf_path}")
    private String confPath;

    // nginx 기동 커맨드 실행
    public NginxStatus start() {

        String[] cmd = {
                "/bin/sh",
                "-c",
                nginxPath
        };

        execCommand(cmd);

        return NginxStatus.builder()
                .isRun(true)
                .cmd(cmd[2])
                .build();
    }

    // nginx 중지 커맨드 실행
    public NginxStatus stop() {

        String[] cmd = {
                "/bin/sh",
                "-c",
                nginxPath + " -s stop"
        };

        execCommand(cmd);

        return NginxStatus.builder()
                .isRun(true)
                .cmd(cmd[2])
                .build();
    }

    // nginx 재기동 커맨드 실행
    public NginxStatus reload() {

        String[] cmd = {
                "/bin/sh",
                "-c",
                nginxPath + " -s reload"
        };

        execCommand(cmd);

        return NginxStatus.builder()
                .isRun(true)
                .cmd(cmd[2])
                .build();
    }

    // nginx 상태 조회
    public MonitoringStatus status() {

        String[] cmd = {
                "/bin/sh",
                "-c",
                statusCmd
        };

        String rawStatus = getStatus(cmd);

        // STATUS EXAMPLE
//        String rawStatus = "Active connections: 541 \n" +
//                "server accepts handled requests\n" +
//                " 331 331 301 \n" +
//                "Reading: 213 Writing: 4444 Waiting: 22";

        String statusString = rawStatus.replaceAll("[^0-9]", " ").replaceAll("\\s+", " ").trim();
        String[] statusNumbers = statusString.split(" ");

        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        if (statusNumbers.length != 6) {
            return MonitoringStatus.builder()
                    .isOk(false)
                    .date(date)
                    .time(time)
                    .build();
        }

        return MonitoringStatus.builder()
                .isOk(true)
                .date(date)
                .time(time)
                .activeConnections(Long.parseLong(statusNumbers[0]))
                .accepts(Long.parseLong(statusNumbers[1]))
                .handled(Long.parseLong(statusNumbers[2]))
                .requests(Long.parseLong(statusNumbers[3]))
                .reading(Long.parseLong(statusNumbers[4]))
                .writing(Long.parseLong(statusNumbers[5]))
                .waiting(Long.parseLong(statusNumbers[6]))
                .build();
    }

    public void execCommand(String[] cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            process.exitValue();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getStatus(String[] cmd) {
        StringBuilder sb = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String readString;
            while ((readString = br.readLine()) != null) {
                sb.append(readString);
            }

            process.waitFor();
            process.exitValue();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    // config file 가져오기
    public String getConfigFile() {
        StringBuilder sb = new StringBuilder();

        try {
            File file = new File(confPath);
//            File file = new File("C:\\Users\\MSI\\Desktop\\nginx\\nginx_config.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(fileReader);

            String line = "";
            while ((line = bufReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            bufReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public boolean reviseConfigFile(String overwrittenConfig) {
        FileWriter fw = null;

        try {
            File file = new File(confPath);
//            File file = new File("C:\\Users\\MSI\\Desktop\\nginx\\nginx_config.txt");
            fw = new FileWriter(file, false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(overwrittenConfig);
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void main(String[] args) {

        NginxService nginxService = new NginxService();
        MonitoringStatus status = nginxService.status();}
}
