package kr.co.direa.nginx_api.vo;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class OpenPort {

    String ip;

    int port;
}
