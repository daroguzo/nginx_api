package kr.co.direa.nginx_api.vo;


import lombok.Builder;
import lombok.Data;

@Data @Builder
public class MonitoringStatus {

    private boolean isOk;

    private String date;

    private String time;

    private long activeConnections;

    private long accepts;

    private long handled;

    private long requests;

    private long reading;

    private long writing;

    private long waiting;

}
