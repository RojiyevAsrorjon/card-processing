package uz.demo.card_procession_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("CardResponse")
public class CardResponse implements Serializable {

    private UUID id;
    private Long userId;
    private String status;
    private BigDecimal balance;
    private String currency;
}
