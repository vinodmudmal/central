/**
 * Created by Wasp System Eclipse Plugin
 * @author
 */
package edu.yu.einstein.wasp.macstwo.batch.tasklet;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import edu.yu.einstein.wasp.Assert;
import edu.yu.einstein.wasp.daemon.batch.tasklets.WaspRemotingTasklet;
import edu.yu.einstein.wasp.grid.GridHostResolver;
import edu.yu.einstein.wasp.grid.work.GridResult;
import edu.yu.einstein.wasp.grid.work.WorkUnit;
import edu.yu.einstein.wasp.macstwo.integration.messages.MacstwoSoftwareJobParameters;
import edu.yu.einstein.wasp.macstwo.software.Macstwo;
import edu.yu.einstein.wasp.model.FileGroup;
import edu.yu.einstein.wasp.model.FileGroupMeta;
import edu.yu.einstein.wasp.model.FileHandle;
import edu.yu.einstein.wasp.model.FileType;
import edu.yu.einstein.wasp.model.Job;
import edu.yu.einstein.wasp.model.Sample;
import edu.yu.einstein.wasp.plugin.mps.grid.software.Imagemagick;
import edu.yu.einstein.wasp.plugin.mps.grid.software.R;
import edu.yu.einstein.wasp.service.FileService;
import edu.yu.einstein.wasp.service.JobService;
import edu.yu.einstein.wasp.service.SampleService;

/**
 * @author 
 * 
 */
public class MacstwoGenerateModelAsPdfTasklet extends WaspRemotingTasklet implements StepExecutionListener {

	private Integer jobId;
	private Integer modelScriptGId;//passed in
	private Integer testSampleId;//passed in
	private Integer controlSampleId;//passed in (could be zero - which indicates no control was used)
	private Integer modelPdfGId;//generated in doExecute()
	private Integer modelPngGId;//generated in doExecute()
	private String commandLineCall;
	
	private StepExecution stepExecution;
	
	@Autowired
	private FileType macs2ModelPdfFileType;
	@Autowired
	private FileType macs2ModelPngFileType;
	@Autowired
	private FileType macs2ModelScriptFileType;
	
	@Autowired
	private JobService jobService;
	
	@Autowired
	private FileType bamFileType;

	@Autowired
	private SampleService sampleService;
	@Autowired
	private FileService fileService;

	@Autowired
	private GridHostResolver gridHostResolver;
	
	@Autowired
	private Macstwo macs2;

	public MacstwoGenerateModelAsPdfTasklet() {
		// proxy
	}

