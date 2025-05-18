package com.minh.shopee.domain.model.location;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minh.shopee.domain.base.BaseLocation;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "provinces")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Province extends BaseLocation {

    @OneToMany(mappedBy = "province")
    @JsonIgnore
    List<District> districts;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Province))
            return false;
        Province province = (Province) o;
        return Objects.equals(getId(), province.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
