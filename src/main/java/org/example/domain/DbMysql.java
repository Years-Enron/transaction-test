package org.example.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "db_mysql")
public class DbMysql {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String f1;

    private String f2;

}
