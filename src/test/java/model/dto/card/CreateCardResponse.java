package model.dto.card;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCardResponse {
    private String cardHolder;
    private String cardNumber;
    private String expiredDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateCardResponse that = (CreateCardResponse) o;
        return Objects.equals(cardHolder, that.cardHolder) && Objects.equals(cardNumber, that.cardNumber) && Objects.equals(expiredDate, that.expiredDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardHolder, cardNumber, expiredDate);
    }
}
