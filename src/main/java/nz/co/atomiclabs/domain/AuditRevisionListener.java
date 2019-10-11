package nz.co.atomiclabs.domain;

import nz.co.atomiclabs.config.Constants;
import nz.co.atomiclabs.security.SecurityUtils;
import org.hibernate.envers.RevisionListener;

public class AuditRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        AuditRevision rev = (AuditRevision) revisionEntity;
        rev.setUserName(SecurityUtils.getCurrentUserLogin().orElse(Constants.SYSTEM_ACCOUNT));
    }
}
