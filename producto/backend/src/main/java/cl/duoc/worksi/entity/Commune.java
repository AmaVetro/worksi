package cl.duoc.worksi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "communes")
public class Commune {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "region_id", nullable = false)
    private Long regionId;

    public Long getId() {
        return id;
    }

    public Long getRegionId() {
        return regionId;
    }
}
