package com.ou.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "detail_evoluation")
public class DetailEvoluation implements Serializable {
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Criterion_id", nullable = false)
    private Criterion criterion;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Evaluation_id", nullable = false)
    private Evaluation evaluation;

    @Lob
    @Column(name = "Rate")
    private String rate;

}