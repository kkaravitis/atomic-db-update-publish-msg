package com.wordpress.kkaravitis.kafka.poc.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class Book {
    @Id
    private Long id;

    @Column
    private String title;
}
