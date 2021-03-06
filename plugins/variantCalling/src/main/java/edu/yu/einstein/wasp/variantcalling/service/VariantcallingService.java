/**
 * Created by Wasp System Eclipse Plugin
 * @author 
 */
package edu.yu.einstein.wasp.variantcalling.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.yu.einstein.wasp.model.Job;
import edu.yu.einstein.wasp.model.JobDraft;
import edu.yu.einstein.wasp.plugin.supplemental.organism.Build;
import edu.yu.einstein.wasp.service.WaspService;

/**
 * 
 */
public interface VariantcallingService extends WaspService {
	
		public static final String WXS_NONE_INTERVAL_FILENAME="None";

		/**
		 * Perform Service
		 * @return String
		 */
		public String performAction();
		
		public List<String> getWxsIntervalFilenameFromConfiguration(Build build);
		
		public Set<Build> getBuildsForJobDraft(JobDraft jobDraftId);

		public Map<String, String> getSavedWxsIntervalFilesByBuild(JobDraft jobDraft);
		
		public Map<String, String> getSavedWxsIntervalFilesByBuild(Job job);

		public String getSavedWxsIntervalFileForBuild(JobDraft jobDraft, Build build);
		
		public String getSavedWxsIntervalFileForBuild(Job job, Build build);

		public void saveWxsIntervalFile(JobDraft jobDraft, Build build, String filePath);


}
