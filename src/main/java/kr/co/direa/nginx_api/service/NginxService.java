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

    private final LogService logService;

    @Value("${nginx.path.service}")
    private String nginxPath;

    @Value("${nginx.cmd.status}")
    private String statusCmd;

    @Value("${nginx.path.conf}")
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

        String rawStatus = getStatusCmdToString(cmd);

        // STATUS EXAMPLE
//        String rawStatus = "Active connections: 541 \n" +
//                "server accepts handled requests\n" +
//                " 331 331 301 \n" +
//                "Reading: 213 Writing: 4444 Waiting: 22";

        String statusString = rawStatus.replaceAll("[^0-9]", " ").replaceAll("\\s+", " ").trim();
        String[] statusNumbers = statusString.split(" ");

        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        if (statusNumbers.length != 7) {
            return MonitoringStatus.builder()
                    .isOk(false)
                    .date(date)
                    .time(time)
                    .build();
        }

        MonitoringStatus result = MonitoringStatus.builder()
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

        // 로그 파일로 쓰기
        logService.writeLog(result.toString());
        return result;
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

    // nginx status를 호출하고 그 결과를 String으로 반한
    public String getStatusCmdToString(String[] cmd) {
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
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    // nginx config 파일 덮어쓰기
    public boolean reviseConfigFile(String overwrittenConfig) {
        try {
            File file = new File(confPath);
//            File file = new File("C:\\Users\\MSI\\Desktop\\nginx\\nginx_config.txt");
            FileWriter fw = new FileWriter(file, false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(overwrittenConfig);
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
