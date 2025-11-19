package domain;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UmsAdminRoleRelation implements Serializable {
    private Long id;

    private Long adminId;

    private Long roleId;

    @Serial
    private static final long serialVersionUID = 1L;
}