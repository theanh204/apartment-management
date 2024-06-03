package com.ou.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "family_member")
public class FamilyMember {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "active", nullable = false, length = 1)
    private String active;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Resident_User_id", nullable = false)
    private Resident residentUser;

}