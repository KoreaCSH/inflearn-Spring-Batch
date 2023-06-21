package com.springbatch.practice.job.domain.orders;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String orderItem;
    private Integer price;
    private Date orderDate;

    @Builder
    public Orders(String orderItem, Integer price, Date orderDate) {
        this.orderItem = orderItem;
        this.price = price;
        this.orderDate = orderDate;
    }

}
