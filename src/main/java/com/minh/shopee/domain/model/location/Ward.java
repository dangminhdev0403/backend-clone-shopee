package com.minh.shopee.domain.model.location;

import java.util.Objects;

import com.minh.shopee.domain.base.BaseLocation;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "wards")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Ward extends BaseLocation {

    @ManyToOne
    District district;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Ward))
            return false;
        Ward ward = (Ward) o;
        return Objects.equals(getId(), ward.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
