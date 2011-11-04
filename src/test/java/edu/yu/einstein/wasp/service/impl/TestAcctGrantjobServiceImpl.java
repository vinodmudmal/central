package edu.yu.einstein.wasp.service.impl;

import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.annotations.*;

import edu.yu.einstein.wasp.model.AcctGrantjob;
import static org.easymock.EasyMock.*;
import edu.yu.einstein.wasp.dao.AcctGrantjobDao;



public class TestAcctGrantjobServiceImpl {
	private AcctGrantjobServiceImpl acctGrantjobServiceImpl = new AcctGrantjobServiceImpl();
	private AcctGrantjobDao mockAcctGrantjobDao;
	
	
	@BeforeClass
	public void setUp() throws Exception {
		
		mockAcctGrantjobDao = createMock(AcctGrantjobDao.class);
		Assert.assertNotNull(mockAcctGrantjobDao);
		acctGrantjobServiceImpl.setAcctGrantjobDao(mockAcctGrantjobDao);
	}

	@AfterClass
	public void tearDown() throws Exception {
		acctGrantjobServiceImpl = null;
		mockAcctGrantjobDao = null;
	}

	@Test (groups = "unit-tests")
	public void testGetAcctGrantByGrantId() {
		AcctGrantjob results = new AcctGrantjob();
			
		expect(mockAcctGrantjobDao.getAcctGrantjobByJobId(1)).andReturn(results);
		replay(mockAcctGrantjobDao);
		acctGrantjobServiceImpl.getAcctGrantjobByJobId(1);
		//Verifies that the specified behavior has been used: acctGrantServiceImpl.getAcctGrantByGrantId(1)
		verify(mockAcctGrantjobDao);
		
		//fail("Not yet implemented");
	}


}
