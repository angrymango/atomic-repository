package nz.co.atomiclabs.repository;
import nz.co.atomiclabs.domain.JsModule;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the JsModule entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JsModuleRepository extends JpaRepository<JsModule, Long>, JpaSpecificationExecutor<JsModule> {

}
