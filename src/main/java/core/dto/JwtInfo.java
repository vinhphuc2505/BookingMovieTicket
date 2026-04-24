package core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtInfo implements Serializable {
    private String jwtId;

    private Date issueTime;

    private Date expiredTime;

    private String jwtIdRefreshToken;
}
