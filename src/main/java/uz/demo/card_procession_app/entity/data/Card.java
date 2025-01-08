package uz.demo.card_procession_app.entity.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity(name = "cards")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Card implements Serializable {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String type;
    private Long userId;
    private String number;
    private String expirationDate;
    private BigDecimal balance;
    private String currency;
    private String cvv;
    private String status;
}