	/**
	 * 
	 * @param contrib
	 * @param context
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional("entityManager")
	public void doExecute(ChunkContext context) throws Exception {
		
		Imagemagick imagemagickSoftware = (Imagemagick) macs2.getSoftwareDependencyByIname("imagemagick"); 
		R rSoftware = (R) macs2.getSoftwareDependencyByIname("rPackage");

		logger.debug("*************************************");
		logger.debug("Starting MacstwoGenerateModelAsPdfTasklet execute");
		
		Map<String,Object> jobParametersMap = context.getStepContext().getJobParameters();		
		for (String key : jobParametersMap.keySet()) {
			if(key.equalsIgnoreCase(MacstwoSoftwareJobParameters.JOB_ID)){
				this.jobId = Integer.parseInt(jobParametersMap.get(key).toString());
			}
			logger.debug("jobParametersMap Key: " + key + " Value: " + jobParametersMap.get(key).toString());
		}
		Assert.assertTrue(this.jobId.intValue() > 0);
		Job job = jobService.getJobByJobId(jobId);
		logger.debug("in doExecute of MacstwoGenerateModelAsPdfTasklet: this.jobId (integer): " + this.jobId.toString());
				
		Map<String,Object> jobExecutionContextMap = context.getStepContext().getJobExecutionContext();		
		for (String key : jobExecutionContextMap.keySet()) {
			if(key.equalsIgnoreCase(MacstwoSoftwareJobParameters.MODEL_SCRIPT_FILEGROUP_ID)){
				this.modelScriptGId = Integer.parseInt(jobExecutionContextMap.get(key).toString());
			}
			else if(key.equalsIgnoreCase(MacstwoSoftwareJobParameters.TEST_SAMPLE_ID)){
				this.testSampleId = Integer.parseInt(jobExecutionContextMap.get(key).toString());
			}
			else if(key.equalsIgnoreCase(MacstwoSoftwareJobParameters.CONTROL_SAMPLE_ID)){
				this.controlSampleId = Integer.parseInt(jobExecutionContextMap.get(key).toString());//could be zero
			}
			logger.debug("jobExecutionContextMap Key: " + key + " Value: " + jobExecutionContextMap.get(key).toString());
		}
		Assert.assertTrue(this.modelScriptGId.intValue() > 0);
		logger.debug("this.modelScriptGId (integer): " + this.modelScriptGId.toString());		
		Assert.assertTrue(this.testSampleId.intValue() > 0);
		logger.debug("this.testSampleId (integer): " + this.testSampleId.toString());			
		Assert.assertTrue(this.controlSampleId >= 0);//controlSampleId can be zero (basically indicating there was no control)
		logger.debug("this.controlSampleId (integer): " + this.controlSampleId.toString());
		
		FileGroup modelScriptFileGroup = fileService.getFileGroupById(this.modelScriptGId);
		logger.debug("modelScriptFileGroup.description: " + modelScriptFileGroup.getDescription());
		Set<FileHandle> fileHandleSet = modelScriptFileGroup.getFileHandles();
		logger.debug("fileHandleSet.size: " + fileHandleSet.size());
		Assert.assertTrue(fileHandleSet.size()==1);
		for(FileHandle fh : fileHandleSet){
			logger.debug("filehandle.name = " + fh.getFileName());
		}
		FileHandle modelScriptFileHandle = new ArrayList<FileHandle>(fileHandleSet).get(0);
		logger.debug("*****modelScriptFileHandle.name = " + modelScriptFileHandle.getFileName());		
		logger.debug("*****modelScriptFileHandle.getFileType().getName() = " + modelScriptFileHandle.getFileType().getName());
		logger.debug("*****macs2ModelScriptFileType.getName() = " + macs2ModelScriptFileType.getName());
		logger.debug("*****macs2ModelScriptFileType.getIName() = " + macs2ModelScriptFileType.getIName());		
		Assert.assertTrue(modelScriptFileHandle.getFileType().getIName().equalsIgnoreCase(macs2ModelScriptFileType.getIName()));
		String pdfFileName = modelScriptFileHandle.getFileName().replaceAll(".r$", ".pdf");//abc_model.r will be used to generate abc_model.pdf
		String pngFileName = modelScriptFileHandle.getFileName().replaceAll(".r$", ".png");//abc_model.pdf will be used to generate abc_model.png

		logger.debug("*****pdfFileName = " + pdfFileName);
		logger.debug("*****pngFileName = " + pngFileName);
		
		logger.debug("preparing to generate workunit in MacstwoGenerateModelAsPdfTasklet.doExecute()");
		WorkUnit w = macs2.getModelPdf(modelScriptFileHandle, pdfFileName, pngFileName);//configure
		logger.debug("OK, workunit has been generated in MacstwoGenerateModelAsPdfTasklet.doExecute()");
		this.commandLineCall = w.getCommand();//not actually being used here
		Assert.assertTrue(!this.commandLineCall.isEmpty());
		logger.debug("commandLineCall in MacstwoGenerateModelAsPdfTasklet.doExecute() is : " + commandLineCall);
			
		List<String> listOfFileHandleNames = new ArrayList<String>();
		
		Set<FileHandle> files = new LinkedHashSet<FileHandle>();

		//the pdf (generated from running Rscript on xx_model.r file)
		FileGroup modelPdfG = new FileGroup();
		FileHandle modelPdf = new FileHandle();
		modelPdf.setFileName(pdfFileName);
		listOfFileHandleNames.add(pdfFileName);
		modelPdf.setFileType(macs2ModelPdfFileType);
		modelPdf = fileService.addFile(modelPdf);
		modelPdfG.addFileHandle(modelPdf);
		files.add(modelPdf);
		modelPdfG.setFileType(macs2ModelPdfFileType);
		modelPdfG.setDescription(modelPdf.getFileName());
		modelPdfG.setSoftwareGeneratedBy(rSoftware);
		modelPdfG.addDerivedFrom(modelScriptFileGroup);
		modelPdfG = fileService.addFileGroup(modelPdfG);
		this.modelPdfGId = modelPdfG.getId();
		logger.debug("recorded fileGroup and fileHandle for rscript to create pdf in MacstwoGenerateModelAsPdfTasklet.doExecute()");

		//the png (converted from the pdf using ImageMagick)
		FileGroup modelPngG = new FileGroup();
		FileHandle modelPng = new FileHandle();
		modelPng.setFileName(pngFileName);
		listOfFileHandleNames.add(pngFileName);
		modelPng.setFileType(macs2ModelPngFileType);
		modelPng = fileService.addFile(modelPng);
		modelPngG.addFileHandle(modelPng);
		files.add(modelPng);
		modelPngG.setFileType(macs2ModelPngFileType);
		modelPngG.setDescription(modelPng.getFileName());
		modelPngG.setSoftwareGeneratedBy(imagemagickSoftware);
		modelPngG.addDerivedFrom(modelPdfG);
		modelPngG = fileService.addFileGroup(modelPngG);
		this.modelPngGId = modelPngG.getId();
		logger.debug("recorded fileGroup and fileHandle for ImageMagick to create png in MacstwoGenerateModelAsPdfTasklet.doExecute()");

		
		
		ExecutionContext stepContext = this.stepExecution.getExecutionContext();
		//in case of crash
		stepContext.put("testSampleId", this.testSampleId);
		stepContext.put("controlSampleId", this.controlSampleId);
		stepContext.put("commandLineCall", this.commandLineCall);
		stepContext.put("modelPdfGId", this.modelPdfGId);
		stepContext.put("modelPngGId", this.modelPngGId);
		logger.debug("saved 5 variables in stepContext within MacstwoGenerateModelAsPdfTasklet.doExecute()");
		
		w.setResultFiles(files);
		logger.debug("executed w.getResultFiles().add(modelPdfG) within MacstwoGenerateModelAsPdfTasklet.doExecute()");
		logger.debug("executed w.getResultFiles().add(modelPngG) within MacstwoGenerateModelAsPdfTasklet.doExecute()");
		
		w.setResultsDirectory(fileService.generateJobSoftwareBaseFolderName(job, macs2)); 
		
		int counter = 0;
		for(String fileName : listOfFileHandleNames){//need to make this symbolic link in order to properly copy files
			w.addCommand("ln -s " + fileName + " ${" + WorkUnit.OUTPUT_FILE+"["+counter+"]}");
			logger.debug("add command: " + "ln -s " + fileName + " ${" + WorkUnit.OUTPUT_FILE+"["+counter+"]}");
			counter++;
		}
		
		logger.debug("executed w.setResultsDirectory(Workunit.....) within MacstwoGenerateModelAsPdfTasklet.doExecute()");
		
		GridResult result = gridHostResolver.execute(w);
		logger.debug("****Executed gridHostResolver.execute(w) in MacstwoGenerateModelAsPdfTasklet.doExecute()");		
		storeStartedResult(context, result);//place the grid result in the step context

	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		return super.afterStep(stepExecution);
	}

	/** 
	 * {@inheritDoc}
	 */
	@Override
	public void beforeStep(StepExecution stepExecution) {
		super.beforeStep(stepExecution);
		logger.debug("StepExecutionListener beforeStep saving StepExecution in MacstwoGenerateModelAsPdfTasklet");
		this.stepExecution = stepExecution;				
		//JobExecution jobExecution = stepExecution.getJobExecution();
		//ExecutionContext jobContext = jobExecution.getExecutionContext();
		//this.scratchDirectory = jobContext.get("scrDir").toString();
		ExecutionContext stepContext = this.stepExecution.getExecutionContext();
		//in case of crash
		this.testSampleId = (Integer) stepContext.get("testSampleId");
		this.controlSampleId = (Integer) stepContext.get("controlSampleId");
		this.commandLineCall = (String) stepContext.get("commandLineCall");
		this.modelPdfGId = (Integer) stepContext.get("modelPdfGId");
		this.modelPngGId = (Integer) stepContext.get("modelPngGId");
	}
	
