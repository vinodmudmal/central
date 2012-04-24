package edu.yu.einstein.wasp.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

public class TestWaspModel {
	
	public Sample sample;
	
	@Test
	public void testDeepCopy() {
		Sample sampleCopy = Sample.getDeepCopy(sample);
		
		Assert.assertNull(sampleCopy.getSampleId()); // for deep copy primary Id's should be null
		Assert.assertEquals(sampleCopy.getName(), "s1");
		
		Assert.assertNotNull(sampleCopy.getLab());
		Assert.assertNull(sampleCopy.getLab().getLabId()); // for deep copy primary Id's should be null
		Assert.assertEquals(sampleCopy.getLab().getName(), "LabName");
		
		Assert.assertNotNull(sampleCopy.getSampleMeta());
		Assert.assertEquals(sampleCopy.getSampleMeta().size(), 2);
		Assert.assertNull(sampleCopy.getSampleMeta().get(1).getSampleMetaId());
		Assert.assertNotNull(sampleCopy.getSampleMeta().get(1).getSampleId()); // for deep copy primary Id's should be null
		Assert.assertEquals(sampleCopy.getSampleMeta().get(1).getSampleId().intValue(), 1);
		Assert.assertEquals(sampleCopy.getSampleMeta().get(0).getK(), "meta1Key");
		Assert.assertEquals(sampleCopy.getSampleMeta().get(1).getV(), "meta2Value");
		Assert.assertEquals(sampleCopy.getReceiveDts(), sample.getReceiveDts());
	}
	
	@Test
	public void testShallowCopy() {
		Sample sampleCopy = Sample.getShallowCopy(sample);
		
		Assert.assertNotNull(sampleCopy.getSampleId()); 
		Assert.assertEquals(sampleCopy.getSampleId().intValue(), 1);
		Assert.assertEquals(sampleCopy.getName(), "s1");
		
		Assert.assertNotNull(sampleCopy.getLab());
		Assert.assertNotNull(sampleCopy.getLab().getLabId()); 
		Assert.assertEquals(sampleCopy.getLab().getLabId().intValue(), 10); 
		Assert.assertEquals(sampleCopy.getLab().getName(), "LabName");
		
		Assert.assertNotNull(sampleCopy.getSampleMeta());
		Assert.assertEquals(sampleCopy.getSampleMeta().size(), 2);
		Assert.assertNotNull(sampleCopy.getSampleMeta().get(1).getSampleMetaId());
		Assert.assertEquals(sampleCopy.getSampleMeta().get(1).getSampleMetaId().intValue(), 21);
		Assert.assertNotNull(sampleCopy.getSampleMeta().get(1).getSampleId()); 
		Assert.assertEquals(sampleCopy.getSampleMeta().get(1).getSampleId().intValue(), 1);
		Assert.assertEquals(sampleCopy.getSampleMeta().get(0).getK(), "meta1Key");
		Assert.assertEquals(sampleCopy.getSampleMeta().get(1).getV(), "meta2Value");
		Assert.assertEquals(sampleCopy.getReceiveDts(), sample.getReceiveDts());
	}

	@BeforeMethod
	public void beforeTest() {
		sample = new Sample();
		sample.setSampleId(1);
		Lab lab = new Lab();
		lab.setLabId(10);
		lab.setName("LabName");
		sample.setLab(lab);
		sample.setName("s1");
		List<SampleMeta> sampleMeta = new ArrayList<SampleMeta>();
		SampleMeta sm1 = new SampleMeta();
		sm1.setSampleMetaId(20);
		sm1.setSampleId(1);
		sm1.setK("meta1Key");
		sm1.setV("meta1Value");
		sampleMeta.add(sm1);
		SampleMeta sm2 = new SampleMeta();
		sm2.setSampleMetaId(21);
		sm2.setSampleId(1);
		sm2.setK("meta2Key");
		sm2.setV("meta2Value");
		sampleMeta.add(sm2);
		sample.setSampleMeta(sampleMeta);
		sample.setReceiveDts(new Date(java.lang.System.currentTimeMillis()));
	}

}
