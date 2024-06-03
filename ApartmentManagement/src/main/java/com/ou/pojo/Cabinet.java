package com.ou.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "cabinet")
public class Cabinet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "cabinetcol", length = 45)
    private String cabinetcol;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Contract_id", nullable = false)
    private Contract contract;

}