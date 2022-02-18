package id.btw.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class Expense {
    private Integer id;

    private String name;

    private Integer amount;

    private String description;

    private LocalDate date;
}