	@Override
	@Transactional("entityManager")
	public void doPreFinish(ChunkContext context) throws Exception {
		
		logger.debug("starting doPreFinish  in MacstwoGenerate<ModelAsPdfTasklet");
		
		//at Andy's suggestion, do this here as well
		ExecutionContext stepContext = this.stepExecution.getExecutionContext();
		this.testSampleId = (Integer) stepContext.get("testSampleId");
		this.controlSampleId = (Integer) stepContext.get("controlSampleId");		
		this.commandLineCall = (String) stepContext.get("commandLineCall");
		this.modelPdfGId = (Integer) stepContext.get("modelPdfGId");
		this.modelPngGId = (Integer) stepContext.get("modelPngGId");

		Sample testSample = sampleService.getSampleById(this.testSampleId);
				
		if (this.modelPdfGId != null && testSample.getId() != 0){
			////fileService.setSampleFile(fileService.getFileGroupById(modelPdfGId), testSample);
			FileGroup fg = fileService.getFileGroupById(this.modelPdfGId);
			fileService.setSampleFile(fg, testSample);
			FileGroupMeta fgm = new FileGroupMeta();
			fgm.setK("chipseqAnalysis.controlId");
			fgm.setV(this.controlSampleId.toString());//could be zero
			fgm.setFileGroupId(fg.getId());
			List<FileGroupMeta> fgmList = new ArrayList<FileGroupMeta>();
			fgmList.add(fgm);
			fileService.saveFileGroupMeta(fgmList, fg);
		}
		
		if (this.modelPngGId != null && testSample.getId() != 0){
			////fileService.setSampleFile(fileService.getFileGroupById(modelPdfGId), testSample);
			FileGroup fg = fileService.getFileGroupById(this.modelPngGId);
			fileService.setSampleFile(fg, testSample);
			FileGroupMeta fgm = new FileGroupMeta();
			fgm.setK("chipseqAnalysis.controlId");
			fgm.setV(this.controlSampleId.toString());//could be zero
			fgm.setFileGroupId(fg.getId());
			List<FileGroupMeta> fgmList = new ArrayList<FileGroupMeta>();
			fgmList.add(fgm);
			fileService.saveFileGroupMeta(fgmList, fg);
		}
		
		logger.debug("ending doPreFinish() in MacstwoGenerate<ModelAsPdfTasklet");

	}
}
