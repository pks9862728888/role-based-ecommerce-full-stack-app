package com.demo.rbac.ecommerce.persistence.entitites.user;

import com.demo.rbac.ecommerce.persistence.entitites.Audit;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
public class UserDetails extends Audit {

    @Id
    private Long userId;
    private String firstName;
    private String lastName;
    @MapsId
    @OneToOne
    private UserAccount userAccount;
}
