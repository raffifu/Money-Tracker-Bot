package id.my.btw.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class Expense {
    private Integer id;

    private Integer amount;

    private String note;

    private String category;

    private String account;

    private LocalDate date;
}
