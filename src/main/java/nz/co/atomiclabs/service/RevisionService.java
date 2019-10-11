package nz.co.atomiclabs.service;

import nz.co.atomiclabs.service.mapper.EntityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionSort;
import org.springframework.data.history.Revisions;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
public abstract class RevisionService<T, D, ID, N extends Number & Comparable<N>> {
    private final Logger log = LoggerFactory.getLogger(RevisionService.class);

    private final RevisionRepository<T, ID, N> revisionRepository;
    private final EntityMapper<D, T> mapper;

    public RevisionService(RevisionRepository<T, ID, N> revisionRepository, EntityMapper<D, T> mapper) {
        this.revisionRepository = revisionRepository;
        this.mapper = mapper;
    }

    /**
     * Returns the revision of the entity it was last changed in.
     *
     * @param id must not be {@literal null}.
     * @return
     */
    @Transactional(readOnly = true)
    public Optional<Revision<N, D>> findLastChangeRevision(ID id) {
        log.debug("Request to get latest revision : {}", id);
        Optional<Revision<N, T>> revision = revisionRepository.findLastChangeRevision(id);
        return revision.map(this::map);
    }

    /**
     * Returns all {@link Revisions} of an entity with the given id.
     *
     * @param id must not be {@literal null}.
     * @return
     */
    @Transactional(readOnly = true)
    public Revisions<N, D> findRevisions(ID id) {
        log.debug("Request to get all revisions : {}", id);
        Revisions<N, T> revisions = revisionRepository.findRevisions(id);
        return map(revisions);
    }

    /**
     * Returns a {@link Page} of revisions for the entity with the given id. Note, that it's not guaranteed that
     * implementations have to support sorting by all properties.
     *
     * @param id must not be {@literal null}.
     * @param pageable
     * @see RevisionSort
     * @return
     */
    @Transactional(readOnly = true)
    public Page<Revision<N, D>> findRevisions(ID id, Pageable pageable) {
        log.debug("Request to get paged revisions : {} {}", id, pageable);
        Page<Revision<N, T>> revisions = revisionRepository.findRevisions(id, pageable);
        return revisions.map(this::map);
    }

    /**
     * Returns the entity with the given ID in the given revision number.
     *
     * @param id must not be {@literal null}.
     * @param revisionNumber must not be {@literal null}.
     * @return the {@link Revision} of the entity with the given ID in the given revision number.
     * @since 1.12
     */
    @Transactional(readOnly = true)
    public Optional<Revision<N, D>> findRevision(ID id, N revisionNumber) {
        log.debug("Request to get revision by id and revision : {} {}", id, revisionNumber);
        Optional<Revision<N, T>> revision = revisionRepository.findRevision(id, revisionNumber);
        return revision.map(r -> Revision.of(r.getMetadata(), mapper.toDto(r.getEntity())));
    }

    private Revisions<N, D> map(Revisions<N, T> revisions) {
        return Revisions.of(revisions.stream().map(this::map).collect(Collectors.toList()));
    }

    private Revision<N, D> map(Revision<N, T> revision) {
        return Revision.of(revision.getMetadata(), mapper.toDto(revision.getEntity()));
    }
}
