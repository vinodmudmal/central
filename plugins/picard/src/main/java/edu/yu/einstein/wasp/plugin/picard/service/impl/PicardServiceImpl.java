/**
 * Created by Wasp System Eclipse Plugin
 * @author 
 */
package edu.yu.einstein.wasp.plugin.picard.service.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonParser;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.exception.MetadataException;
import edu.yu.einstein.wasp.grid.GridHostResolver;
import edu.yu.einstein.wasp.grid.work.GridResult;
import edu.yu.einstein.wasp.grid.work.GridTransportConnection;
import edu.yu.einstein.wasp.grid.work.GridWorkService;
import edu.yu.einstein.wasp.grid.work.WorkUnit;
import edu.yu.einstein.wasp.grid.work.WorkUnit.ProcessMode;
import edu.yu.einstein.wasp.model.FileGroup;
import edu.yu.einstein.wasp.model.FileGroupMeta;
import edu.yu.einstein.wasp.model.SampleSource;
import edu.yu.einstein.wasp.model.SampleSourceMeta;
import edu.yu.einstein.wasp.plugin.picard.service.PicardService;

import edu.yu.einstein.wasp.service.FileService;
import edu.yu.einstein.wasp.service.SampleService;
import edu.yu.einstein.wasp.service.impl.WaspServiceImpl;

@Service
@Transactional("entityManager")
public class PicardServiceImpl extends WaspServiceImpl implements PicardService {
	

	@Autowired
	SampleService sampleService;
	@Autowired
	FileService fileService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String performAction() {
		// do something
		return "done";
	}

	
	public boolean alignmentMetricsExist(FileGroup fileGroup){
		JSONObject jsonObj = getAlignmentMetricsMetaAsJSON(fileGroup);
		if(jsonObj != null){
			return true;
		}
		return false;
	}
	private String getAlignmentMetric(FileGroup fileGroup, String jsonKey){
		String value = "";
		JSONObject json = getAlignmentMetricsMetaAsJSON(fileGroup);
		if(json.has(jsonKey)){
			value = json.getString(jsonKey);
		}
		return value;
	}
	private JSONObject getAlignmentMetricsMetaAsJSON(FileGroup fileGroup){
		
		JSONObject jsonObj = null;
		String meta = "";
		for(FileGroupMeta fgm : fileGroup.getFileGroupMeta()){
			if(fgm.getK().equalsIgnoreCase(BAMFILE_ALIGNMENT_METRICS_META_KEY)){
				meta = fgm.getV();
			}
		}
		if(!meta.isEmpty()){
			jsonObj = new JSONObject(meta);
		}
		return jsonObj;
	}
	
	public String getUnpairedMappedReads(FileGroup fileGroup){		
		return this.getAlignmentMetric(fileGroup, BAMFILE_ALIGNMENT_METRIC_UNPAIRED_READS);
	}
	public String getPairedMappedReads(FileGroup fileGroup){		
		return this.getAlignmentMetric(fileGroup, BAMFILE_ALIGNMENT_METRIC_PAIRED_READS);
	}
	public String getUnmappedReads(FileGroup fileGroup){		
		return this.getAlignmentMetric(fileGroup, BAMFILE_ALIGNMENT_METRIC_UNMAPPED_READS);
	}
	public String getUnpairedMappedReadDuplicates(FileGroup fileGroup){		
		return this.getAlignmentMetric(fileGroup, BAMFILE_ALIGNMENT_METRIC_UNPAIRED_READ_DUPLICATES);
	}
	public String getPairedMappedReadDuplicates(FileGroup fileGroup){		
		return this.getAlignmentMetric(fileGroup, BAMFILE_ALIGNMENT_METRIC_PAIRED_READ_DUPLICATES);
	}
	public String getPairedMappedReadOpticalDuplicates(FileGroup fileGroup){		
		return this.getAlignmentMetric(fileGroup, BAMFILE_ALIGNMENT_METRIC_PAIRED_READ_OPTICAL_DUPLICATES);
	}
	
	public String getFractionMapped(FileGroup fileGroup){		
		return this.getAlignmentMetric(fileGroup, BAMFILE_ALIGNMENT_METRIC_FRACTION_MAPPED);
	}
	public String getMappedReads(FileGroup fileGroup){		
		return this.getAlignmentMetric(fileGroup, BAMFILE_ALIGNMENT_METRIC_MAPPED_READS);
	}
	public String getTotalReads(FileGroup fileGroup){		
		return this.getAlignmentMetric(fileGroup, BAMFILE_ALIGNMENT_METRIC_TOTAL_READS);
	}	
	public String getFractionMappedAsCalculation(FileGroup fileGroup){		
		String fractionMapped = this.getFractionMapped(fileGroup);
		String mappedReads = this.getMappedReads(fileGroup);
		String totalReads = this.getTotalReads(fileGroup);		
		return mappedReads + " / " + totalReads + " = " + fractionMapped;
	}
	
	public String getFractionDuplicated(FileGroup fileGroup){		
		return this.getAlignmentMetric(fileGroup, BAMFILE_ALIGNMENT_METRIC_FRACTION_DUPLICATED);//fraction of mapped reads
	}
	public String getDuplicateReads(FileGroup fileGroup){		
		return this.getAlignmentMetric(fileGroup, BAMFILE_ALIGNMENT_METRIC_DUPLICATE_READS);
	}
	public String getFractionDuplicatedAsCalculation(FileGroup fileGroup){	
		String fractionDuplicated = getFractionDuplicated(fileGroup);
		String duplicateReads = getDuplicateReads(fileGroup);
		String mappedReads = this.getMappedReads(fileGroup);		
		return duplicateReads + " / " + mappedReads + " = " + fractionDuplicated;	}
	
	public String getFractionUniqueNonRedundant(FileGroup fileGroup){	
		return this.getAlignmentMetric(fileGroup, BAMFILE_ALIGNMENT_METRIC_FRACTION_UNIQUE_NONREDUNDANT);
	}
	public String getUniqueReads(FileGroup fileGroup){	
		return this.getAlignmentMetric(fileGroup, BAMFILE_ALIGNMENT_METRIC_UNIQUE_READS);
	}
	public String getUniqueNonRedundantReads(FileGroup fileGroup){	
		return this.getAlignmentMetric(fileGroup, BAMFILE_ALIGNMENT_METRIC_UNIQUE_NONREDUNDANT_READS);
	}
	public String getFractionUniqueNonRedundantAsCalculation(FileGroup fileGroup){	
		String fractionUniqueNonRedundant = getFractionUniqueNonRedundant(fileGroup);
		String uniqueReads = getUniqueReads(fileGroup);
		String uniqueNonRedundantReads = getUniqueNonRedundantReads(fileGroup);		
		return uniqueNonRedundantReads + " / " + uniqueReads + " = " + fractionUniqueNonRedundant;
	}
}
