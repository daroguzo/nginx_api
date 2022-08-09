package kr.co.direa.nginx_api.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data @Builder
public class NodeInfo {
    boolean isRun;

    String date;

    String time;

    int size;

    Set<OpenPort> openPortList;
}
