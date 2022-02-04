package id.btw.payload.input;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.util.Date;

@Getter
@Builder
public class Expense {
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than 0")
    Double amount;

    @NotBlank(message = "Name cannot be null")
    String name;

    @NotBlank(message = "Description cannot be null")
    String description;

    @NotNull(message = "Date cannot be null")
    @PastOrPresent(message = "Date cannot be in the future")
    Date date;
}
