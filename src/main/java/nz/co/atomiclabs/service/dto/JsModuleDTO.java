package nz.co.atomiclabs.service.dto;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link nz.co.atomiclabs.domain.JsModule} entity.
 */
public class JsModuleDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String name;

    @NotNull
    private String source;

    @NotNull
    private Boolean autoLoad;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean isAutoLoad() {
        return autoLoad;
    }

    public void setAutoLoad(Boolean autoLoad) {
        this.autoLoad = autoLoad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JsModuleDTO jsModuleDTO = (JsModuleDTO) o;
        if (jsModuleDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), jsModuleDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "JsModuleDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", source='" + getSource() + "'" +
            ", autoLoad='" + isAutoLoad() + "'" +
            "}";
    }
}
