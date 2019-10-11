package nz.co.atomiclabs.repository;

import nz.co.atomiclabs.domain.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Node entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NodeRepository extends JpaRepository<Node, Long>, JpaSpecificationExecutor<Node>, RevisionRepository<Node, Long, Long> {

}
