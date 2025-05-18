package com.minh.shopee.domain.model.location;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minh.shopee.domain.base.BaseLocation;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "districts")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class District extends BaseLocation {

    @ManyToOne
    Province province;

    @OneToMany(mappedBy = "district")
    @JsonIgnore
    List<Ward> wards;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof District))
            return false;
        District district = (District) o;
        return Objects.equals(getId(), district.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
