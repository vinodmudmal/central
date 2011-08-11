
/**
 *
 * RunFileImpl.java 
 * @author echeng (table2type.pl)
 *  
 * the RunFile object
 *
 *
 **/

package edu.yu.einstein.wasp.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.model.RunFile;

@SuppressWarnings("unchecked")
@Transactional
@Repository
public class RunFileDaoImpl extends WaspDaoImpl<RunFile> implements edu.yu.einstein.wasp.dao.RunFileDao {

  public RunFileDaoImpl() {
    super();
    this.entityClass = RunFile.class;
  }

  @SuppressWarnings("unchecked")
  @Transactional
  public RunFile getRunFileByRunlanefileId (final int runlanefileId) {
    HashMap m = new HashMap();
    m.put("runlanefileId", runlanefileId);
    List<RunFile> results = (List<RunFile>) this.findByMap((Map) m);
    if (results.size() == 0) {
      RunFile rt = new RunFile();
      return rt;
    }
    return (RunFile) results.get(0);
  }


  @SuppressWarnings("unchecked")
  @Transactional
  public RunFile getRunFileByFileId (final int fileId) {
    HashMap m = new HashMap();
    m.put("fileId", fileId);
    List<RunFile> results = (List<RunFile>) this.findByMap((Map) m);
    if (results.size() == 0) {
      RunFile rt = new RunFile();
      return rt;
    }
    return (RunFile) results.get(0);
  }


}

