package github.com.jbabe.repository.division;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Table(name = "division_enum")
@NoArgsConstructor
public class DivisionEnum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "division_enum_id")
    private long divisionEnumId;

    @Column(name = "division_value", nullable = false)
    private String divisionName;

    @OneToMany(mappedBy = "divisionEnum", fetch = FetchType.LAZY)
    private List<Division> divisions;

    public DivisionEnum(String divisionName) {
        this.divisionName = divisionName;
    }


}
